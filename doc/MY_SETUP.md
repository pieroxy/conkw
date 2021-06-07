# conkw documentation - My very own setup

Here is my current setup:

![](https://pieroxy.net/conkw/screenshots-doc/conkw_setup_raw.png)

## Explanations of the regions

1. The first column represents my main computer on which I am working all day long. CPU, RAM, network, hdd, etc.
2. The second column represents all other computers and services I own or operate. Servers in the basement, hosted servers, etc. It goes from production beasts to raspberry pies.
3. The third column represents the time and music, plus today's weather.
4. The fourth column id the 8 day weather forecast, along with stock and news.
5. The bottom portion is the 48-hours weather forecast.

## Detailed explanation

![](https://pieroxy.net/conkw/screenshots-doc/conkw_setup_numbered.png)

### 0
* The first line represents the global conkw status and the zoom level of the UI.
* The second line various metrics about conkw performance.
* The third the uptime of my computer
* The fourth the status of the online Slack service. See the [FileGrabber](GRABBER_FILE.md) to see how I do it.


### 1
This is the RAM usage of my machine. 

* The first line is the swap used, with a gauge.
* The second one is the total amount of memory.
* The third the used memory
* The fourth the cached memory
* All three lines above are represented into a hgauge. That's a gauge with history.
* The bottom part represents the top 3 processes as far as RAM usage is concerned. IntelliJ is on top :)

### 2
This is the CPU usage on my system.

* The first line is the CPU used, total
* Then the cpu used by the system (sometimes called kernel time)
* The cpu used by user processes
* The cpu used by niced and wait processes
* All four values are summarized on an hgaughe. As you can see, there is a huge increase in CPU usage over the last minute.
* Then, you see the top three processes, from a CPU usage standpoint. As you can see most of the CPU is used by ffmpeg.

### 3
This is the HDD I/O, summarized.

* Read rate in the last second
* Maximum read rate observed since the conkw installation
* This is summarized by an hgauge. As you can see, not much activity in the last minute.
* The second portion is the same about writes. As you can see there was a burst a bit ago.

### 4
This is the Network I/O, summarized.

* Input rate in the last second
* Maximum input rate observed since the conkw installation
* This is summarized by an hgauge. As you can see, there was a huge read activity about two minutes ago.
* The second portion is the same about output. The huge read burst happened while a small output burst was happening. This is always the case with network.

### 5
The load average on my system, over the last minute, 5 minutes and 15 minutes.


### 6
The HDD usage percentage. This is all the mount points on my system, so not only local filesystems can be monitored. As usual, my NAS is almost full.

### 7
External computers monitoring

This section includes monitoring for 8 different computers. You can see 6 hgauges for each machine:

* CPU
* RAM
* NET in/out
* HDD Read/writes

Is also monitored the state of the mdadm arrays on the machines that have them.
That's software raid, in case you don't know. 
You can see a "summary string" for the arrays. 
If it ever deviates from this value, il will be displayed in red.

TO BE CONTINUED