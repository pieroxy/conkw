# conkw documentation - ProcGrabber

This is the system grabber for Linux. It relies heavily on `/proc` and `/sys` filesystems. 

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcGrabber`
* *Default instance name:* `proc`

## Use cases

* You want to monitor system metrics out of your Linux machine. That's CPU, HDD, SSD, Network, battery, etc.

## Configuration
```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcGrabber",
  "config": {
    "toExtract":"processes,uptime,bdio",
    "blockDevices":"sda",
    "mdstatFile":"/proc/mdstat"
  }
},
```

* `toExtract` allows to extract only part of the metrics it can gather, for optimal performances
* `mdstatFile` allows to specify the location and name of the mdstat file. If not present, the default is `/proc/mdstat`.
* `blockDevices` Allows to specify the block devices you want to monitor. If not present, an attempt will be made to autodetect them, see `bdio` below for more details.

## Possible extractions:

### processes
Extracts the top 3 processes in terms of CPU and Memory usage. This data is extracted from the various `/proc/<pid>/stat` files for every process in the system.

For CPU usage, the percentage used is in regards to the total CPU used. So if during the last second there was only 7.3% total CPU used on the system, the `prc_top1cpu` can very well indicate 100%, meaning 100% of the 7.3% CPU was consumed by this process.

* `num.prc_top1cpu` The percentage CPU used by the process #1, between 0 and 100.
* `str.top1cpu` the name of the process #1
* `str.pid_top1cpu` the process ID of the process #1
* And so on with 2 and 3.

For memory usage:

* `num.prc_top1mem` The percentage memory of the system used by the process #1, between 0 and 100.
* `str.top1mem` the name of the process #1
* `str.pid_top1mem` the process ID of the process #1
* And so on with 2 and 3.

### cpufreq

Extracts the frequency in hz per logical processor.

It produces, from 0 to n-1 (where n is the value of `nbcpu_threads` extracted by the `nbcpu` extraction):

* `num.cpu_0_freq` The frequency in Hz of the core specified.

### uptime
Extracts hostname and load average, as viewed in `/proc/uptime` and `/proc/loadavg`.

Metrics:

* `num.uptime` The uptime of the machine, in seconds.
* `num.loadavg1` The load average (1mn).
* `num.loadavg2` The load average (5mn).
* `num.loadavg3` The load average (15mn).

### nbcpu
Extracts the number of cores and threads on the system, as viewed in `/proc/cpuinfo`. Note that these metrics are extracted at startup time and never updated throughout the lifetime of the conkw instance.

Metrics:

* `num.nbcpu_cores` The number of cores.
* `num.nbcpu_threads` The number of logical processors viewed by the system, including hyperthreaded cores.


### cpu
Extracts CPU activity over the last second, as viewed in `/proc/stat`. 

Metrics (percentages between 0 and 100):

* `num.cpu_usg_user` The % cpu used in user mode.
* `num.cpu_usg_nice` The % cpu used by niced processes in user mode.
* `num.cpu_usg_sys`  The % cpu used in kernel mode.
* `num.cpu_usg_idle` The % of time spent idle.
* `num.cpu_usg_used` The summary of used CPU. In practice, `100-idle` time.
* `num.cpu_usg_wait` Various other states (sum entries 4, 5 and 6 in `/proc/stat`)


### mem
Extracts metrics about memory usage of the system, as viewed in `/proc/meminfo`.

Metrics, in bytes:

* `num.ramTotal` The total size of RAM.
* `num.ramFree` The free part of RAM. This is almost useless as it is quickly going down to a very small value as caches fill up in the system.
* `num.ramUsed` This is computed as `total-free-cached`.
* `num.ramCached` The sum of `cached`, `buffers` and `reclaimable` memory.
* `num.swapTotal` The total size of swap space available.
* `num.swapUsed` Computed as `total-free`.
* `num.swapFree` The free space available in the swap space.

### bdio
Extracts the bytes read and written from and to block devices, as read in `/sys/block/<device>/stat`.

The list of devices monitored can come from three sources:

* The grabber `blockDevices` parameter, which can contain a comma separated list of devices to monitor on the system. For example: `sda,nvme0n1`
* If the list of devices is not set, the block devices available are detected by parsing the output of `lsblk -d -o NAME,MODEL`. Of course, `lsblk` must be present in `/bin/` or `/usr/bin/` for this to work. All devices with a non-empty name are then considered.
* If the above auto detection fails for any reason, `sda` is then used.

Metrics extracted for every device, here for `/dev/sda`:

* `num.read_bytes_sda` The number of bytes read in the last second.
* `num.write_bytes_sda` The number of bytes written in the last second.
* Auto max: `num.max$read_bytes_sda` The maximum number observed for this value.
* Auto max: `num.max$write_bytes_sda` The maximum number observed for this value.

Global metrics:

* `num.read_bytes_all` The sum of all reads on all monitored devices.
* `num.write_bytes_all`  The sum of all writes on all monitored devices.
* Auto max: `num.max$read_bytes_all` The maximum number observed for this value.
* Auto max: `num.max$write_bytes_all` The maximum number observed for this value.
* `str.allbd` The comma separated list of monitored devices, for example: `sda,sdb,sdc,nvme0n1`


### net
Extracts the network activity as parsed from `/proc/net/netstat` second `IpExt` line.

Metrics:

* `num.netp_in` The number of bytes in since the last second.
* `num.netp_out` The number of bytes out since the last second.

These two metrics have an auto maximum value computed:

* `num.max$netp_in` The maximum number observed for this value.
* `num.max$netp_out` The maximum number observed for this value.


### battery
Extracts battery metrics from three files: `/sys/class/power_supply/BAT0/charge_full`, `/sys/class/power_supply/BAT0/charge_now` and `/sys/class/power_supply/AC[0]/online`.

The following metrics extracted and refreshed every 10 runs:

* `num.bat_exists` Is `0` if no battery has been found (and then no other metrics are extracted), `1` otherwise.
* `num.bat_prc` The percentage of charge of the battery, between 0 and 100. 
* `num.bat_plugged` Is `0` if the battery is not plugged in, `1` otherwise.

### mdstat
Extracts data from `/proc/mdstat` regarding the status of mdadm arrays, every minute.

Overall metrics:

* `num.mdstatFailed` The number of failed drives across all arrays.
* `str.mdstatSummary` The summary string, in the form of `md1:[2/2] md2:[2/2]<12%> md3:[10/10] md0:[2/2]` where `md2` is being checked / reconstructed and the process is at 12%.
* `num.mdstatNbDevices` The number of devices. In the example above there are four: `md0`, `md1`, `md2` and `md3`.

For each device:

* `str.mdstatByArray<n>` The summary string for this array, where `n` goes from 0 to `mdstatNbDevices-1`, for example `mdstatByArray0` with the value `md1:[2/2]`.


### hostname
This grabber exposes the content of `/etc/hostname` in the metric `hostname`.

Extracted once per hour, so expect the wrong value to stick around for a few minutes if you change it.