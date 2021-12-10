package com.ycu.tang.msbplatform.service;

import com.ycu.tang.msbplatform.gateway.utils.DateUtils;
import com.ycu.tang.msbplatform.gateway.utils.MapUtils;
import com.ycu.tang.msbplatform.service.costing.Allocation;
import com.ycu.tang.msbplatform.service.costing.CostCalculator;
import com.ycu.tang.msbplatform.service.costing.Department;
import com.ycu.tang.msbplatform.service.costing.ResourceUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CostingService {

  private final String VIEW_TYPE_HOURLY = "hourly";
  private final String VIEW_TYPE_DAILY = "daily";
  private final String VIEW_TYPE_WEEKLY = "weekly";
  private final String VIEW_TYPE_MONTHLY = "monthly";

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  NamedParameterJdbcTemplate npJdbcTemplate;

  public List<Map<String, Object>> getProductionList() {
    String sql = "select * from productions";
    List<Map<String, Object>> sqlResultList = jdbcTemplate.queryForList(sql);
    return sqlResultList;
  }

  @Transactional
  public Integer startLot(Integer planId, Integer prodId, Map<String, Object> lotInfo) {
    String sql = "INSERT INTO lots (prod_plan_id, production_id, start_time, prod_cnt_target) " +
            "VALUES (:prod_plan_id, :production_id, :start_time, :prod_cnt_target) RETURNING id";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    SqlParameterSource namedParameters = new MapSqlParameterSource(
            MapUtils.of(
                    new String[]{"prod_plan_id", "production_id", "start_time", "prod_cnt_target"},
                    new Object[]{
                            planId,
                            prodId,
                            DateUtils.parseDate((String) lotInfo.get("datetime")),
                            lotInfo.get("prod_cnt_target")}));
    int result = npJdbcTemplate.update(sql, namedParameters, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Transactional
  public boolean stopLot(Integer lotId, Map<String, Object> lotInfo) {

    Calendar calendar = DateUtils.parseDate((String) lotInfo.get("datetime"));

    int prodCnt = (int) lotInfo.get("prod_cnt");
    int totalCost = calLotTotalCost(lotId);
    int singleCost = Math.round(totalCost / prodCnt);
    lotInfo.put("total_cost", totalCost);
    lotInfo.put("single_cost", singleCost);
    lotInfo.put("end_time", calendar);
    updateLot(lotId, lotInfo);

    Map<String, Object> prodCostInfo = findLotInfoByLotId(lotId);

    prodCostInfo.put("date", calendar);
    prodCostInfo.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
    prodCostInfo.put("cal_time", calendar);
    prodCostInfo.put("cost",
            calAverageCost((int) prodCostInfo.get("prod_plan_id"), (int) prodCostInfo.get("production_id")));

    upsertProductionCost(prodCostInfo);

    return true;

  }

  private Map<String, Object> findLotInfoByLotId(int lotId) {
    String sql = "select production_id, prod_plan_id from lots WHERE id = :lot_id";
    return npJdbcTemplate.query(
            sql,
            MapUtils.of("lot_id", lotId),
            (resultSet, i) -> MapUtils.of(
                    new String[]{"production_id", "prod_plan_id"},
                    new Object[]{
                            resultSet.getInt("production_id"),
                            resultSet.getInt("prod_plan_id")})).get(0);
  }

  @Transactional
  public void insertLotResources(Integer lotId, List<Map<String, Object>> resourceUsageList) {
    String sql = "INSERT INTO lots_resources (" +
            "lot_id, resource_id, resource_cnt, actual_price) " +
            "VALUES (:lot_id, :resource_id, :resource_cnt, :price)";

    for (Map<String, Object> resourceUsage : resourceUsageList) {
      resourceUsage.put("lot_id", lotId);
      resourceUsage.putIfAbsent("price", null);
    }

    int[] result = npJdbcTemplate.batchUpdate(sql, resourceUsageList.toArray(new Map[0]));
  }

  private int calLotTotalCost(Integer lotId) {
    String resourceUsageSql =
            "SELECT " +
                    " lt.prod_plan_id," +
                    " lt.production_id," +
                    " lr.resource_id," +
                    " lr.resource_cnt," +
                    " lr.actual_price," +
                    " rs.type1 as resource_type1," +
                    " rs.type2 as resource_type2," +
                    " rs.agg_department_id," +
                    " rs.standard_price," +
                    " dep.type as department_type, " +
                    " dep.id as department_id " +
                    "FROM " +
                    " lots as lt " +
                    " inner join lots_resources as lr on lt.id = lr.lot_id" +
                    " inner join resources as rs on lr.resource_id = rs.id" +
                    " left join departments as dep on rs.agg_department_id = dep.id " +
                    "WHERE " +
                    " lt.id = :lot_id";
    List<ResourceUsage> resourceUsageList = npJdbcTemplate.query(
            resourceUsageSql,
            MapUtils.of("lot_id", lotId),
            new ResourceUsage.RowMapper());


    String allocationSql =
            "SELECT " +
                    " a.*, " +
                    " dep.type as department_type " +
                    "FROM " +
                    " allocations as a " +
                    " left join departments as dep on a.to_department = dep.id " +
                    "WHERE " +
                    " prod_plan_id = :prod_plan_id";
    List<Allocation> allocationList = npJdbcTemplate.query(
            allocationSql,
            MapUtils.of("prod_plan_id", resourceUsageList.get(0).getProdPlanId()),
            new Allocation.RowMapper());

    String departmentSql =
            "SELECT * FROM departments";
    List<Department> departmentList = npJdbcTemplate.query(departmentSql, new Department.RowMapper());

    return CostCalculator.calTotalCost(
            resourceUsageList,
            allocationList,
            resourceUsageList.get(0).getProductionId(),
            departmentList);
  }

  private Integer calAverageCost(Integer planId, Integer lotId) {
    String sql = "SELECT SUM(prod_cnt) as prod_cnt, SUM(total_cost) as total_cost " +
            "FROM lots WHERE prod_plan_id = :prod_plan_id AND production_id = :production_id";

    Map<String, Object> result = npJdbcTemplate.query(
            sql,
            MapUtils.of(new String[]{"prod_plan_id", "production_id"}, new Object[]{planId, lotId}),
            (resultSet, i) -> MapUtils.of(
                    new String[]{"prod_cnt", "total_cost"},
                    new Object[]{
                            resultSet.getInt("prod_cnt"),
                            resultSet.getLong("total_cost")})).get(0);

    return Math.round((Long) result.get("total_cost") / (Integer) result.get("prod_cnt"));
  }

  private void upsertProductionCost(Map<String, Object> prodCostInfo) {
    String sql = "INSERT INTO production_cost (" +
            "prod_plan_id, production_id, date, hour, cal_time, cost) " +
            "VALUES (:prod_plan_id, :production_id, :date, :hour, :cal_time, :cost) " +
            "ON CONFLICT ON CONSTRAINT pk " +
            "DO UPDATE SET cost = :cost, cal_time = :cal_time";
    npJdbcTemplate.update(sql, prodCostInfo);

  }

  private void updateLot(Integer lotId, Map<String, Object> lotInfo) {
    String sql = "UPDATE lots SET " +
            "prod_cnt = :prod_cnt, " +
            "end_time = :end_time, " +
            "total_cost = :total_cost, " +
            "single_cost = :single_cost " +
            "WHERE id = :lot_Id";

    lotInfo.put("lot_Id", lotId);
    SqlParameterSource namedParameters = new MapSqlParameterSource(lotInfo);
    int result = npJdbcTemplate.update(sql, namedParameters);
  }

  public List<Map<String, Object>> getProdRecipes(int prodId) {
    String sql = "SELECT re.resource_id, rs.name AS resource_name, re.cnt AS resource_cnt,rs.standard_price  AS price\n" +
            "FROM recipes AS re INNER JOIN resources AS rs ON re.resource_id = rs.id\n" +
            "WHERE re.production_id = :prod_id";
    return npJdbcTemplate.query(
            sql,
            MapUtils.of("prod_id", prodId),
            (resultSet, i) -> MapUtils.of(
                    new String[]{"resource_id", "resource_name", "resource_cnt", "price"},
                    new Object[]{
                            resultSet.getInt("resource_id"),
                            resultSet.getString("resource_name"),
                            resultSet.getInt("resource_cnt"),
                            resultSet.getDouble("price"),
                    }));
  }

  public Map<String, Double[]> getProdPrice(Integer planId, Integer prodId, String viewType, String startDate, String endDate) {
    String sql = "";

    Map<String, Object> paramMap = MapUtils.of(
            new String[]{"production_id", "prod_plan_id", "start_date", "end_date"},
            new Object[]{prodId, planId, startDate + " 00:00:00", endDate + " 00:00:00"});

    switch (viewType) {
      case VIEW_TYPE_HOURLY:
        sql = getHourlySql();
        break;
      case VIEW_TYPE_DAILY:
        sql = getDailySql();
        break;
      case VIEW_TYPE_WEEKLY:
        sql = getWeeklySql();
        break;
      case VIEW_TYPE_MONTHLY:
        sql = getMonthlySql();
        break;
      default:
        return null;
    }

    List<Map<String, Object>> queryResult = npJdbcTemplate.query(
            sql,
            paramMap,
            (resultSet, i) -> MapUtils.of(
                    new String[]{"from_date", "type", "cost", "cal_time"},
                    new Object[]{
                            resultSet.getString("from_date"),
                            resultSet.getInt("type"),
                            resultSet.getDouble("cost"),
                            resultSet.getString("cal_time"),
                    }));

    LinkedHashMap<String, Double[]> result = new LinkedHashMap();

    String previousKey = null;
    for (Map<String, Object> record : queryResult) {

      String fromDate = (String) record.get("from_date");

      Double[] defaultCosts = null;

      if (previousKey != null) {
        defaultCosts = result.get(previousKey).clone();
      } else {
        defaultCosts = new Double[]{0D, 0D};
      }

      Double[] currentCosts = result.getOrDefault(fromDate, defaultCosts);

      // costTypeが計画原価（２）か実績原価（１）かを区別する。0の場合、NULLとする
      int costType = (int) record.get("type");
      Double cost = (Double) record.get("cost");
      if (costType != 0) {
        currentCosts[costType - 1] = cost;
      }

      result.put(fromDate, currentCosts);

      if (previousKey == null || !previousKey.equals(fromDate)) {
        previousKey = fromDate;
      }
    }

    return result;
  }

  private String getHourlySql() {
    return "SELECT  " +
            "  sub.from_date,  " +
            "  sub.type,  " +
            "  sub.cost,  " +
            "  sub.from_datetime, " +
            "  sub.cal_time  " +
            "FROM ( " +
            "  SELECT  " +
            "    from_date  as from_datetime," +
            "    from_date, " +
            "    type,  " +
            "    cost, " +
            "    cal_time, " +
            "     ROW_NUMBER() OVER (PARTITION BY from_date,type ORDER BY pc.cal_time DESC) as row_num, " +
            "     extract(hour from from_date) as fhour " +
            "  FROM " +
            "    (SELECT generate_series AS from_date, " +
            "        generate_series + '1 hour'::interval AS to_date " +
            "     FROM generate_series(:start_date ::TIMESTAMP, :end_date ::TIMESTAMP, '1 hour')) AS time_ranges " +
            "  LEFT JOIN production_cost as pc " +
            "    ON pc.cal_time BETWEEN from_date AND to_date AND pc.production_id = :production_id AND pc.prod_plan_id=:prod_plan_id " +
            "  ORDER BY from_date ASC " +
            ") as sub WHERE sub.fhour BETWEEN 8 AND 18 AND row_num = 1 AND from_datetime <= current_timestamp";
  }

  private String getDailySql() {
    return "SELECT  " +
            "  sub.from_date,  " +
            "  sub.type,  " +
            "  sub.cost,  " +
            "  sub.from_datetime, " +
            "  sub.cal_time  " +
            "FROM ( " +
            "  SELECT  " +
            "    from_date  as from_datetime," +
            "    to_char(from_date, 'YYYY-MM-dd') as from_date, " +
            "    type,  " +
            "    cost, " +
            "    cal_time, " +
            "     ROW_NUMBER() OVER (PARTITION BY from_date,type ORDER BY pc.cal_time DESC) as row_num" +
            "  FROM " +
            "    (SELECT generate_series AS from_date, " +
            "        generate_series + '1 day'::interval AS to_date " +
            "     FROM generate_series(:start_date ::TIMESTAMP, :end_date ::TIMESTAMP, '1 day')) AS time_ranges " +
            "  LEFT JOIN production_cost as pc " +
            "    ON pc.cal_time BETWEEN from_date AND to_date AND pc.production_id = :production_id AND pc.prod_plan_id=:prod_plan_id " +
            "  ORDER BY from_date ASC " +
            ") as sub WHERE row_num = 1 AND from_datetime <= current_timestamp";
  }

  private String getWeeklySql() {
    return "SELECT  " +
            "  sub.from_date,  " +
            "  sub.type,  " +
            "  sub.cost,  " +
            "  sub.from_datetime, " +
            "  sub.cal_time  " +
            "FROM ( " +
            "  SELECT  " +
            "    from_date  as from_datetime," +
            "    to_char(from_date, 'YYYY-MM-dd') as from_date, " +
            "    type,  " +
            "    cost, " +
            "    cal_time, " +
            "     ROW_NUMBER() OVER (PARTITION BY from_date,type ORDER BY pc.cal_time DESC) as row_num" +
            "  FROM " +
            "    (SELECT generate_series AS from_date, " +
            "        generate_series + '7 day'::interval AS to_date " +
            "     FROM generate_series(:start_date ::TIMESTAMP, :end_date ::TIMESTAMP, '7 day')) AS time_ranges " +
            "  LEFT JOIN production_cost as pc " +
            "    ON pc.cal_time BETWEEN from_date AND to_date AND pc.production_id = :production_id AND pc.prod_plan_id=:prod_plan_id " +
            "  ORDER BY from_date ASC " +
            ") as sub WHERE row_num = 1 AND from_datetime <= current_timestamp";
  }

  private String getMonthlySql() {
    return "SELECT  " +
            "  sub.from_date,  " +
            "  sub.type,  " +
            "  sub.cost,  " +
            "  sub.from_datetime, " +
            "  sub.cal_time  " +
            "FROM ( " +
            "  SELECT  " +
            "    from_date  as from_datetime," +
            "    to_char(from_date, 'YYYY-MM-dd') as from_date, " +
            "    type,  " +
            "    cost, " +
            "    cal_time, " +
            "     ROW_NUMBER() OVER (PARTITION BY from_date,type ORDER BY pc.cal_time DESC) as row_num, " +
            "     extract(hour from from_date) as fhour " +
            "  FROM " +
            "    (SELECT generate_series AS from_date, " +
            "        generate_series + '1 month'::interval AS to_date " +
            "     FROM generate_series(:start_date ::TIMESTAMP, :end_date ::TIMESTAMP, '1 month')) AS time_ranges " +
            "  LEFT JOIN production_cost as pc " +
            "    ON pc.cal_time BETWEEN from_date AND to_date AND pc.production_id = :production_id AND pc.prod_plan_id=:prod_plan_id " +
            "  ORDER BY from_date ASC " +
            ") as sub WHERE row_num = 1 AND from_datetime <= current_timestamp";
  }
}
