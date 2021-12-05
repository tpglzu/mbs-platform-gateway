package com.ycu.tang.msbplatform.service.costing;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Department {
  private int id;
  private int type;
  private String name;

  public int getId() {
    return id;
  }

  public Department setId(int id) {
    this.id = id;
    return this;
  }

  public int getType() {
    return type;
  }

  public Department setType(int type) {
    this.type = type;
    return this;
  }

  public String getName() {
    return name;
  }

  public Department setName(String name) {
    this.name = name;
    return this;
  }

  public static class RowMapper implements org.springframework.jdbc.core.RowMapper<Department>{

    @Override
    public Department mapRow(ResultSet resultSet, int i) throws SQLException {
      return new Department()
              .setId(resultSet.getInt("id"))
              .setType(resultSet.getInt("type"))
              .setName(resultSet.getString("name"));
    }
  }
}
