# conkw documentation - PushToEmiGrabber

This is the PushToEmiGrabber grabber. It is used to push all or part of the configured grabbers data to another conkw instance, through the `/emi` endpoint. It is used to to send metrics from one to another conkw instance.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.PushToEmiGrabber`
* *Default instance name:* None. You have to provide a name in the config file.

## Use cases

* You want some metrics to be pushed to another instance of conkw, somewhere on your machine or accessible through the network.

## Configuration

```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.PushToEmiGrabber",
  "extract":"sys",
  "name":"p2emi",
  "parameters": {
    "url":"http://localhost:12789/emi?ns=test_emi",
    "prefix":"mygrabber"
  }
}
```

* `extract` this is the list of extractors to extract and send away. All metrics from every extractor will be prefixed by the extractor name.
* `name` this is a unique name for this grabber.
* `prefix` this is a unique string used to prefix all metrics.
* `url` This is pointing to the conkw instance you want to send your metrics to. Needs to be a `http` or `https` url. Note that you have to specify the grabber name on the target conkw instance through the `ns` parameter.

Let's take the `totalCpuUsage` metric of the `sys` grabber. With the above configuration, the metric will be pushed as `mygrabber_sys_totalCpuUsage`. That's the prefix, the extractor name and the actual metric name, all separated by underscores.


## How it works

Every second, the `PushToEmiGrabber` will grab metrics from the configured grabbers and send them to the distant conkw instance in one http (or https) call. This allows potentially thousands of servers to report their health status to a central instance.

## Performance

* Between each grab, `PushToEmiGrabber` waits 1s +/-10ms to avoid having all instance push their metrics at the same time.
* You might want to fine tune your grabbers to only extract the needed information as more metrics will mean more stuff to extract, so more CPU on the sending conkw, and more network activity both on the sending and receiving conkw instances. So, fine tuning your grabbers will mean a more transparent setup.
* Obviously, if you have many (hundreds or thousands of) machines sending their metrics to one conkw, this conkw will consume a non-negligible amount of CPU and RAM. You might want to consider having a dedicated machine / instance for it.