package com.microsoft.examples;

import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import org.json.JSONObject;

// For logging
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;

public class ParserBolt extends BaseBasicBolt {
  private static final Logger LOG = LogManager.getLogger(ParserBolt.class);

  //Declare output fields & streams
  //hbasestream is all fields, and goes to hbase
  //The default stream is just the device and temperature, and goes to the dashboard
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declareStream("hbasestream", new Fields("timestamp", "deviceid", "temperature"));
    // Default stream
    declarer.declare(new Fields("deviceid", "temperature"));
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
        collector.emit(new Values(deviceid, temperature));
    }
  }
}
