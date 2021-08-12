# conkw documentation - Configure your instance

The config file can be found in `$CONKW_HOME/config/config.jsonc`. The file format is jsonc, meaning json with comments. Lots of comments are already in the default config file, to help you figure out what is possible. Keep it around so that you can refer to it.

We will show you the basics in here:

## General structure

```json
{
  "grabbers":[],
  "apiAuth":{},
  "httpPort": 12789,
  "disableDefaultUI":false,
  "disableCustomUI":false,
  "disableApi":false,
  "disableEmi":false
}
```

* `grabbers`: This is the list of grabbers with their configurations. Refer to the next section to see how to define and configure a grabber.
* `apiAuth`: This is the configuration for the api authentication. See the section below for details.
* `httpPort`: This holds the port number on which the http server is listening.
* `disableDefaultUI`: Set to `true` to hide the default UI. The documentation, home page and grabbers default UI will be unavailable.
* `disableCustomUI`: Set to `true` to hide the custon UI. Everything under `$CONKW_HOME/ui` is normally accessible through the `/ui` endpoint. This flag will hide it.
* `disableApi`: Set to `true` to hide the API. If you don't need any UI nor the API, disable it here and the `/api` endpoint won't be registered.
* `disableEmi`: Set to `true` to hide the `/emi` endpoint. While not a security threat, if you have no `ExternalMetricsGrabber` registered, it stands to reason to disable the endpoint handling them.

Note that if the last four flags are set to `true`, the internal Apache Tomcat instance won't be started and conkw will act as an agent. This will save a bit of memory and a tiny bit of CPU on startup.

## The grabbers

The `grabbers` section of the config file is an array. It contains one entry per grabber you want configured. They all share a few properties. Let's look at an example:

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

Here is the meaning of these parameters:

* `implementation` : The fully qualified name of the class implementing the grabber. Note that more than one instance of a specific implementation might exist in your config file. In this example, you might want to monitor the weather in Paris, Dubaï and Tokyo. You will define three grabbers with `OpenWeatherMapGrabber` as an implementation.
* `extract` : The list of metrics you want to extract for this instance. Note that some grabbers do not act on this list. Read the documentation for your grabber to see which supports only extracting part of their metrics.
* `logLevel` : The log level of this grabber. Used to troubleshoot a grabber that refuses to do what you want it to do. Hop on to the [toubleshooting](TROUBLESHOOT.md) section of this guide for more details.
* `name` : The name of this grabber instance. This is the name the UI will see, and the name that needs to be provided to the API. Most grabbers have a default instance name, shown in their documentation page. Not more than one grabber instance might exist with the same name, so all of your grabbers in the list must have a different name.
* `parameters` : This is the map of strings that is used to configure the grabber. This is highly grabber specific. Read the documentation or the default config file to see how to configure your grabber.

## The API authentication

This section of the configuration file allows you to setup authentication for the API. The API is the endpoint mapped to `/api` that delivers the actual data to your UI.

```json
"apiAuth":{
  "auth":false,
  "sessionLifetime":"1y",
  "sessionInactivityTimeout":"30d",
  "users":[
    {
      "login":"testuser", 
      "password":"foobar"
    }
  ]
},
```

* `auth` : Set to `false`, the api is not authenticated. Set to `true`, only users in the users list can connect to the API.
* `sessionLifetime` : The lifetime of any session. After this delay, the session will be deleted. See below for the format.
* `sessionInactivityTimeout` : The lifetime of any session when inactive. After this delay is elapsed while no request is made with a given session, the session will be deleted. See below for the format.
* `users` : The list of users, with their users and passwords, in clear text. This is the V1, please be patient. Yes, clear text password is not ideal from a security point of view. 


## Duration format

[Have a look at the duration concept here](CONCEPTS.md)