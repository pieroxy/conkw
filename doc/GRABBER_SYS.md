# conkw documentation - JavaSystemViewGrabber

This is the system grabber written in pure Java. It doesn't rely on anything else than the JDK, and thus should work on all systems. 

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.JavaSystemViewGrabber`
* *Default instance name:* `sys`

## Use cases

* You want to monitor system metrics out of your Linux, Windows or Mac machine. That's CPU, HDD, SSD, Network, battery, etc.

## Configuration
```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.JavaSystemViewGrabber",
  "extract":"sys,cpu,freespace",
  "parameters": {
    "mountPoints":["/"]
  }
},
```

* Allows to extract only part of the metrics it can gather, for optimal performances
* `mountPoints` Allows to specify the mountpoints you want monitored for free space. If not present, the mount points will be inferred automatically. See `freespace` below for more information.

## Possible extractions:

### sys
Extracts a few static informations about the system:

* `str.arch` The CPU architecture. For example `amd64` or `x86 64`.
* `num.nbcpu` The number of logical processors available to the JVM.
* `str.osname` The description of the OS. For example `Mac OS X`.
* `str.osversion` The version of the OS. For example `10.16`.
* `str.user` The user under which conkw is running.
* `str.hostname` The name of the computer on the network.

### cpu
Extracts a few metrics about CPU usage.

Metrics:

* `num.systemloadavg` The load average of the system, on 1m. Not available on Windows.
* `num.processCpuUsage` The CPU usage of the conkw process, between 0 and 1.
* `num.ProcessCpuTime` The CPU time usage of the conkw process, in nanoseconds.
* `num.totalCpuUsage` The CPU usage of the system, between 0 and 1.

### mem
Extracts memory usage metrics:

* `num.ramAvailable` The amount of RAM not allocated, in bytes.
* `num.ramTotal` The total RAM available to the system, in bytes.
* `num.ramUsed` computed as `ramTotal-ramAvailable`.

### freespace
Extracts metrics about filesystems, such as total capacity and available space.

The list of filesystems monitored can come from two sources:

* The grabber `mountPoints` parameter, which can contain a comma separated list of mount points, pointing to filesystems to monitor. For example: `["/","/media/USB"]` or `["C:\","D:\"]`
* If the list of filesystems is not set, the filesystems are detected automatically. On windows, they are the list of drives returned by `File.listRoots()`, on other systems they are guessed by excluding from `FileSystems.getDefault().getFileStores()` all path starting with anyone of `/dev /snap /sys /System /proc /run` which usually represent special filesystems, not general purpose storage filesystems.


Metrics, in bytes, where `*` is the name of the filesystem:

* `num.freespace_total_*` The total size of the filesystem.
* `num.freespace_usable_*` The free space for this filesystem.
* `num.freespace_used_*` This is computed as `total-used`.

So, for `D:\`, the metrics names are `freespace_total_D:\` and so on. The last metric emmited:

* `str.freespace_mountpoints` The comma separated list of filesystems monitored. For example: `/,/Volumes/Memory Card` or `C:\,D:\`