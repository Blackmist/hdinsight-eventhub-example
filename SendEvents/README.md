Use one of the applications under this directory to send example data to Event Hub.

##Node.js

1. Modify app.js to include your ServiceBus namespace, Event Hub name, and the Shared Access policy and key that is used to write to it.

2. Install modules.

				npm install

3. Run.

				node app.js

		Each time you run this command, ten events with random temperature data are sent to Event Hub.

##.NET

1. Modify the App.config to include the endpoint connection string for the Event Hub SAS used to send messages.

<add key="Microsoft.ServiceBus.ConnectionString" value="Endpoint=sb://[your namespace].servicebus.windows.net;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=[your secret]" />

2. Modify **Program.cs** to include your ServiceBus namespace, Event Hub name, and the Shared Access policy and key that is used to write to it.

3. Run. It will emit events for as long as it is left open.
