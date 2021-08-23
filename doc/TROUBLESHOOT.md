# conkw documentation - Troubleshooting

You've got this grabber you're trying to configure and it refuses to do what you want it to do. Here is the place where I will give you the tools to try and figure out what's wrong.

This all revolves around logging.

The log file for conkw is located in `$CONKW_HOME/log/conkw.log`. What will be logged and how is defined in two files:

## $CONKW_HOME/config/config.jsonc

This is the config file. This is where you can define the log level of a specific grabber: 

```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.openweathermap.OpenWeatherMapGrabber",
  "extract":"minute,hour,day,current",
  "logLevel":"FINE",
  "name":"paris_weather",
  "parameters": {
    "token":"abcdef1234567890",
    "lat":"48.8534",
    "lon":"2.3488"
  }
},
```

Notice the property `logLevel`: it defines the log level for this instance of OpenWeatherMapGrabber. 

Log levels can be, in order of increasing verbosity:

* `OFF` No log will emmited.
* `SEVERE` Only the severe errors will be logged.
* `WARNING` Warnings will be emmited.
* `INFO` This is the default log level. The grabber will give information while trying not to be too verbose.
* `CONFIG` This level is rarely used in CONKW.
* `FINE` This is where the grabber will start to be quite verbose. This level and below are useful to troubleshoot but you don't want to leave them on in a production environment.
* `FINER` More details.
* `FINEST` This level is rarely used in CONKW.
* `ALL` Logs everything.


## $CONKW_HOME/config/logging.properties

This file defines how the logger works. This is the default conkw config file for `java.util.logging`.

```INI
handlers= net.pieroxy.conkw.utils.logging.FileHandler

.level= INFO
http_log.handlers=net.pieroxy.conkw.utils.logging.HttpFileHandler
http_log.useParentHandlers=false
http_log.level = FINE

net.pieroxy.conkw.utils.logging.FileHandler.pattern = /home/myuser/.conkw/log/conkw.log
net.pieroxy.conkw.utils.logging.FileHandler.limit = 5000000
net.pieroxy.conkw.utils.logging.FileHandler.count = 10
net.pieroxy.conkw.utils.logging.FileHandler.level = FINEST
net.pieroxy.conkw.utils.logging.FileHandler.append = true
net.pieroxy.conkw.utils.logging.FileHandler.formatter = net.pieroxy.conkw.utils.logging.SingleLineFormatter

net.pieroxy.conkw.utils.logging.HttpFileHandler.pattern = /home/myuser/.conkw/log/http.log
net.pieroxy.conkw.utils.logging.HttpFileHandler.limit = 5000000
net.pieroxy.conkw.utils.logging.HttpFileHandler.count = 10
net.pieroxy.conkw.utils.logging.HttpFileHandler.level = FINEST
net.pieroxy.conkw.utils.logging.HttpFileHandler.append = true
net.pieroxy.conkw.utils.logging.HttpFileHandler.formatter = net.pieroxy.conkw.utils.logging.SingleLineFormatter
```

First, we use our own version of `FileHandler`. The feature in there is that it rotates *and* compress the old log files. 

Then, the default level is INFO. This is where you can change it, although we recommend running with this in production.

The next three lines define a specific logger for `http_log`. We don't want the http log file mixed with the rest of the messages.

Then we configure the `FileHandler`. 

* `pattern` The file name and location. None of the interpolations done by the default FileHandler works there, it is just a file name.
* `limit` Tells the handler to rotate the file when it's approximately 5MB big.
* `count` Tels the handler to retain 10 old log files.
* `level` Tells the handler to log everything that's thrown at it.
* `formatter` This is our own formatter. 

Last, we configure the logger we've defined for `http_log`.

For example, here the log for a shutdown sequence:

```log

2021-08-12 08:26:16 WARNING roxy.conkw.webapp.servlets.Api Shutdown sequence requested.
2021-08-12 08:26:17    INFO ieroxy.conkw.standalone.Runner Shutting Down.
2021-08-12 08:26:17    INFO oyote.http11.Http11NioProtocol Pausing ProtocolHandler ["http-nio-12789"]
2021-08-12 08:26:17    INFO .catalina.core.StandardService Stopping service [Tomcat]
2021-08-12 08:26:17    INFO                         JVM GC [GC (Allocation Failure)  69770K->63755K(84160K), 4ms]
2021-08-12 08:26:17    INFO roxy.conkw.webapp.servlets.Api Api Thread stopped.
2021-08-12 08:26:17    INFO webapp.servlets.ApiAuthManager Stopping save thread.
2021-08-12 08:26:17    INFO bers.JavaSystemViewGrabber/sys Dispose sys
2021-08-12 08:26:17    INFO rabbers.PushToEmiGrabber/p2emi Stopping PushToEmiGrabber
2021-08-12 08:26:17    INFO LatestUnreadMailsGrabber/mails Dispose mails
2021-08-12 08:26:17    INFO xternalMetricsGrabber/test_emi Stopping save thread.
2021-08-12 08:26:17    INFO                         stdout Hello world!!!
2021-08-12 08:26:17    INFO oyote.http11.Http11NioProtocol Stopping ProtocolHandler ["http-nio-12789"]
2021-08-12 08:26:17    INFO oyote.http11.Http11NioProtocol Destroying ProtocolHandler ["http-nio-12789"]
```

And here is what it means:

* The Api receives a shutdown request, that is accepted.
* The Runner decides to shut down.
* Tomcat is notified and informs that it is starting its shutdown sequence.
* The GC kicks in and frees up some memory.
* Api and ApiAuth stops
* 4 Grabbers stop. Note that the logger name for a grabber is the fully qualified name of the grabber class concatenated with the name of the instance with a "/" in the middle.
* Some class, somewhere wrote something on stdout. "Hello world!!!"
* Tomcat notifies the shutdown sequence is complete and stops listening on port `12789`.


## Cheat sheet

* The JVM GC logs under "JVM GC". Be aware that while those logs look like those of a `-verbose:gc`, they are not the same numbers. So do not try to compare them. Sizes are global, not only the eden and old gen space.
* stdout and stderr are captured so any stuff that writes on them is logged as INFO (stdout) or SEVERE (stderr). The IMAP library comes to mind. If any of the Mail grabbers is set as logging `FINE`, the IMAP library is told to log "debug" and lots of stuff end up on `stdout`.
* Grabbers are logged under the fully qualified name of their implementation ` + / + ` name of the grabber.
* Log files are rotated and compressed so you don't need to worry about their size. Leaving stuff in `FINE` will just consume more resources as well as flushing the logs more often. But it will not fill up your disk.
* The http traffic is logged in `$CONKW_HOME/log/http.log`. This log does not use the standard http log file pattern but a structured object serialized in json. You can disable it in the `logging.properties` by setting `http_log.level = INFO`.
* If you can't get it working even with all that, hop on github and ask a question: [Open an issue](https://github.com/pieroxy/conkw/issues) or [Start a discussion](https://github.com/pieroxy/conkw/discussions)
