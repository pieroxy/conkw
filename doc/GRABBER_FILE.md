# conkw documentation - FileGrabber

This is the file grabber. The way it works is that it reads a file and expose the metrics it found in the file through the API. In other words, it's up to you to write scripts and programs that write data to file(s) and then automatically you can use those metrics in conkw.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.FileGrabber`
* *Default instance name:* `file`

## File format

* The files imported must abide by the properties format.
* You can read more about the file format [on wikipedia](https://en.wikipedia.org/wiki/.properties).
* To make it simple, each line must be of the form `key=value` or `key:value`.
* Each value whose key starting with `str_` will be stored in the string space.
* Each value whose key starting with `num_` will be parsed and stored as a number.
* The metrics keys generated are stripped of their prefixes.
* key/values pairs whose key doesn't start with one of the two prefixes will be ignored.


Example (live from my system)
```
num_temp_CPU: 35.0
num_temp_core0: 33.0
num_temp_core1: 33.0
temp1: 27.8
SYSTIN: 26.0
num_temp_mobo_cpu: 15.5
 (crit = 84.8
Sensor 1: 31.9
Sensor 2: 30.9
num_rpm_fan1_input: 851.000
num_rpm_fan2_input: 335.000
num_rpm_fan4_input: 770.000
num_temp_hdd_home : 32
num_temp_hdd_home2 : 31
str_displaystate: on
```

This file fed to FileGrabber will result in the following metrics:

* `num.temp_CPU`: 35
* `num.temp_core0`: 33
* `num.temp_core1`: 33
* `num.temp_mobo_cpu`: 15.5
* `num.rpm_fan1_input`: 851
* `num.rpm_fan2_input`: 335
* `num.rpm_fan4_input`: 770
* `num.temp_hdd_home`: 32
* `num.temp_hdd_home2`: 31
* `str.displaystate`: "on"

## Performance and refresh rate
The files are parsed at program startup, then each second, if their last modified time changed, they will be parsed again, otherwise the cached version will be used.

Note: You can use several instances of this grabber in order to grab more than one file. Do that by making it appear several times in the config file, with a different filename each time. You will have to choose a different name for each instance.