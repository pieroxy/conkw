# Roadmap
Here are the things we have in mind for conkw. This is kind of my TODO list.

## UI
* Allow to override UI elements - today you need to mess with files that are going to be overriden at the next update.
* Find a proper way to describe number formatting. This endless list of keywords isn't really nice.

## Grabbers

Those existing that need to be ported to conkw (so, expect them soon)
* *External Metrics Ingestion*: allow an external system to push metrics
* *News* Grab news headlines, details being configurable.
* *Stock* Grab stock levels for your preferred companies. Also cryptocurrencies.

Those planned (so expect them later)
* lm-sensors - to get temperatures, voltages and fan speeds on linux.
* smartctl - to get metrics out of hdd, specifically their health status.
* MPRIS2 - to get music playing for most Linux players.
* Calendar Google - to get next meetings
* Calendar Outlook - to get next meetings
* Mail Google - to get latest mails
* Mail Outlook - to get latest mails
* Freebox - to get internet status on this home router
* Fetch another instance on the network and ingest all or part of its metrics
* Push metrics to another instance on the network through Emi (See above)

## Misc
* https support with live reload of the certificates.
* Authenticate API calls with a private shared token.
* Most of this stuff needs to be tested.

