# Roadmap
Here are the things we have in mind for conkw. This is kind of my TODO list.

## Next up

* The current task is to write a dashboard editor. Enough with cryptic HTML tags!
  * The nice dashboard editor will generate a "cryptic HTML" file so that dashboards still work on old browsers
* Work with the UI and API to be able to exploit the TailLogFileGrabber data. This will unleash the multi dimensional metrics.
* This work will dig in the "Features" TODO list below.

## UI

* Find a proper way to describe number formatting. This endless list of keywords isn't really nice.

## Grabbers

* smartctl - to get metrics out of hdd, specifically their health status.
* MPRIS2 - to get music playing for most Linux players.
* Calendar Google - to get next meetings
* Calendar Outlook - to get next meetings
* Freebox - to get internet status on this home router/modem

## Misc

* https support with live reload of the certificates.
* Authenticate EMI calls with a private shared token or asymetric key.
* Most of this stuff needs to be tested.

## Features

* Build aggregators grabbers so that metrics can be extracted on a different scale as "per second" - such as average on the last minute.
* Extract metrics to influxDB/Grafana
* Define alerts. Thresholds, etc. Send them by mail in step #1
* Build an app to monitor battery level of device and receive alerts.
* Build alert management

## Path to world dominaion

* Build the thing
* ...
* World domination !
