package com.ycu.tang.msbplatform.gateway.utils;

import com.ycu.tang.msbplatform.gateway.thrift.Data;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static int getNowSec(){
    return Calendar.getInstance().get(Calendar.SECOND);
  }

  public static Object[] getNowDateAndHour(){
    Calendar current = Calendar.getInstance();
    int hour = current.get(Calendar.HOUR_OF_DAY);
    String dateStr = getDateString(current);
    return new Object[]{dateStr, hour, current};
  }

  //カレンダーを年月日の文字列で取得
  private static String  getDateString(Calendar tmpCal){
    return tmpCal.get(Calendar.YEAR) + "/" + tmpCal.get(Calendar.MONTH) + "/" + tmpCal.get(Calendar.DATE);

  }
}
