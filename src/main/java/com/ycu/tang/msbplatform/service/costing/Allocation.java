package com.ycu.tang.msbplatform.service.costing;

import org.ejml.All;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Allocation {
  private int formDepartment;
  private int toDepartment;
  private int toProduction;
  private double ratio;
  private int prodPlanId;
  private int resourceType2;
  private int departmentType;

  public int getFormDepartment() {
    return formDepartment;
  }

  public Allocation setFormDepartment(int formDepartment) {
    this.formDepartment = formDepartment;
    return this;
  }

  public int getToDepartment() {
    return toDepartment;
  }

  public Allocation setToDepartment(int toDepartment) {
    this.toDepartment = toDepartment;
    return this;
  }

  public int getToProduction() {
    return toProduction;
  }

  public Allocation setToProduction(int toProduction) {
    this.toProduction = toProduction;
    return this;
  }

  public double getRatio() {
    return ratio;
  }

  public Allocation setRatio(double ratio) {
    this.ratio = ratio;
    return this;
  }

  public int getProdPlanId() {
    return prodPlanId;
  }

  public Allocation setProdPlanId(int prodPlanId) {
    this.prodPlanId = prodPlanId;
    return this;
  }

  public int getResourceType2() {
    return resourceType2;
  }

  public Allocation setResourceType2(int resourceType2) {
    this.resourceType2 = resourceType2;
    return this;
  }

  public int getDepartmentType() {
    return departmentType;
  }

  public Allocation setDepartmentType(int departmentType) {
    this.departmentType = departmentType;
    return this;
  }

  public static class RowMapper implements org.springframework.jdbc.core.RowMapper<Allocation>{

    @Override
    public Allocation mapRow(ResultSet resultSet, int i) throws SQLException {
      return new Allocation()
              .setFormDepartment(resultSet.getInt("from_department"))
              .setToDepartment(resultSet.getInt("to_department"))
              .setToProduction(resultSet.getInt("to_production"))
              .setRatio(resultSet.getDouble("ratio"))
              .setProdPlanId(resultSet.getInt("prod_plan_id"))
              .setResourceType2(resultSet.getInt("resource_type2"))
              .setDepartmentType(resultSet.getInt("department_type"));
    }
  }
}
