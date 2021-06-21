# conkw documentation - Concepts

## Duration format

Here and there in the config file or in the UI, you will have to configure durations or delays. They can be expressed in seconds, minutes, hours, days or years. Here are a few examples:

* `30s` 30 seconds
* `5m` 5 minutes
* `1h` one hour
* `3d` three days
* `1y` one year

Notes:

* A year is defined as being 365 days.
* You cannot combine several units in the same string. To express one minute and 30 seconds, you must uise the seconds unit ans write `90s`.
