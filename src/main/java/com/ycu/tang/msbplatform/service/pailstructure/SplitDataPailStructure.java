package com.ycu.tang.msbplatform.service.pailstructure;

import com.ycu.tang.msbplatform.gateway.thrift.Data;
import com.ycu.tang.msbplatform.gateway.thrift.DataUnit;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TUnion;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;

import java.util.*;

public class SplitDataPailStructure extends DataPailStructure {
  public static HashMap<Short, FieldStructure> validFieldMap = new HashMap<>();

  static {
    for (DataUnit._Fields k : DataUnit.metaDataMap.keySet()) {
      FieldValueMetaData md = DataUnit.metaDataMap.get(k).valueMetaData;
      FieldStructure fieldStruct;
      if ((md instanceof StructMetaData) && ((StructMetaData) md).structClass.getName().endsWith("Property")) {
        fieldStruct = new PropertyStructure(((StructMetaData) md).structClass);
      } else {
        fieldStruct = new EdgeStructure();
      }
      validFieldMap.put(k.getThriftFieldId(), fieldStruct);
    }
  }

  @Override
  public List<String> getTarget(Data object) {
    List<String> ret = new ArrayList<>();
    DataUnit du = object.getDataunit();
    short id = du.getSetField().getThriftFieldId();
    ret.add("" + id);
    validFieldMap.get(id).fillTarget(ret, du.getFieldValue());
    return ret;
  }

  @Override
  public boolean isValidTarget(String... dirs) {
    if (dirs.length == 0) return false;
    try {
      short id = Short.parseShort(dirs[0]);
      FieldStructure s = validFieldMap.get(id);
      if (s == null) return false;
      return s.isValidTarget(dirs);
    } catch (NumberFormatException e) {
      return false;
    }
  }

  protected static interface FieldStructure {
    public boolean isValidTarget(String[] dirs);

    public void fillTarget(List<String> ret, Object val);
  }

  protected static class EdgeStructure implements FieldStructure {

    @Override
    public boolean isValidTarget(String[] dirs) {
      return true;
    }

    @Override
    public void fillTarget(List<String> ret, Object val) {

    }
  }

  protected static class PropertyStructure implements FieldStructure {

    private TFieldIdEnum valueId;
    private HashSet<Short> validIds;

    public PropertyStructure(Class prop) {
      try {
        Map<TFieldIdEnum, FieldMetaData> propMeta = getMetadataMap(prop);

        Class valClass = Class.forName(prop.getName() + "Value");
        valueId = getIdForClass(propMeta, valClass);

        validIds = new HashSet<>();
        Map<TFieldIdEnum, FieldMetaData> valueMeta = getMetadataMap(valClass);

        for (TFieldIdEnum valId : valueMeta.keySet()) {
          validIds.add(valId.getThriftFieldId());
        }

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public boolean isValidTarget(String[] dirs) {
      if (dirs.length < 2) return false;
      try {
        short s = Short.parseShort(dirs[1]);
        return validIds.contains(s);
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public void fillTarget(List<String> ret, Object val) {
      ret.add("" + ((TUnion) ((TBase) val).getFieldValue(valueId)).getSetField().getThriftFieldId());
    }

    private static Map<TFieldIdEnum, FieldMetaData> getMetadataMap(Class c) {
      try {
        Object o = c.newInstance();
        return (Map) c.getField("metaDataMap").get(0);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private static TFieldIdEnum getIdForClass(Map<TFieldIdEnum, FieldMetaData> meta, Class toFind) {
      for (TFieldIdEnum k : meta.keySet()) {
        FieldValueMetaData md = meta.get(k).valueMetaData;
        if (md instanceof StructMetaData) {
          if (toFind.equals(((StructMetaData) md).structClass)) {
            return k;
          }
        }
      }

      throw new RuntimeException("Could not find " + toFind.toString() + " in " + meta.toString());
    }
  }
}
