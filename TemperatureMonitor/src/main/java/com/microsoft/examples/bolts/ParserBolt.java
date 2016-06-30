package com.microsoft.examples;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;

public class ParserBolt extends BaseBasicBolt {
  private static final Logger LOG = LoggerFactory.getLogger(ParserBolt.class);

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
    //Should only be one tuple, which is the JSON message from the spout
    String value = tuple.getString(0);
    LOG.info("Read tuple {} from stream.", value);
    //Deal with cases where we get multiple
    //EventHub messages in one tuple
    String[] arr = value.split("}");
    for (String ehm : arr)
    {
        //Convert it from JSON to an object
        //EventHubMessage msg = new Gson().fromJson(ehm.concat("}"),EventHubMessage.class);
        JSONObject msg=new JSONObject(ehm.concat("}"));
        //Pull out the values and emit as a stream
        String timestamp = msg.getString("TimeStamp");
        int deviceid = msg.getInt("DeviceId");
        int temperature = msg.getInt("Temperature");
        LOG.info("Emitting device id {} with a temperature of {} and timestamp of {}", deviceid, timestamp, temperature);

        collector.emit("hbasestream", new Values(timestamp, deviceid, temperature));
        collector.emit("dashstream", new Values(deviceid, temperature));
    }
  }
}
