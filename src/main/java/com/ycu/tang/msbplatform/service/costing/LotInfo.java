package com.ycu.tang.msbplatform.service.costing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class LotInfo {
  private Integer id;
  private Integer productionId;
  private Timestamp startTime;
  private Timestamp endTime;
  private Integer prodCnt;
  private Double totalCost;
  private Double singleCost;
  private Integer prodPlanId;
  private Integer prodCntPred;
  private Double totalCostPred;

  public Integer getId() {
    return id;
  }

  public LotInfo setId(Integer id) {
    this.id = id;
    return this;
  }

  public Integer getProductionId() {
    return productionId;
  }

  public LotInfo setProductionId(Integer productionId) {
    this.productionId = productionId;
    return this;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public LotInfo setStartTime(Timestamp startTime) {
    this.startTime = startTime;
    return this;
  }

  public Timestamp getEndTime() {
    return endTime;
  }

  public LotInfo setEndTime(Timestamp endTime) {
    this.endTime = endTime;
    return this;
  }

  public Integer getProdCnt() {
    return prodCnt;
  }

  public LotInfo setProdCnt(Integer prodCnt) {
    this.prodCnt = prodCnt;
    return this;
  }

  public Double getTotalCost() {
    return totalCost;
  }

  public LotInfo setTotalCost(Double totalCost) {
    this.totalCost = totalCost;
    return this;
  }

  public Double getSingleCost() {
    return singleCost;
  }

  public LotInfo setSingleCost(Double singleCost) {
    this.singleCost = singleCost;
    return this;
  }

  public Integer getProdPlanId() {
    return prodPlanId;
  }

  public LotInfo setProdPlanId(Integer prodPlanId) {
    this.prodPlanId = prodPlanId;
    return this;
  }

  public Integer getProdCntPred() {
    return prodCntPred;
  }

  public LotInfo setProdCntPred(Integer prodCntPred) {
    this.prodCntPred = prodCntPred;
    return this;
  }

  public Double getTotalCostPred() {
    return totalCostPred;
  }

  public LotInfo setTotalCostPred(Double totalCostPred) {
    this.totalCostPred = totalCostPred;
    return this;
  }

  public static class RowMapper implements org.springframework.jdbc.core.RowMapper<LotInfo>{

    @Override
    public LotInfo mapRow(ResultSet resultSet, int i) throws SQLException {
      return new LotInfo()
              .setId(resultSet.getInt("id"))
              .setProdPlanId(resultSet.getInt("prod_plan_id"))
              .setProductionId(resultSet.getInt("production_id"))
              .setStartTime(resultSet.getTimestamp("start_time"))
              .setEndTime(resultSet.getTimestamp("end_time"))
              .setProdCnt(resultSet.getInt("prod_cnt"))
              .setTotalCost(resultSet.getDouble("total_cost"))
              .setSingleCost(resultSet.getDouble("single_cost"))
              .setProdCntPred(resultSet.getInt("prod_cnt_pred"))
              .setTotalCostPred(resultSet.getDouble("total_cost_pred"));
    }
  }
}
