# conkw documentation - The grabbers

Here are the grabbers included in conkw:

## The system grabbers
Those are grabbing memory, CPU, network, hdd activity and more

* [ProcGrabber](GRABBER_PROC_GRABBER.md)
* [SysGrabber](GRABBER_SYS.md)
* [OshiGrabber](GRABBER_OSHI.md)

## The services built-in grabbers
They grab data from external sources. Weather, music, stock price, etc.

* [SpotifyGrabber](GRABBER_SPOTIFY.md)
* [OpenWeatherMapGrabber](GRABBER_OWM.md)
* [YahooFinanceGrabber](GRABBER_YF.md)
* [BingNewsGrabber](GRABBER_BN.md)
* [HttpsCertGrabber](GRABBER_HTTPS_CERT.md)

## The external grabbers 
They allow your conkw to monitor other data that you will grab yourself. Push that data directly in your conkw, or write it to a file for conkw to parse.

* [FileGrabber](GRABBER_FILE.md)
* [ExternalMetricsGrabber](GRABBER_EMI.md)

## The external grabbers 
Used to monitor a conkw instance from another conkw instance. This is how you monitor several computers metrics in one unique conkw instance.

* [PushToEmiGrabber](GRABBER_PUSH.md)
* [ExternalInstanceGrabber](GRABBER_EIG.md)

