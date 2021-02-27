# Roadmap
Here are the things we have in mind for conkw. This is kind of my TODO list.

## UI
* Explode default UI on the server side to allow easy cloning.
* Allow to override UI elements - today you need to mess with files that are going to be overriden at the next update.

## Grabbers

Those existing that need to be ported to conkw (so, expect them soon)
* *External Metrics Ingestion*: allow an external system to push metrics
* *News* Grab news headlines, details being configurable.
* *OpenWeatherMap* Grab weather forecasts.
* *Spotify* Grab the music currently playing.

Those planned (so expect them later)
* lm-sensors
* Calendar Google
* Calendar Outlook
* Mail Google
* Mail Outlook
* Freebox
* https cert expiration date
* Fetch another instance on the network and ingest all or part of its metrics
* Push metrics to another instance on the network through Emi (See above)

## Misc
* https support with live reload of the certificates.
* Authenticate calls with a private shared token.
* Most of this stuff needs to be tested.

## Documentation
* Write it.
