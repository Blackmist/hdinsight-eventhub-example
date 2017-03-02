package com.microsoft.examples;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.Config;
import org.apache.storm.Constants;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

// For logging
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.net.URISyntaxException;
import java.util.Map;

public class DashboardBolt extends BaseBasicBolt {
  //Socket.IO
  private Socket socket;
  private static String clientId;
  private String dashboardUri;

  private static final Logger LOG = LogManager.getLogger(DashboardBolt.class);

  public DashboardBolt(String dashboardUri) {
      LOG.info("Creating an instance of the DashboardBolt.");
      this.dashboardUri = dashboardUri;
  }
  //Declare output fields
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    //no stream output - we talk directly to socket.io
  }

  @Override
  public void prepare(Map config, TopologyContext context) {
    //using Socket.io
    try {
      //Open a socket to your web server
      socket = IO.socket(this.dashboardUri);
      socket.connect();
    } catch(URISyntaxException e) {
      //Assume we can connect
    }
  }

  //Process tuples
  @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {
    //Get the device ID and temperature
    int deviceid = tuple.getIntegerByField("deviceid");
    int temperature = tuple.getIntegerByField("temperature");

    LOG.info("DeviceID is {}, temperature is {}", deviceid, temperature);
    //Create a JSON object
    JSONObject obj = new JSONObject();
    obj.put("deviceid", deviceid);
    obj.put("temperature", temperature);
    //Send it to the server
    socket.emit("message", obj);
  }
}
