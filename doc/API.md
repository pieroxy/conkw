# conkw documentation - The api

Conkw is a programs that gathers data from many places through [the grabbers](LIST_GRABBERS.md) and displays it through [the ui](UI.md). Of course, the missing piece in all of this is how the UI can get the data it displays.

This is the API. 

Note that reading this document is *not* required for most people. Two clients for the api exists, one in the UI and one in the [`ExternalInstanceGrabber`](GRABBER_EIG.md). This is all you should know for most usages.

If you intend to write your own client however, here are the details:

## How to call the API

The API listens to http requests on the `/api` path of your server. It responds a JSON response with the specified data. It can accept two parameters, mutually exclusive:

* `grabbers` contains the list of grabbers instances you want data from. This is a comma separated list of strings. The grabbers instance names are either their default name or the name defined in the config file for the instance.
* `grabberAction` Defines the request as an action to be passed to a specific grabber. The request will be passed to all grabbers until one declares that the request is his and processes it. Whatever the grabber returns will be the output of this call. Note that in the built-in grabbers only the `SpotifyGrabber` handles this function. It serves both for the return from the authentication as well as for the commands (play, pause, etc)


## The model
The API in `grabbers` mode will return a JSON like this:

```json
{
  "timestamp": 1621865909383,
  "instanceName":"_default__",
  "errors": [],
  "responseJitter": 52,
  "metrics": {
    "test_eig": {
      "timestamp": 1621865908390,
      "errors": [],
      "extracted": [],
      "extractor": "ExternalInstanceGrabber",
      "name": "test_eig",
      "num": {
        "sys_totalCpuUsage": 0.05900816070307596,
        "sys_freespace_total_/media/extended": 1968874332160,
        "sys_ProcessCpuTime": 1275980000000,
      },
      "timestamps": {},
      "elapsedToGrab": 0,
      "str": {
        "sys_freespace_mountpoints": "/,/home,/media/backup_extended,/media/extended,/media/Travail,/media/movies,/media/rw-media",
        "sys_user": "pieroxy",
        "sys_arch": "amd64",
        "sys_osversion": "5.8.0-53-generic",
        "sys_osname": "Linux",
        "sys_hostname": "pieroxy-dev"
      }
    }
  },
  "needsAuthentication": false
}
```

At the root level:

* `timestamp` is the timestamp at which this response was built, calling all extractors for their data.
* `instanceName` the name of the conkw reporting the data. Useful in a setup where several conkw are installed.
* `errors` is the list of error messages produced by the api, grabbers, etc.
* `responseJitter` is the current timestamp in millisecond modulo 1000 of the server at the time of the request. 
* `needsAuthentication` is a flag indicating the need for authentication. It is `true` if authentication is on and the current request is not authenticated. When `true`, the following property is not set.
* `metrics` This is where the data extracted will be shown. There is here one entry per grabber requested, indexed by its name. It contains:
   * `timestamp`: The timestamp at which the data was extracted.
   * `extracted`: The list of namespaces extracted. Grabbers can be instructed to extract only part of what they can extract. By default it will be empty, meaning everything was extracted.
   * `extractor`: The identity of the extractor. It is the simple name (without the package) of the Java class doing the extraction.
   * `name`: The name of the extractor. Either the default name for this class or the name overriden in the config.
   * `elapsedToGrab`: The time in ms taken to do the extraction of the metrics.
   * `num`: All the numeric metrics, by name.
   * `str`: All the string metrics, by name.
   * `timestamps`: Some extractors (Such as the `ExternalMetricsGrabber`) are pushed metrics individually. Therefore, the timestamp of the extracted metrics vary from one metric to the next. The timestamp of the individual metrics are stored in there, prefixed with `num.` for the numerical metrics and by `str.` for the alphanumeric ones. If the timestamp cannot be found here for a given metric, it will be assumed to be the `timestamp` property above.


## Authenticating a call to the API

Note: This part is not yet stable as more user sources will be built, it will change. So I will document it when it will be stable.

Just know that even through http, the password is never transmitted, so no risk there. Of course, the session token can be stolen easily if you don't use https, hence I do recommend to use https for authentication.

## Performance considerations

* Extraction of the various grabbers is done asynchronously, at most once every second (configurable for some grabbers). Therefore, calling the API more than once a second is likely to return the same data.
* The grabbers that are not called will stop grabbing their data after 5 seconds. So, no call to the api will effectively stop the grabbers and therefore all activity of the server.
* Calling the API with only some of the configured grabbers will not wake up the other grabbers. 
* Note that the `PushToEmiGrabber` is calling the API every second, keeping the grabbing up and running for the grabbers it is configured to handle.
* Grabbers can be configured to grab only a few of their metrics, optimizing the performance hit of the data grabbing by avoiding to grab "useless" (for your use case) metrics. As a result, having default grabbers configured to grab everything and some "performance" grabbers optimized for the automatic data gathering allows to open the default UI on a the machine and see all detailed metrics, while in normal operations the server will be optimized.
* The JSON serialization (and deserialization) is done with [dsl-json](https://github.com/ngs-doo/dsl-json/), which gives appropriate performances.

