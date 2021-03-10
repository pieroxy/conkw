# conkw documentation - ProcGrabber

This is the system grabber for Linux. It relies heavily on `/proc` and `/sys` filesystems. 

*Full name:* `net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcGrabber`

Possible extractions:

## processes
Extracts the top 3 processes in terms of CPU and Memory usage. This data is extracted from the various `/proc/<pid>/stat` files for every process in the system.

For CPU usage, the percentage used is in regards to the total CPU used. So if during the last second there was only 7.3% total CPU used on the system, the `prc_top1cpu` can very well indicate 100%, meaning 100% of the 7.3% CPU was consumed by this process.

* `prc_top1cpu` The percentage CPU used by the process #1
* `top1cpu` the name of the process #1
* `pid_top1cpu` the process ID of the process #1
* And so on with 2 and 3.

For memory usage:

* `prc_top1mem` The percentage memory of the system used by the process #1
* `top1mem` the name of the process #1
* `pid_top1mem` the process ID of the process #1
* And so on with 2 and 3.

## uptime
Extracts hostname and load average, as viewed in `/proc/uptime` and `/proc/loadavg`.

Metrics:

* `uptime` The uptime of the machine, in seconds.
* `loadavg1` The load average (1mn).
* `loadavg2` The load average (5mn).
* `loadavg3` The load average (15mn).

## cpu
Extracts CPU activity over the last second, as viewed in `/proc/stat`. 

Metrics:

* `cpu_usg_user` The % cpu used in user mode.
* `cpu_usg_nice` The % cpu used by niced processes in user mode.
* `cpu_usg_sys`  The % cpu used in kernel mode.
* `cpu_usg_idle` The % of time spent idle.
* `cpu_usg_used` The summary of used CPU. In practice, `100-idle` time.
* `cpu_usg_wait` Various other states (sum entries 4, 5 and 6 in `/proc/stat`)


## mem
Extracts metrics about memory usage of the system, as viewed in `/proc/meminfo`.

Metrics, in bytes:

* `ramTotal` The total size of RAM
* `ramFree` The free part of RAM. This is almost useless as it is quickly going down to a very small value as caches fill up in the system.
* `ramUsed` This is computed as `total-free-cached`.
* `ramCached` The sum of `cached`, `buffers` and `reclaimable` memory.
* `swapTotal` The total size of swap space available.
* `swapUsed` Computed as `total-free`.
* `swapFree` The free space available in the swap space.

## bdio
Extracts the bytes read and written from and to block devices, as read in `/sys/block/<device>/stat`.

The list of devices monitored can come from three sources:

* The grabber `blockDevices` parameter, which can contain a comma separated list of devices to monitor on the system. For example: `sda,nvme0n1`
* If the list of devices is not set, the block devices available are detected by parsing the output of `lsblk -d -o NAME,MODEL`. Of course, `lsblk` must be present in `/bin/` or `/usr/bin/` for this to work. All devices with a non-empty name are then considered.
* If the above auto detection fails for any reason, `sda` is then used.

Metrics extracted for every device, here for `/dev/sda`:

* `read_bytes_sda` The number of bytes read in the last second.
* `write_bytes_sda` The number of bytes written in the last second.

Global metrics:

* `read_bytes_all` The sum of all reads on all monitored devices.
* `write_bytes_all`  The sum of all writes on all monitored devices.
* `allbd` The comma separated list of monitored devices, for example: `sda,sdb,sdc,nvme0n1`


## net
Extracts the network activity as parsed from `/proc/net/netstat` second `IpExt` line.

Metrics:

* `netp_in` The number of bytes in since the last second.
* `netp_out` The number of bytes out since the last second.


## battery
Extracts battery metrics from three files: `/sys/class/power_supply/BAT0/charge_full`, `/sys/class/power_supply/BAT0/charge_now` and `/sys/class/power_supply/AC[0]/online`.

The following metrics extracted and refreshed every 10 runs:

* `bat_exists` Is `0` if no battery has been found (and then no other metrics are extracted), `1` otherwise.
* `bat_prc` The percentage of charge of the battery, between 0 and 100. 
* `bat_plugged` Is `0` if the battery is not plugged in, `1` otherwise.

## mdstat
Extracts data from `/proc/mdstat` regarding the status of mdadm arrays.

Overall metrics:

* `mdstatFailed` The number of failed drives across all arrays.
* `mdstatSummary` The summary string, in the form of `md1:[2/2] md2:[2/2]<12%> md3:[10/10] md0:[2/2]` where `md2` is being checked / reconstructed and the process is at 12%.
* `mdstatNbDevices` The number of devices.

For each device:

* `mdstatByArray<n>` The summary string for this array, where `n` goes from 0 to `mdstatNbDevices-1`, for example `mdstatByArray0`.


## hostname
This grabber exposes the content of `/etc/hostname` in the metric `hostname`.