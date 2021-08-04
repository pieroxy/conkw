# conkw documentation - ExternalMetricsGrabber

This is the EMI grabber, or external metrics ingestion grabber. Just like the [FileGrabber](GRABBER_FILE.md), it allows you to ingest metrics grabbed outside of conkw, but here you need to call conkw with an http request on the `/emi` endpoint in order to feed it a metric. The advantage, of course, is that the ingestion can be done through the network.

The metrics ingested will be served by one or more `ExternalMetricsGrabber` instance defined in your config file. Each one of those is associated to a "namespace" so use more than one to segment the metrics you want to ingest.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber`
* *Default instance name:* `emi`

## Use cases

* You want to let external programs / computers push their metrics into your instance.

## HTTP dialect for single metric ingestion

The endpoint you should call is `/emi`, with a POST request.

It takes a number of parameters, either in the url or in the body of the POST message if using the content type `application/x-www-form-urlencoded`:

* `ns`: The namespace, ie: the name of the grabber instance this value should be injected into.
* `name`: The name of the metric to ingest
* `value`: The value of the metric.
* `type`: The type of the value, either `num` or `str`

If one of the parameters is missing or incorrect, the endpoint returns a http 400 code with the body being the error message.

Example of calls with curl (The first two are equivalent and ingest the same metric):

```sh
curl 'http://localhost:12789/emi?ns=test_emi&name=metric1&value=2&type=num' -X POST
```

```sh
curl 'http://localhost:12789/emi' -X POST -d 'ns=test_emi&name=metric1&value=2&type=num'
```

```sh
curl 'http://localhost:12789/emi?ns=test_emi&name=metric4&value=someTextualData&type=str' -X POST
```

## HTTP dialect for batch metric ingestion

In case you want to ingest more than one metric at a time, you can use the JSON ingestion mechanism. You still need to set the namespace in the URL, but you need to specify a content type of `application/json` and inject a JSON in the form of:

```json
{
  "num": {
    "metric1":12,
    "metric2":128
  },
  "str": {
    "metric4":"This is the value"
  }
}
```
Example of call with curl:

```sh
curl 'http://localhost:12789/emi?ns=test_emi' -X POST -H "Content-Type: application/json" -d '{"num":{"metric1":12},"str":{"metric4":"I did it!"}}'
```

## Configuration

You will need to know the name of the grabber configured in order to be able to ingest data into it through the `ns` parameter. You can have several grabbers like this configured in your system.

Example:

```json
    {
      "implementation":"net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber",
      "name":"laptop_batteries",
      "parameters": {
      }
    },
    {
      "implementation":"net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber",
      "name":"online_server",
      "parameters": {
      }
    },
```

## Real world use-case

Here is a script I run on all of my macbooks to monitor their battery charge level:

```sh
#!/bin/bash 

BL=$(pmset -g batt | grep -Eo "\d+%" | cut -d% -f1 )
CC=$(pmset -g batt | grep -Eo "discharging" )
echo $BL $CC > /tmp/bstmp.log

touch /tmp/bs.log
DIFF=$(diff /tmp/bstmp.log /tmp/bs.log)

if [ "$DIFF" != "" ]
then
  curl "http://1.2.3.4:12789/emi?ns=batteries&name=MacAirLevel&type=num&value=$BL" -X POST -H "Content-Type:text/plain"
  curl "http://1.2.3.4:12789/emi?ns=batteries&name=MacAirCharging&type=str&value=$CC" -X POST -H "Content-Type:text/plain"
  echo changed
fi

echo $BL $CC > /tmp/bs.log
```

And here is the related section of the crontab on these machines:

```
* * * * * /Users/MyUser/bin/reportBatt.sh
*/15 * * * * rm /tmp/bs.log
```

This way, every minute the charging status and level of the battery is extracted and sent to the remote conkw instance if any change happened. Every 15 minutes, the status will be sent even with no change. This allow me to set a stale time on the battery status of 20 minutes and detect when the computer is in sleep mode or off. 

Here are the same scripts on my Linux laptop:

```sh
#!/bin/bash

AC=$(cat /sys/class/power_supply/AC/online)
let BP=($(cat /sys/class/power_supply/BAT0/charge_now)*100)/$(cat /sys/class/power_supply/BAT0/charge_full)

echo $AC $BP > /tmp/hwtmp.log
touch /tmp/hw.log

DIFF=$(diff /tmp/hwtmp.log /tmp/hw.log)

if [ "$DIFF" != "" ]
then
  curl "http://1.2.3.4:12789/emi?ns=batteries&name=LinuxLaptopLevel&type=num&value=$BP" -X POST -H "Content-Type:text/plain"
  curl "http://1.2.3.4:12789/emi?ns=batteries&name=LinuxLaptopCharging&type=num&value=$AC" -X POST -H "Content-Type:text/plain"
fi

echo $AC $BP > /tmp/hw.log
```

And here is the related section of the crontab on that machine:

```
* * * * * /bin/bash /home/MyUser/bin/hwMon.sh
*/15 * * * * rm /tmp/hw.log
```

## Note on performance
Those metrics are kept in memory and dumped in full on the hard drive in `$CONKW_HOME/data` every 10s at most. So it is clearly not meant to handle millions of metrics.

Moreover, it is optimized to work on a fixed set of metrics. Adding a few metrics from time to time is fine, but adding a bunch of metrics every second is not where this grabber will perform best.

Metrics that have not been updated in 30 days will be removed automatically.

As always, test your use case and if something's not right, drop a message in GitHub.