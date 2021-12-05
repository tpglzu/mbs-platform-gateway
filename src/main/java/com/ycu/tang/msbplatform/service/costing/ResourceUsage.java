package com.ycu.tang.msbplatform.service.costing;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceUsage {
  private Integer prodPlanId;
  private Integer productionId;
  private Integer resourceId;
  private Integer resourceCnt;
  private Integer actualPrice;
  private Integer resourceType1;
  private Integer resourceType2;
  private Integer aggDepartmentId;
  private Integer standardPrice;
  private Integer departmentId;
  private Integer departmentType;

  public int getProdPlanId() {
    return prodPlanId;
  }

  public ResourceUsage setProdPlanId(int prodPlanId) {
    this.prodPlanId = prodPlanId;
    return this;
  }

  public int getProductionId() {
    return productionId;
  }

  public ResourceUsage setProductionId(int productionId) {
    this.productionId = productionId;
    return this;
  }

  public int getResourceId() {
    return resourceId;
  }

  public ResourceUsage setResourceId(int resourceId) {
    this.resourceId = resourceId;
    return this;
  }

  public int getResourceCnt() {
    return resourceCnt;
  }

  public ResourceUsage setResourceCnt(int resourceCnt) {
    this.resourceCnt = resourceCnt;
    return this;
  }

  public int getActualPrice() {
    return actualPrice;
  }

  public ResourceUsage setActualPrice(int actualPrice) {
    this.actualPrice = actualPrice;
    return this;
  }

  public int getResourceType1() {
    return resourceType1;
  }

  public ResourceUsage setResourceType1(int resourceType1) {
    this.resourceType1 = resourceType1;
    return this;
  }

  public int getResourceType2() {
    return resourceType2;
  }

  public ResourceUsage setResourceType2(int resourceType2) {
    this.resourceType2 = resourceType2;
    return this;
  }

  public int getAggDepartmentId() {
    return aggDepartmentId;
  }

  public ResourceUsage setAggDepartmentId(int aggDepartmentId) {
    this.aggDepartmentId = aggDepartmentId;
    return this;
  }

  public int getStandardPrice() {
    return standardPrice;
  }

  public ResourceUsage setStandardPrice(int standardPrice) {
    this.standardPrice = standardPrice;
    return this;
  }

  public int getDepartmentId() {
    return departmentId;
  }

  public ResourceUsage setDepartmentId(int departmentId) {
    this.departmentId = departmentId;
    return this;
  }

  public int calCost(){
    int price = standardPrice;
    if(actualPrice != null && actualPrice != 0)
      price = actualPrice;
    return price * resourceCnt;
  }

  public Integer getDepartmentType() {
    return departmentType;
  }

  public ResourceUsage setDepartmentType(Integer departmentType) {
    this.departmentType = departmentType;
    return this;
  }

  public static class RowMapper implements org.springframework.jdbc.core.RowMapper<ResourceUsage>{

    @Override
    public ResourceUsage mapRow(ResultSet resultSet, int i) throws SQLException {
      return new ResourceUsage()
              .setProdPlanId(resultSet.getInt("prod_plan_id"))
              .setProductionId(resultSet.getInt("production_id"))
              .setResourceId(resultSet.getInt("resource_id"))
              .setResourceCnt(resultSet.getInt("resource_cnt"))
              .setActualPrice(resultSet.getInt("actual_price"))
              .setResourceType1(resultSet.getInt("resource_type1"))
              .setResourceType2(resultSet.getInt("resource_type2"))
              .setAggDepartmentId(resultSet.getInt("agg_department_id"))
              .setStandardPrice(resultSet.getInt("standard_price"))
              .setDepartmentId(resultSet.getInt("department_id"))
              .setDepartmentType(resultSet.getInt("department_type"));
    }
  }
}
