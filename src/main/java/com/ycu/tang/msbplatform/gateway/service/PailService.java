package com.ycu.tang.msbplatform.gateway.service;

import com.backtype.hadoop.pail.Pail;
import com.backtype.support.Utils;
import com.ycu.tang.msbplatform.gateway.Properties;
import com.ycu.tang.msbplatform.gateway.service.pailstructure.SplitDataPailStructure;
import com.ycu.tang.msbplatform.gateway.thrift.Data;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PailService {

  @Autowired
  protected Properties properties;

  private Configuration hadoopConf;

  public void writeData(String pailName, Data data) throws IOException {
    Pail pail = null;
    if(!isPailExists(pailName)){
      pail = createPail(pailName);
    }else{
      pail = new Pail(pailName, getHadoopConf());
    }
    Pail.TypedRecordOutputStream out = pail.openWrite();
    out.writeObject(data);
    out.close();
  }

  public final void writeData(String pailName, List<Data> data) throws IOException {
    Pail pail = null;
    if(!isPailExists(pailName)){
      pail = createPail(pailName);
    }else{
      pail = new Pail(pailName, getHadoopConf());
    }
    Pail.TypedRecordOutputStream out = pail.openWrite();
    out.writeObjects(data.toArray());
    out.close();
  }

  public Pail createPail(String pailName) throws IOException {
    FileSystem fs = new Path(pailName).getFileSystem(getHadoopConf());
    return Pail.create(fs, pailName,new SplitDataPailStructure());
  }

  public boolean isPailExists(String pailName) throws IOException {
    return Utils.getFS(pailName, getHadoopConf()).exists(new Path(pailName));
  }

  public Configuration getHadoopConf(){
    if(hadoopConf == null){
      hadoopConf = new Configuration();
      hadoopConf.set("fs.default.name", properties.getNamenodeUrl());
    }
    return hadoopConf;
  }
}
