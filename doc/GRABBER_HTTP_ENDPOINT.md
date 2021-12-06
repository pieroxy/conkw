# conkw documentation - HttpEndpointGrabber

This is the http endpoint grabber. It will probe the provided http(s) URLs and grab the size, http response code, response time and eventually capture data. This allows monitoring of your http endpoints to make sure a given server or service is up and running. It refreshes its values every second by default.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.http.HttpEndpointGrabber`
* *Default instance name:* `httpEndpoint`

## Use cases

* You want to monitor that a given webserver is responding in the right timing.
* You want to monitor that a given endpoint response contains a specific string, like a version number for example - to ensure all servers are deployed with the same version.

## Configuration
```json
"endpoints":[
  {
    "id":"home",
    "url":"http://myservice.com/",
    "toExtract":[
      {
        "id":"version",
        "pattern":".*<code class=\"version\">([0-9a-z]*)</code>.*",
        "number":false
      }
    ]
  },
  {
  "id":"slack",
  "url":"https://status.slack.com/api/v2.0.0/current",
  "toExtract":[
      {
        "id":"ok",
        "pattern":".*\"status\":\"ok\".*",
        "number":false
      }
    ]
  },
  {
    "id":"sysgrabber",
    "url":"http://localhost:12789/api?grabbers=sys",
    "toExtract":[
      {
        "id":"cpu",
        "pattern":".*\"totalCpuUsage\":([0-9+-e.]+),.*",
        "number":true
      }
    ]
  }
]
```

As this is a `TimeThrottledGrabber`, you can define `ttl` and `errorTtl`. [See here for more details](CONFIGURE.md). The default ttl is 1 second.

For each endpoint being monitored, you can define its name on the metric path and the URL being monitored, as well as a set of regular expressions that can either match the whole body or a part of it. If the `number` property is set to `true`, the matching area will be converted to a Double. Here is the explanation for the three endpoints defined above:

### home
This one monitors the home page of the website `http://myservice.com/` under the `home` keyword. Additionally, it will try to capture the content of the `<code class=\"version\">` html tag under the `version` keyword.

Note: It is not a good idea to parse HTML with a regexp. Hopefully, it's my website, so I control how the HTML is generated. So this basic pattern matching is actually acceptable.

### slack
This is an example of how you can use this grabber to monitor external services health status. There's more in the default config file, have a look. Here, conkw will probe `https://status.slack.com/api/v2.0.0/current` and will try to see if the content contains the string `"status":"ok"`. 

Note: It is not at all a good idea to try to parse JSON with a regexp, but hopefully, that's not what we're doing here. It's basic pattern matching. And if and when Slack's JSON generation stops matching this pattern, I will have to investigate which is a good thing since their monitoring stuff changed as well.

### sysgrabber
This is an example of how you can use this grabber to monitor a value that comes out of an API. Here, it's querying the local conkw instance for the `sys` grabber, and extract the CPU usage level.

Note: It is not at all a good idea to try to parse JSON with a regexp, which is what I'm doing here. Again, as I own the API I'm querying, it works. It's still a bad idea though.


## Metrics

For each domain name (here `sysgrabber`, the third configured endpoint in the example above), this grabber will produce the following metrics:

* `num.sysgrabber.status`: The http status code, usually `200` when all is well
* `num.sysgrabber.size`: The size in bytes of the response body.
* `num.sysgrabber.firstByte.time`: The response time of the first byte of the response, in nanoseconds.
* `num.sysgrabber.lastByte.time`: The response time of the last byte of the response, in nanoseconds.

For each regexp in the `toExtract` property: 

* `num.sysgrabber.cpu.found`: How many partial matches were found for the regexp provided, here `0` as the regexp matches the entire body.
* `num.sysgrabber.cpu.captured`: The value captured by the matching group in the regexp, as a number. Note that only one group is extracted for now.
* `num.sysgrabber.cpu.matched`: `1` if the regexp matched the entire body, `0` otherwise.

If the `number` attribute of the extract configuration is set to false (such as for the first example above):

* `str.home.version.captured`: The value captured by the matching group in the regexp. Note that only one group is extracted for now.


