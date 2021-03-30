# conkw


![](src/web/favicon-16x16.png) Web-based conky-like monitoring platform.

## Conky?
[Conky](https://github.com/brndnmtthws/conky) is a program I've been using for quite a while. But recently I've been wondering if the real estate consumed by its window was really worth it:

* Either the window is docked and it consumes quite a bit of real estate on the screen.
* Either the window is decorating the desktop background and I never see it.

## Conkw?
Conkw was spawned out of a simple idea: We all have an old laptop or tablet lying around, gathering dust. Why not using this otherwise wasted screen real estate to display my much cherished data I used to have on my old Conky setup?

* The easiest client-server setup is a web server and a browser.
* All tablets and laptops have browsers.
* Every reasonably modern computer can host a web server.

## UI Support

* The UI supports iOS Safari down to iOS 9.3.5, hence supporting the iPad 2 and iPhone 4S, both released in 2011 as well as the iPod touch 5th gen released in 2012.
* More tests in progress.

## Back-end support
* Any OS where you can install java 8 or greater. According to [Oracle](https://www.oracle.com/java/technologies/javase/products-doc-jdk8-jre8-certconfig.html) you need something as recent as:
  * Windows Vista SP2 (2007)
  * Windows Server 2008 R2 (2009)
  * Ubuntu Linux 12.04 LTS - both ARM and x86 (2012)
  * Suse Linux Enterprise Server 10 SP2+ (2008)
  * Red Hat Enterprise Linux 5.5 (2010)
  * Mac OS 10.8.3

* Notes:
  * More OS can run Java8, such as Windows XP, MaxOS 10.6 and Ubuntu 10.04 but not from the official download pages. You will need to fiddle a bit and find the proper download.
  * The system grabbers might start misbehaving on very old systems. You will then have to rely on scripts in your crontab to extract the desired data and combine them with the FileGrabber.

## Project status
This project is being imported from a dirty local repository, so hang in there for a few days and something usable should pop up soon.

For now, breaking changes are going to be frequent and without any warning, so be aware.

## Documentation
[Getting started here](doc/INDEX.md)
