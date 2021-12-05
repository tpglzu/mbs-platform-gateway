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
import java.util.List;
import java.util.Map;

@Component
public class CostingService {
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
  public Integer startLot(Integer planId, Integer prodId, String dateStr) {
    String sql = "INSERT INTO lots (prod_plan_id, production_id, start_time) " +
            "VALUES (:prod_plan_id, :production_id, :start_time) RETURNING id";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    SqlParameterSource namedParameters = new MapSqlParameterSource(
            MapUtils.of(
                    new String[]{"prod_plan_id", "production_id", "start_time"},
                    new Object[]{planId, prodId, DateUtils.parseDate(dateStr)}));
    int result = npJdbcTemplate.update(sql, namedParameters, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Transactional
  public boolean stopLot(Integer lotId, Map<String, Object> lotInfo, String dateStr) {

    Calendar calendar = DateUtils.parseDate(dateStr);

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
            calAverageCost((int)prodCostInfo.get("prod_plan_id"), (int)prodCostInfo.get("production_id")));

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
            "VALUES (:lot_id, :resource_id, :resource_cnt, :actual_price)";

    for (Map<String, Object> resourceUsage : resourceUsageList) {
      resourceUsage.put("lot_id", lotId);
      resourceUsage.putIfAbsent("actual_price", null);
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
            " dep.type as department_type, "+
            " dep.id as department_id "+
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
            new Allocation.RowMapper()) ;

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

    return Math.round((Long)result.get("total_cost") / (Integer) result.get("prod_cnt"));
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
}
