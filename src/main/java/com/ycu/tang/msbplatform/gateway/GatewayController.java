package com.ycu.tang.msbplatform.gateway;

import com.ycu.tang.msbplatform.gateway.thrift.*;
import com.ycu.tang.msbplatform.gateway.utils.DateUtils;
import com.ycu.tang.msbplatform.service.KafkaService;
import com.ycu.tang.msbplatform.service.PailService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class GatewayController {
  Logger logger = LoggerFactory.getLogger(GatewayController.class);

  @Autowired
  protected Properties properties;

  @Autowired
  protected PailService pailService;

  @Autowired
  protected KafkaService kafkaService;

  @PostMapping("/event/page-view")
  void pageView(@RequestBody Map<String, Object> payload) throws IOException, TException {
    String cookie = (String) payload.get("cookie");
    Integer userId = (Integer) payload.get("user_id");
    String pageId = (String) payload.get("pageId");
    Integer nonce = new Random().nextInt();

    PersonID personIdObj = null;
    if (cookie != null) {
      personIdObj = PersonID.cookie(cookie);
    } else if (userId != null) {
      personIdObj = PersonID.user_id(userId);
    }

    DataUnit dataUnit = new DataUnit();
    dataUnit.setPage_view(new PageViewEdge(personIdObj, PageID.url(pageId), nonce));
    Data data = new Data(new Pedigree(DateUtils.getNowSec()), dataUnit);

    pailService.writeData(properties.getNewRoot(), data);
    kafkaService.send(properties.getKafkaTopicPageViews(), data);
  }

  @PostMapping("/event/person-property")
  void personProperty(@RequestBody Map<String, Object> payload) throws IOException, TException {
    String cookie = (String) payload.get("cookie");
    Integer userId = (Integer) payload.get("user_id");
    String name = (String) payload.get("name");
    String gender = (String) payload.get("gender");
    String location_city = (String) payload.get("location_city");
    String location_state = (String) payload.get("location_state");
    String location_country = (String) payload.get("location_country");

    PersonID personIdObj = null;
    if (cookie != null) {
      personIdObj = PersonID.cookie(cookie);
    } else if (userId != null) {
      personIdObj = PersonID.user_id(userId);
    }

    List<Data> dataList = new ArrayList<>();
    if (name != null) {
      dataList.add(
              new Data(
                      new Pedigree(DateUtils.getNowSec()),
                      DataUnit.person_property(
                              new PersonProperty(personIdObj, PersonPropertyValue.full_name(name))
                      )
              ));
    }

    if(gender != null){
      dataList.add(
              new Data(
                      new Pedigree(DateUtils.getNowSec()),
                      DataUnit.person_property(
                              new PersonProperty(personIdObj, PersonPropertyValue.gender(GenderType.valueOf(gender)))
                      )
              ));
    }

    if(location_city != null || location_state != null || location_country != null){
      Location location = new Location();
      location.setCity(location_city);
      location.setState(location_state);
      location.setCountry(location_country);
      dataList.add(
              new Data(
                      new Pedigree(DateUtils.getNowSec()),
                      DataUnit.person_property(
                              new PersonProperty(personIdObj, PersonPropertyValue.location(location))
                      )
              ));
    }

    pailService.writeData(properties.getNewRoot(), dataList);

    kafkaService.send(properties.getKafkaTopicPersonProperty(), dataList);
  }

  @PostMapping("/event/page_property")
  void pageProperty(@RequestBody Map<String, Object> payload) {

  }

  @PostMapping("/event/person_equiv")
  void personEquiv(@RequestBody Map<String, Object> payload) throws IOException, TException {
    Integer userId1 = (Integer) payload.get("user_id_1");
    Integer userId2 = (Integer) payload.get("user_id_2");

    EquivEdge equivEdge = new EquivEdge(PersonID.user_id(userId1), PersonID.user_id(userId2));
    DataUnit dataUnit = DataUnit.equiv(equivEdge);

    Data data = new Data(new Pedigree(DateUtils.getNowSec()),dataUnit);

    pailService.writeData(properties.getNewRoot(), data );
    kafkaService.send(properties.getKafkaTopicPersonEquiv(), data);
  }


  @PreDestroy
  void shutdown(){
    logger.info("do shutdown !!!");
    kafkaService.close();
  }
}
