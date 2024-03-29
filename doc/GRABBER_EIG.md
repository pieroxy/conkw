# conkw documentation - ExternalInstanceGrabber

This is the EIG grabber, or external instance grabber. It will fetch metrics from other instances of conkw, and can even fetch metric from the current instance of conkw. One of the application is to have instances of conkw running on all your machines and one central instance producing a dashboard grouping all machines.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.ExternalInstanceGrabber`
* *Default instance name:* `ext`

## Use cases

* You want to monitor all or some metrics from another running conkw instance.
* You want to monitor all or some metrics from many different conkw instance.

## Operation

The configuration only parameter is the API URL that this instance needs to call. This is the same API that the UI calls to fetch data. For example:

```json
{
  // External instance grabber. Grabs metrics from another live instance of conkw.
  "implementation":"net.pieroxy.conkw.webapp.grabbers.ExternalInstanceGrabber",
  "name":"test_eig",
  "config": {
    "url":"http://1.2.3.4:12789/api?grabbers=proc,sys",
    "credentialsRef":"TheUserCredentials"
  }
}
```

Where:

* `1.2.3.4` is the IP or host name of the computer on which conkw is running.
* `12789` is the port on which conkw is running on the computer `1.2.3.4`.
* `proc,sys` is the grabbers that need to be fetched.
* `credentialsRef` Credentials for the remote API. Only needed if the remote api is authenticated. See [all about credentials here](CONCEPTS_CREDS.md) for details.

The metrics extracted from both grabbers `proc` and `sys` are merged into one namespace, but prefixed with their grabber name. For example, the metric `num.systemloadavg` from the `sys` grabber will be renamed by EIG as `num.sys_systemloadavg` while the metric `num.uptime` from `proc` will be renamed `num.proc_uptime`.
