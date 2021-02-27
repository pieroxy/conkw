# Roadmap
Here are the things we have in mind for conkw. This is kind of my TODO list.

## UI
* Explode default UI on the server side to allow easy cloning.
* Allow to override UI elements - today you need to mess with files that are going to be overriden at the next update.

## Grabbers

In progress:
* OSHIGrabber to fetch system metrics on all OSes.

Those existing that need to be ported to conkw (so, expect them soon)
* *External Metrics Ingestion*: allow an external system to push metrics
* *News* Grab news headlines, details being configurable.
* *OpenWeatherMap* Grab weather forecasts.
* *Spotify* Grab the music currently playing.

Those planned (so expect them later)
* lm-sensors - to get temperatures, voltages and fan speeds on linux.
* MPRIS2 - to get music playing for most Linux players.
* Calendar Google - to get next meetings
* Calendar Outlook - to get next meetings
* Mail Google - to get latest mails
* Mail Outlook - to get latest mails
* Freebox - to get internet status on this home router
* https cert expiration date - to make sure your https certs are not about to expire.
* Fetch another instance on the network and ingest all or part of its metrics
* Push metrics to another instance on the network through Emi (See above)

## Misc
* https support with live reload of the certificates.
* Authenticate calls with a private shared token.
* Most of this stuff needs to be tested.

## Documentation
* Write it.
