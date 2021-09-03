package com.ycu.tang.msbplatform.gateway.utils;

import com.ycu.tang.msbplatform.gateway.thrift.Data;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static int getNowSec(){
    return Calendar.getInstance().get(Calendar.SECOND);
  }
}
