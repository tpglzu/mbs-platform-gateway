package com.ycu.tang.msbplatform.gateway.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
  public static <K,V> Map<K,V>  of(K key, V value){
    Map<K, V> map = new HashMap<>();
    map.put(key, value);
    return map;
  }

  public static <K,V> Map<K,V>  of(K[] keys, V[] values){
    Map<K, V> map = new HashMap<>();
    for (int i = 0; i < keys.length; i++){
      map.put(keys[i], values[i]);
    }
    return map;
  }
}
