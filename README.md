hdinsight-eventhub-example
==========================

Example of using HDInsight (Storm) to read events from Event Hub, write events to HBase, and visualize events using SignalR and D3.js

See [http://azure.microsoft.com/en-us/documentation/articles/hdinsight-storm-sensor-data-analysis](http://azure.microsoft.com/en-us/documentation/articles/hdinsight-storm-sensor-data-analysis) for a walkthrough.

* **dashboard** contains an ASP.NET application that hosts SignalR. This is used to communicate between the Storm topology and the D3.js driven dashboard app (index.html in this project.)

* **SendEvents** contains a C# console application that writes events to Azure Event Hub. It simulates 10 devices writing 1 event each every second.

* **TemperatureMonitor** contains a Storm application that reads from Event Hub, writes to HBase and to the dashboard.

  * it requires the eventhub spout supplied with HDInsight Storm clusters
  * it requires the Java SignalR client
  * it requires the storm-hbase bolt
  * it requires gson
  
This also requires Azure Event Hub, and was written/tested with HDInsight Storm and HBase clusters running on the same Azure Virtual network. The website was tested in Azure Websites.

  
