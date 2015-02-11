package com.microsoft.examples;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;
import backtype.storm.Config;
import backtype.storm.Constants;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

public class DashboardBolt extends BaseBasicBolt {
  //Socket.IO
  private Socket socket;
  private static String clientId;
  private static String redirectUri;
  private static String resourceUri;
  private static String authority;
  private static String datasetsUri;


  //Declare output fields
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    //no stream output - we talk directly to SignalR
  }

  @Override
  public void prepare(Map config, TopologyContext context) {
    //using Socket.io
    try {
      //Open a socket to your web server
      socket = IO.socket("http://localhost:3000");
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

    //Create a JSON object
    JSONObject obj = new JSONObject();
    obj.put("deviceid", deviceid);
    obj.put("temperature", temperature);
    //Send it to the server
    socket.emit("message", obj);
  }
}
