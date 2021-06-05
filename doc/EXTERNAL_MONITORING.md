# conkw documentation - Monitor things that are not available by default

Apart from the existing grabbers, conkw lets you monitor anything you want! You will just have to make the data available to conkw on your own.

There are several ways of doing that.

## FileGrabber

Conkw can read a file from the filesystem. It will automatically refresh data as soon as the file changes. Your job is then to make sure the file contains the data you want to make available to conkw. 

Read more in the documentation of the [FileGrabber](GRABBER_FILE.md)

## ExternalMetricGrabber

Conkw exposes a REST endpoint on `/emi`. EMI stands for External Metrics Ingestion. Any process or computer with access to this endpoint can then inject metrics into your conkw instance. 

Read more in the documentation of the [ExternalMetricsGrabber](GRABBER_EMI.md)

## ExternalInstanceGrabber

Conkw can also read metrics from another conkw instance. Just tell it the other conkw URL and which grabbers you want to grab, and all those metrics will be available to your conkw instance. This is one of the ways to mesh conkw instances, in "pull" mode.

Read more in the documentation of the [ExternalInstanceGrabber](GRABBER_EIG.md)

## PushToEmiGrabber

Conkw can also push some or all of its metrics to another conkw instance. This work through the `/emi` endpoint of the distant instance (see above). This is the only way to have an agent that doesn't listen to any port and do not expose endpoints and still make its metrics available through another conkw instance. This is the "push" mode available to mesh conkw instances. 

Read more in the documentation of the [PushToEmiGrabber](GRABBER_PUSH.md)