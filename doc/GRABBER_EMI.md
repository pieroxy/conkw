# conkw documentation - ExternalMetricsGrabber

This is the EMI grabber, or external metrics ingestion grabber. Just like the [FileGrabber](GRABBER_FILE.md), it allows you to ingest metrics grabbed outside of conkw, but here you need to call conkw with an http request in order to feed it a metric. The advantage, of course, is that the ingestion can be done through the network.


*Full name:* `net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber`

## HTTP dialect

The endpoint you should call is `/emi`, with a POST request.

It takes a number of parameters, either in the url or in the body of the POST message if using the content type `application/x-www-form-urlencoded`:

* `ns`: The namespace, ie: the name of the grabber this value should be injected into.
* `name`: The name of the metric to ingest
* `value`: The value of the metric.
* `type`: The type of the value, either `num` or `str`

If one of the parameters is missing or incorrect, the endpoint returns a http 400 code with the body being the error message.

Example of calls (The first two are equivalent and ingest the same metric):

```sh
curl 'http://localhost:12789/emi?ns=test_emi&name=metric1&value=2&type=num' -X POST
```

```sh
curl 'http://localhost:12789/emi' -X POST -d 'ns=test_emi&name=metric1&value=2&type=num'
```

```sh
curl 'http://localhost:12789/emi?ns=test_emi&name=metric4&value=someTextualData&type=str' -X POST
```

## Configuration
You will need to know the name of the grabber configured in order to be able to ingest data into it. You can have several grabbers like this configured in your system.