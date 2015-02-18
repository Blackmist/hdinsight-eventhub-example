using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.ServiceBus.Messaging;
using Newtonsoft.Json;
using Microsoft.ServiceBus;
using System.Threading;

namespace SendEvents
{
    class Program
    {

        static int numberOfDevices = 10;
        static string eventHubName = "temperature";
        static string eventHubNamespace = "event hub namespace";
        static string sharedAccessPolicyName = "devices";
        static string sharedAccessPolicyKey = "key for 'devices' policy";

        static void Main(string[] args)
        {
            var settings = new MessagingFactorySettings()
            {
                TokenProvider = TokenProvider.CreateSharedAccessSignatureTokenProvider(sharedAccessPolicyName, sharedAccessPolicyKey),
                TransportType = TransportType.Amqp
            };
            var factory = MessagingFactory.Create(
                 ServiceBusEnvironment.CreateServiceUri("sb", eventHubNamespace, ""), settings);

            EventHubClient client = factory.CreateEventHubClient(eventHubName);
 
            try
            {

                List<Task> tasks = new List<Task>();
                // Send messages to Event Hub
                Console.WriteLine("Sending messages to Event Hub {0}", client.Path);
                Random random = new Random();
                //for (int i = 0; i < numberOfMessages; ++i)
                while(!Console.KeyAvailable)
                {
                    // One event per device
                    for(int devices = 0; devices < numberOfDevices; devices++)
                    {
                        // Create the device/temperature metric
                        Event info = new Event() { 
                            TimeStamp = DateTime.UtcNow,
                            DeviceId = random.Next(numberOfDevices),
                            Temperature = random.Next(100)
                        };
                        // Serialize to JSON
                        var serializedString = JsonConvert.SerializeObject(info);
                        Console.WriteLine(serializedString);
                        EventData data = new EventData(Encoding.UTF8.GetBytes(serializedString))
                        {
                            PartitionKey = info.DeviceId.ToString()
                        };

                        // Send the metric to Event Hub
                        tasks.Add(client.SendAsync(data));
                    }
                    // Sleep a second
                    Thread.Sleep(1000);
                };

                Task.WaitAll(tasks.ToArray());
            }
            catch (Exception exp)
            {
                Console.WriteLine("Error on send: " + exp.Message);
            }

        }
    }
}
