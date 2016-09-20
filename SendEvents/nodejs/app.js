var https = require('https');
var crypto = require('crypto');
var moment = require('moment');

// ServiceBus Namespace
var namespace = 'YourNamespace';
// Event Hub Name
var hubname ='sensordata';
// Shared access Policy name and key (from Event Hub configuration)
var my_key_name = 'devices';
var my_key = 'YourKey';

// Full URI to send messages to the hub
var my_uri = 'https://' + namespace + '.servicebus.windows.net' + '/' + hubname + '/messages';

// Create a SAS token
// See http://msdn.microsoft.com/library/azure/dn170477.aspx

function create_sas_token(uri, key_name, key)
{
	// Token expires in one hour
	var expiry = moment().add(1, 'hours').unix();

	var string_to_sign = encodeURIComponent(uri) + '\n' + expiry;
	var hmac = crypto.createHmac('sha256', key);
	hmac.update(string_to_sign);
	var signature = hmac.digest('base64');
	var token = 'SharedAccessSignature sr=' + encodeURIComponent(uri) + '&sig=' + encodeURIComponent(signature) + '&se=' + expiry + '&skn=' + key_name;

	return token;
}

function send_message(payload)
{
	// Send the request to the Event Hub
	var options = {
		hostname: namespace + '.servicebus.windows.net',
		port: 443,
		path: '/' + hubname + '/messages',
		method: 'POST',
		headers: {
			'Authorization': my_sas,
			'Content-Length': payload.length,
			'Content-Type': 'application/atom+xml;type=entry;charset=utf-8'
		}
	};

	var req = https.request(options, function(res) {
		//console.log("statusCode: ", res.statusCode);
		//console.log("headers: ", res.headers);

		res.on('data', function(d) {
			process.stdout.write(d);
			});
	});

	req.on('error', function(e) {
		console.error(e);
	});

	req.write(payload);
	req.end();
}

// Create the shared access signature for authentication
var my_sas = create_sas_token(my_uri, my_key_name, my_key)

// Send a message for each device
for(var i = 0; i < 10; i++)
{
	// Random temperature value
	var temp = Math.floor(Math.random() * 90);
	var payload = '{\"TimeStamp\":\"2015-02-10T14:43.05.00320Z\",\"DeviceId\":\"' + i + '\",\"Temperature\":' + temp + '}';
	console.log(payload);
	// Send it
	send_message(payload);
}
