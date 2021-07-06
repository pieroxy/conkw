# conkw


![](src/web/favicon-16x16.png) Web-based conky-like monitoring platform.

## Conky?

[Conky](https://github.com/brndnmtthws/conky) is a program I've been using for quite a while. But recently I've been wondering if the real estate consumed by its window was really worth it:

* Either the window is docked and it consumes quite a bit of real estate on the screen.
* Either the window is decorating the desktop background and I never see it.

## Conkw?

Conkw was spawned out of a simple idea: We all have an old laptop or tablet lying around, gathering dust. Why not using this otherwise wasted screen real estate to display my much cherished data I used to have on my old Conky setup?

The easiest client-server setup today is a web server and a browser. That's great, because:

* All tablets and laptops have browsers.
* Every reasonably modern computer can host a web server.

## UI Support

* The UI supports iOS Safari down to iOS 9.3.5, hence supporting the iPad 2 and iPhone 4S, both released in 2011 as well as the iPod touch 5th gen released in 2012.
* All recent browsers are supported (Edge, Chrome, Firefox, etc)
* More tests and support for more ancient platforms in progress.

## Back-end support

* Any OS where you can install java 8 or greater. According to [Oracle](https://www.oracle.com/java/technologies/javase/products-doc-jdk8-jre8-certconfig.html) you need something at least as recent as:
  * Windows Vista SP2 (2007)
  * Windows Server 2008 R2 (2009)
  * Ubuntu Linux 12.04 LTS - both ARM and x86 (2012)
  * Suse Linux Enterprise Server 10 SP2+ (2008)
  * Red Hat Enterprise Linux 5.5 (2010)
  * Mac OS 10.8.3

* Notes:
  * More OS than those listed above can run Java 8, such as Windows XP, MaxOS 10.6 and Ubuntu 10.04 but not from the official download pages. You will need to fiddle a bit and find the proper download.
  * The system grabbers might start to fail extracting some class of data on very old systems. You may have to rely on scripts in your crontab to extract the desired data and combine them with the FileGrabber.
  * The memory footprint of the server for a typical installation is under 200MB
    
## Performance

* UI
  * Complex setups (with 100s of elements) can be displayed on the oldest hardware. Lots of thought has been given to performance.
  * An iPad 2 (2011) can easily display hundreds of gauges and numbers in one page, with time to spare (rendering time around 50ms).

* Backend
  * CPU performance for basic metrics extraction (CPU, memory, etc) is fairly good. For reference here are a few numbers for configurations monitoring cpu, RAM, HDD and Net throughputs as well as available free space on all drives:
    * An install on a Raspberry Pi 1 B+, the process consumes less than 1.5% of CPU.
    * An install on an Atom N270 (A low power processor dating back to 2008) is consuming less than 0.75% CPU.
    * An install on an old i5 760 (2010) consumes less than 0.1% CPU.
    * On a modern CPU, such as a core i7-10700K, the CPU consumption is around 0.01%.
  * RAM consumption is around 170MB for a simple instance. There is probably little one can do to make that less, with ~100MB overhead from the JVM . On a more loaded instance, monitoring 8 external computers, with news, stocks, weather, spotify and lots of stuff, the memory usage climbs to 195MB.

Note: You should treat these numbers as orders of magnitude, not precise measurements.

## Project status

This project is being setup and is not yet considered as stable. While the product works and can already bring a lot of value, I don't feel it is mature yet. So for now, breaking changes are going to be frequent and without any warning, so be aware.

If you want to try it out and use it, please, by all means do. I'm not going to break *everything* on a daily basis. But if you want to deploy this on your infrastructure and really invest in it, please start a discussion so I can guide you through what is stable and what is not at this stage. Good news is, most is stable.

## What does it look like?

Ok, enough talks about the theory. How does it look?

Here is my current setup below. And here is [a link to a page detailing everything that you see](doc/MY_SETUP.md).
![](https://pieroxy.net/conkw/screenshots-doc/conkw_setup_raw.png?)

## Documentation

[Getting started here](doc/INDEX.md)
