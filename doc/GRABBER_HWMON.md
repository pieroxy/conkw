# conkw documentation - HwmonGrabber

This is the grabber monitoring hardware on Linux through the `/sys/class/hwmon` interface. As a result, it only works on Linux and other OSes where this filesystem exists.

Note that unlike the [LmSensorsGrabber](GRABBER_LM_SENSORS.md), this grabber establishes the metrics to grab at startup time and needs to be restarted in order to rescan the file list. So if you plugged in some USB cards or drive that you need to monitor, you will need to restart conkw.

Also note that HDD temperatures can be fetched with this grabber using the drivetemp kernel module. Load it with `sudo modprobe drivetemp`.

The reference documentation of the kernel sysfs hwmon interface can [be found here](https://www.kernel.org/doc/html/latest/hwmon/sysfs-interface.html).

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.HwmonGrabber`
* *Default instance name:* `hwmon`

## Use cases

* You want to monitor the fan speeds, voltages and temperatures of the computer.

## Configuration

```json
{
    "implementation":"net.pieroxy.conkw.webapp.grabbers.HwmonGrabber",
    "extract":"hwmon0,hwmon4",
    "config": {
      "include":[".*_input"]
    },
    "namedParameters": {
    }
}
```

* `extract` chooses which hwmon sensors you're willing to monitor
* `include` is an include list, a list of regexps against file names. HwmonGrabber will only extract values from those files whose name match one of those regexp.

## Metrics

Just have a look in your `/sys/class/hwmon` folder. There are three names to remember:
* The name of the hwmon folder, for example `hwmon3`
* The file called `name` inside the folder.
* The file name of the metric. Those are the files that contain a `_`.

This grabber will concatenate these three names with underscores as separators. Example:

```json
"hwmon1_BAT0_curr1_input": 0.001,
"hwmon1_BAT0_in0_input": 12.936,
"hwmon2_nvme_temp1_input": 42.85,
"hwmon2_nvme_temp2_input": 42.85,
"hwmon2_nvme_temp3_input": 47.85,
"hwmon2_nvme_temp3_crit": 100,
```

### Categories

In addition to these metrics, some string metrics are generated to try to make lists. In order to do so I created the concept of a category of metrics. The category is the basic metric name, stripped of its numbers. So, for the example above, there are the following categories: `temp_input`, `temp_crit`, `curr_input`, `in_input`

Those categories will have for value a comma separated list of metric names in this category. So, with the example above:

```json
str.temp_input: "hwmon2_nvme_temp1_input,hwmon2_nvme_temp2_input,hwmon2_nvme_temp3_input",
str.temp_crit: "hwmon2_nvme_temp3_crit",
str.curr_input: "hwmon1_BAT0_curr1_input",
str.in_input: "hwmon1_BAT0_in0_input",
```

Every metric in these lists i sorted by the hwmon number and the file name.

### Mins and Maxes

Last, the grabber automatically computes minimums and maximums of the values observed. So, for every metric generated, for example `num.hwmon2_nvme_temp1_input` there will be a `num.min$hwmon2_nvme_temp1_input` and a `num.max$hwmon2_nvme_temp1_input` metrics automatically generated.

Those values are recomputed after each run of the grabber, and the maxes and mins will evolve over time as the value of the metric goes over the maximum or under the minimum.

### Units and scale

As can be found in the [kernel documentation](https://www.kernel.org/doc/html/latest/hwmon/sysfs-interface.html) :

* `temp_` metrics are temperatures in °C. Their number is divided by 1000 by HwmonGrabber.
* `in_` metrics are voltages in volts. Their number is divided by 1000 by HwmonGrabber.
* `curr_` metrics are currents in Amps. Their number is divided by 1000 by HwmonGrabber.
* `power_*interval*` are in milliseconds.
* `power_therest` are power in watts. Their number is divided by 1000000 by HwmonGrabber.
* `fan_` metrics are rotations per minutes (or RPM).

This list and conversions are here for convenience. Your mileage may vary. Experiment and then choose the proper unit in the UI.

### Recommendation

I do not recommend to display every metric as they are displayed in the default UI. Instead, use the default UI (or the command line program) to figure out which metric you want, and then add them manually to your custom UI.