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
      //Assume we can connect for now
    }

    // // Connect to the DashHub SignalR server
    // conn = new HubConnection("http://dashboard.azurewebsites.net/");
    // // Create the hub proxy
    // proxy = conn.createHubProxy("DashHub");
    // // Subscribe to the error event
    // conn.error(new ErrorCallback() {
    //   @Override
    //   public void onError(Throwable error) {
    //     error.printStackTrace();
    //   }
    // });
    // // Subscribe to the connected event
    // conn.connected(new Runnable() {
    //   @Override
    //   public void run() {
    //     System.out.println("CONNECTED");
    //   }
    // });
    // // Subscribe to the closed event
    // conn.closed(new Runnable() {
    //   @Override
    //   public void run() {
    //     System.out.println("DISCONNECTED");
    //   }
    // });
    // // Start the connection
    // conn.start()
    //   .done(new Action<Void>() {
    //     @Override
    //     public void run(Void obj) throws Exception {
    //       System.out.println("Done Connecting!");
    //     }
    // });
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
    // Gson gson = new Gson();
    // try {
    //   //Get the deviceid and temperature by field name
    //   int deviceid = tuple.getIntegerByField("deviceid");
    //   int temperature = tuple.getIntegerByField("temperature");
    //   //Construct the SignalR message
    //   SignalRMessage srMessage = new SignalRMessage();
    //   srMessage.device = deviceid;
    //   srMessage.temperature = temperature;
    //   // send it as JSON
    //   proxy.invoke("send", gson.toJson(srMessage));
    // } catch (Exception e) {
    //    // LOG.error("Bolt execute error: {}", e);
    //    collector.reportError(e);
    // }
  }
}
