package com.microsoft.examples;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ParserBolt extends BaseBasicBolt {

  //Declare output fields & streams
  //hbasestream is all fields, and goes to hbase
  //dashstream is just the device and temperature, and goes to the dashboard
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declareStream("hbasestream", new Fields("timestamp", "deviceid", "temperature"));
    declarer.declareStream("dashstream", new Fields("deviceid", "temperature"));
  }

  //Process tuples
  @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {
    Gson gson = new Gson();
    //Should only be one tuple, which is the JSON message from the spout
    String value = tuple.getString(0);

    //Deal with cases where we get multiple
    //EventHub messages in one tuple
    String[] arr = value.split("}");
    for (String ehm : arr)
    {
        //Convert it from JSON to an object
        EventHubMessage msg = new Gson().fromJson(ehm.concat("}"),EventHubMessage.class);

        //Pull out the values and emit as a stream
        String timestamp = msg.TimeStamp;
        int deviceid = msg.DeviceId;
        int temperature = msg.Temperature;
        collector.emit("hbasestream", new Values(timestamp, deviceid, temperature));
        collector.emit("dashstream", new Values(deviceid, temperature));
    }
  }
}
