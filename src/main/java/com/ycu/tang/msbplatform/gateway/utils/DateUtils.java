package com.ycu.tang.msbplatform.gateway.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static int getNowSec() {
    return Calendar.getInstance().get(Calendar.SECOND);
  }

  public static Object[] getDateAndHour() {
    Calendar current = Calendar.getInstance();
    int hour = current.get(Calendar.HOUR_OF_DAY);
    String dateStr = getDateString(current);
    return new Object[]{dateStr, hour, current};
  }

  public static Object[] getDateAndHour(Calendar calendar) {
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    String dateStr = getDateString(calendar);
    return new Object[]{dateStr, hour, calendar};
  }

  public static Calendar parseDate(String strDate) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = sdf.parse(strDate);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  //カレンダーを年月日の文字列で取得
  public static String getDateString(Calendar tmpCal) {
    return tmpCal.get(Calendar.YEAR) + "/" + tmpCal.get(Calendar.MONTH) + "/" + tmpCal.get(Calendar.DATE);
  }
}
