package com.ycu.tang.msbplatform.service.costing;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.ejml.All;
import org.ejml.simple.SimpleMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CostCalculator {

  // 直接費
  public static final int TYPE1_DIRECT = 1;
  // 間接費
  public static final int TYPE1_INDIRECT = 2;
  // 材料費
  public static final int TYPE2_ITEM = 1;
  // 労務費
  public static final int TYPE2_LABOUR = 2;
  // 経費
  public static final int TYPE2_EXPENSE = 3;
  // 製造部門
  public static final int DEP_MANUFACTURING = 1;
  // 補助部門
  public static final int DEP_SUPPORT = 2;
  // 共通部門
  public static final int DEP_COMMON = 3;

  public static int calTotalCost(
          List<ResourceUsage> resourceUsageList,
          List<Allocation> allocationList,
          int productionId,
          List<Department> departmentList) {

    int direct = 0;
    int indirect = 0;

    // 部門別資源区分別に集計(１回目)
    // <部門ID, <資源区分２, コスト>
    // 製造部門
    Map<Integer, Map<Integer, Integer>> departmentMSum1 = new HashMap<>();
    // 補助部門
    Map<Integer, Map<Integer, Integer>> departmentSSum1 = new HashMap<>();
    // 共通部門
    Map<Integer, Map<Integer, Integer>> departmentCSum1 = new HashMap<>();
    for (ResourceUsage resourceUsage : resourceUsageList) {
      if (resourceUsage.getResourceType1() == TYPE1_DIRECT) {
        direct += resourceUsage.calCost();
      } else {
        int depId = resourceUsage.getAggDepartmentId();
        int depType = resourceUsage.getDepartmentType();
        int resourceType2 = resourceUsage.getResourceType2();
        int cost = resourceUsage.calCost();

        switch (depType) {
          case DEP_MANUFACTURING:
            sumToDepartmentType(departmentMSum1, depId, resourceType2, cost);
            break;
          case DEP_SUPPORT:
            sumToDepartmentType(departmentSSum1, depId, resourceType2, cost);
            break;
          case DEP_COMMON:
            sumToDepartmentType(departmentCSum1, depId, resourceType2, cost);
            break;
          default:
            break;
        }
      }
    }

    // 部門別に集計(2回目)
    // 製造部門
    Map<Integer, Integer> departmentMSum2 = new HashMap<>();
    for (Integer depId : departmentMSum1.keySet()) {
      int depTotal = 0;
      Map<Integer, Integer> resType2Sum = departmentMSum1.get(depId);
      for (Integer resType2Id : resType2Sum.keySet()) {
        depTotal += resType2Sum.get(resType2Id);
      }
      departmentMSum2.put(depId, depTotal);
    }

    // 補助部門
    Map<Integer, Integer> departmentSSum2 = new HashMap<>();
    for (Integer depId : departmentSSum1.keySet()) {
      int depTotal = 0;
      Map<Integer, Integer> resType2Sum = departmentSSum1.get(depId);
      for (Integer resType2Id : resType2Sum.keySet()) {
        depTotal += resType2Sum.get(resType2Id);
      }
      departmentSSum2.put(depId, depTotal);
    }

    // 共通部門 -> 原価部門
    for (Integer fromDepId : departmentCSum1.keySet()) {
      Map<Integer, Integer> resType2Sum = departmentCSum1.get(fromDepId);
      for (Integer resType2Id : resType2Sum.keySet()) {
        for (Allocation allocation : allocationList) {
          // 配賦先部門==指定共通部門、さらに配賦対象リソース区分(材料費、労務費、経費)==指定リソース区分の場合
          // 該当部門の費用を配賦先部門に集計する
          if (allocation.getFormDepartment() == fromDepId && allocation.getResourceType2() == resType2Id) {
            Integer toDepId = allocation.getToDepartment();
            Integer toDepType = allocation.getDepartmentType();
            switch (toDepType) {
              case DEP_MANUFACTURING:
                // 製造部門に配賦する
                departmentMSum2.put(toDepId,
                        (int) (resType2Sum.get(resType2Id) * allocation.getRatio())
                                + departmentMSum2.getOrDefault(toDepId, 0));
                break;
              case DEP_SUPPORT:
                // 補助部門に配賦する
                departmentSSum2.put(toDepId,
                        (int) (resType2Sum.get(resType2Id) * allocation.getRatio())
                                + departmentSSum2.getOrDefault(toDepId, 0));
                break;
            }
          }
        }
      }
    }

    // 製造部門に集計(3回目)
    // 製造部門
    Map<Integer, Integer> departmentMSum3 = departmentMSum2;
    // 補助部門
    Map<Integer, Integer> departmentSSum3 = new HashMap<>();

    // 補助部門数
    int depSNum = departmentSSum2.size();
    // B2マトリックス構造
    SimpleMatrix mB2 = new SimpleMatrix(depSNum, depSNum);
    SimpleMatrix mIdentity = SimpleMatrix.identity(depSNum);
    SimpleMatrix mD2 = new SimpleMatrix(1, depSNum);
    List<Integer> depIdSList = departmentSSum2.keySet().stream().sorted().collect(Collectors.toList());
    for (int i = 0; i < depSNum; i++) {
      int fromDepId = depIdSList.get(i);
      for (int j = 0; j < depSNum; j++) {
        int toDepId = depIdSList.get(j);
        Allocation allocation =
                allocationList
                        .stream()
                        .filter(a -> a.getFormDepartment() == fromDepId && a.getToDepartment() == toDepId)
                        .findFirst()
                        .get();
        mB2.set(i, j, allocation.getRatio());
      }
      mD2.set(0, i, departmentSSum2.get(fromDepId));
    }
    // D2' = D2(I-B2)^-1
    SimpleMatrix mD2Dash = mD2.mult(mIdentity.minus(mB2).invert());
    for (int i = 0; i < depSNum; i++) {
      int dep = depIdSList.get(i);
      departmentSSum3.put(dep, (int) mD2Dash.get(0, i));
    }

    for (Integer depSKey : departmentSSum3.keySet()) {
      for (Integer depMKey : departmentMSum3.keySet()) {
        Allocation allocation =
                allocationList
                        .stream()
                        .filter(a -> a.getFormDepartment() == depSKey && a.getToDepartment() == depMKey)
                        .findFirst()
                        .get();
        double ration = allocation.getRatio();
        departmentMSum3.put(depMKey,
                (int) (departmentMSum3.getOrDefault(depMKey, 0) + departmentSSum3.get(depSKey) * ration));
      }
    }

    // 製品に集計(4回目)
    for (Integer depMKey : departmentMSum3.keySet()) {
      Allocation allocation =
              allocationList
                      .stream()
                      .filter(a -> a.getFormDepartment() == depMKey && a.getToProduction() == productionId)
                      .findFirst()
                      .get();
      indirect += departmentMSum3.get(depMKey) * allocation.getRatio();
    }

    return direct + indirect;
  }

  private static void sumToDepartmentType(Map<Integer, Map<Integer, Integer>> departmentMSum1, int depId, int resourceType2, int cost) {
    Map<Integer, Integer> resourceTypeMap = departmentMSum1.get(depId);
    if (resourceTypeMap == null) {
      resourceTypeMap = new HashMap<>();
      departmentMSum1.put(depId, resourceTypeMap);
    }

    resourceTypeMap.put(resourceType2, cost + resourceTypeMap.getOrDefault(resourceType2, 0));
  }

  private static Allocation findAllocation(List<Allocation> list, Integer fromDepId, Integer toDepId, Integer resType2Id){
    return list
            .stream()
            .filter(a ->
                    a.getFormDepartment() == fromDepId &&
                            a.getToDepartment() == toDepId &&
                            a.getResourceType2() == resType2Id)
            .findFirst()
            .get();
  }
}
