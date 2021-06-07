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

TO BE CONTINUED