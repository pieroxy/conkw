# conkw documentation - Customize the UI

This documentation explores the default UI installed with conkw. It is extensible and should cover all your needs, except for the most wanting people. The good news is, you can extend this UI beyond what is available. 

## Index

* The [UI Principles](UI_PRINCIPLES.md) covers the basic HTML structure of a ConkW page.
* The [UI Reference](UI_REFERENCE.md) covers all the types of behavior the UI offers. Gauges, tooltips, errors, numbers, etc.
* The [UI Style Guide](UI_STYLES.md) covers the default stylesheet, fonts and graphical widgets available, such as the clocks.


## Beyond the ConkW UI

There is an API (If you haven't disabled it) available. Have [a look here for more details](API.md). You can use that to build your own UI. Any UI. 

You can plug [opsgenie](https://www.atlassian.com/software/opsgenie/) into it. You can plug [Graphite](https://graphiteapp.org/) or [InfluxDB](https://www.influxdata.com/products/influxdb/) right into it.

Writing a connector for these apps will be a tiny bit of work but it will allow you to leverage the mesh capabilities and in depth integration of ConkW, with a real database or alerting system.
