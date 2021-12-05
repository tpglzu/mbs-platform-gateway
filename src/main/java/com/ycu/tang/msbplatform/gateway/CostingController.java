package com.ycu.tang.msbplatform.gateway;

import com.ycu.tang.msbplatform.gateway.utils.MapUtils;
import com.ycu.tang.msbplatform.service.CostingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CostingController {
  Logger logger = LoggerFactory.getLogger(CostingController.class);

  @Autowired
  protected Properties properties;

  @Autowired
  protected CostingService costingService;

  @GetMapping("/productions")
  List<Map<String, Object>> getProductionList(){
    return costingService.getProductionList();
  }

  @PostMapping("/plan/{plan_id}/lot/start/{prod_id}")
  Map<String, Integer> startLot(
          @PathVariable("plan_id") Integer planId,
          @PathVariable("prod_id") Integer prodId,
          @RequestParam("datetime") String dateStr){
    Integer id = costingService.startLot(planId, prodId, dateStr);
    return MapUtils.of("lot_id", id);
  }

  @PostMapping("/lot/{lot_id}/resource_usage")
  void processLot(@PathVariable("lot_id") Integer lotId, @RequestBody List<Map<String, Object>> payload){
    costingService.insertLotResources(lotId, payload);
  }

  @PostMapping("/lot/{lot_id}/stop")
  @Transactional
  void stopLot(
          @PathVariable("lot_id") Integer lotId,
          @RequestBody Map<String, Object> payload,
          @RequestParam("datetime") String dateStr){
    boolean result = costingService.stopLot(lotId, payload, dateStr);
  }

}