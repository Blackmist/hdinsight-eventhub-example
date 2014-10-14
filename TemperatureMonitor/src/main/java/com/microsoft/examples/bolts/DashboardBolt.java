package com.microsoft.examples;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;
import backtype.storm.Config;
import backtype.storm.Constants;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class DashboardBolt extends BaseBasicBolt {
  //Connection and proxy for SignalR hub
  private HubConnection conn;
  private HubProxy proxy;

  //Declare output fields
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    //no stream output - we talk directly to SignalR
  }

  @Override
  public void prepare(Map config, TopologyContext context) {

    // Connect to the DashHub SignalR server
    conn = new HubConnection("http://dashboard.azurewebsites.net/");
    // Create the hub proxy
    proxy = conn.createHubProxy("DashHub");
    // Subscribe to the error event
    conn.error(new ErrorCallback() {
      @Override
      public void onError(Throwable error) {
        error.printStackTrace();
      }
    });
    // Subscribe to the connected event
    conn.connected(new Runnable() {
      @Override
      public void run() {
        System.out.println("CONNECTED");
      }
    });
    // Subscribe to the closed event
    conn.closed(new Runnable() {
      @Override
      public void run() {
        System.out.println("DISCONNECTED");
      }
    });
    // Start the connection
    conn.start()
      .done(new Action<Void>() {
        @Override
        public void run(Void obj) throws Exception {
          System.out.println("Done Connecting!");
        }
    });
  }

  //Process tuples
  @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {
    Gson gson = new Gson();
    try {
      //Get the deviceid and temperature by field name
      int deviceid = tuple.getIntegerByField("deviceid");
      int temperature = tuple.getIntegerByField("temperature");
      //Construct the SignalR message
      SignalRMessage srMessage = new SignalRMessage();
      srMessage.device = deviceid;
      srMessage.temperature = temperature;
      // send it as JSON
      proxy.invoke("send", gson.toJson(srMessage));
    } catch (Exception e) {
       // LOG.error("Bolt execute error: {}", e);
       collector.reportError(e);
    }
  }
}
