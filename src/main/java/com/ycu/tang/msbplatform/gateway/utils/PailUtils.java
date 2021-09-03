package com.ycu.tang.msbplatform.gateway.utils;

import com.backtype.hadoop.pail.Pail;
import com.backtype.support.Utils;
import com.ycu.tang.msbplatform.gateway.pails.SplitDataPailStructure;
import com.ycu.tang.msbplatform.gateway.thrift.Data;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.List;

public class PailUtils {
  public static final void writeData(String pailName, Data data) throws IOException {
    Pail pail = null;
    if(!isPailExists(pailName)){
      pail = createPail(pailName);
    }else{
      pail = new Pail(pailName);
    }
    Pail.TypedRecordOutputStream out = pail.openWrite();
    out.writeObject(data);
    out.close();
  }

  public static final void writeData(String pailName, List<Data> data) throws IOException {
    Pail pail = null;
    if(!isPailExists(pailName)){
      pail = createPail(pailName);
    }else{
      pail = new Pail(pailName);
    }
    Pail.TypedRecordOutputStream out = pail.openWrite();
    out.writeObjects(data.toArray());
    out.close();
  }

  public static Pail  createPail(String pailName) throws IOException {
    return Pail.create(pailName,new SplitDataPailStructure());
  }

  public static boolean isPailExists(String pailName) throws IOException {
    return Utils.getFS(pailName).exists(new Path(pailName));
  }
}
