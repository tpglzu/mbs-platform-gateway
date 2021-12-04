package com.ycu.tang.msbplatform.service;

import com.ycu.tang.msbplatform.gateway.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Component
public class CostingService {
  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  NamedParameterJdbcTemplate npJdbcTemplate;

  public List<Map<String, Object>> getProductionList(){
    String sql = "select * from productions";
    List<Map<String, Object>> sqlResultList = jdbcTemplate.queryForList(sql);
    return sqlResultList;
  }

  @Transactional
  public Integer startLot(Integer planId, Integer prodId){
    String sql = "INSERT INTO lots (prod_plan_id, production_id, start_time) VALUES (:prod_plan_id, :production_id, current_timestamp) RETURNING id";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    SqlParameterSource namedParameters = new MapSqlParameterSource(
            MapUtils.of(
                    new String[]{"prod_plan_id", "production_id"},
                    new Object[]{planId, prodId}));
    int result = npJdbcTemplate.update(sql,namedParameters, keyHolder);
    return keyHolder.getKey().intValue();
  }

  @Transactional
  public boolean stopLot(Integer lotId, Map<String, Object> lotInfo){
    String sql = "UPDATE lots SET " +
            "prod_cnt = :prod_cnt, " +
            "end_time = current_timestamp " +
            "WHERE id = :lotId";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    SqlParameterSource namedParameters = new MapSqlParameterSource(
            MapUtils.of(
                    new String[]{"prod_cnt", "lotId"},
                    new Object[]{lotInfo.get("prod_cnt"), lotId}));
    int result = npJdbcTemplate.update(sql,namedParameters, keyHolder);
    return result == 1;
  }

  @Transactional
  public void insertLotResources(Integer lotId, List<Map<String, Object>> resourceUsageList){
    String sql = "INSERT INTO lots_resources (" +
            "lot_id, resource_id, resource_cnt, actual_price) " +
            "VALUES (:lot_id, :resource_id, :resource_cnt, :actual_price)";

    for (Map<String, Object> resourceUsage: resourceUsageList) {
      resourceUsage.put("lot_id", lotId);
    }

    int[] result = npJdbcTemplate.batchUpdate(sql, (Map<String, ?>[]) resourceUsageList.toArray(new Map[0]));
  }
}
