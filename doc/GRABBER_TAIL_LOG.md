# conkw documentation - TailLogFileGrabber

This grabber will listen to a log file, and extract data from it. It, of course, depends on an instance of `LogParser` to extract that data from the file.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.logfile.TailLogFileGrabber`
* *Default instance name:* `taillog`

## Use cases

* You want to monitor any kind of metrics from an application. Have this application log those metrics into a file, write a simple parser and voilà! You can have them into conkw.

## Configuration

```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.logfile.TailLogFileGrabber",
  "parameters": {
    "filename":"/home/pieroxy/.conkw/logs/http.log",
    "parserClassName":"net.pieroxy.conkw.webapp.grabbers.logfile.parsers.HttpLogFileParser",
    "accumulators":"multi([name(sizehist,log125hist(size,1000,1000000)),name(byct,stablekey(contentType,count(),3)),name(byuri,stablekey(uri,count(),3)),sum(size,0),count(),sum(time,0)])"
  }
}
```

* The `filename`, without much surprise, is the file this instance should monitor.
* `parserClassName` represents the fully qualified class name used to parse those logs and output a `LogRecord` out of them. See the [Provide your classes](PROVIDE_CLASSES.md) documentation to see how you can write your own parsers.
* `accumulators` is an expression allowing you to tell conkw how you want those datapoints aggregated. See [Aggregating multi dimensional datapoints](AGGREGATE.md) to understand how you can aggregate data.

## Extractions for the default http parser

While it may be of some use, the `net.pieroxy.conkw.webapp.grabbers.logfile.parsers.HttpLogFileParser` is more meant as an example of log parser to help you write your own. It is able to parse conkw http log file. Here is the data it extracts, for each line in the file:

### values

For each line, the values extracted are:

* The time spent in the request.
* The size of the response.
* While not explicitely extracted, the number of events is also recorded.

### dimensions

For each line, the dimensions extracted are:

* The Content-Type of the response.
* The host the request was intended to.
* The URI requested. Note that it only extracts the first path of the request, meaning everything before the second `/`.
* The status, meaning the http response code (200, 404, etc)
* The verb of the http request (GET, POST, etc.)
