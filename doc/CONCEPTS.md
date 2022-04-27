# conkw documentation - Concepts

## Duration format

Here and there in the config file or in the UI, you will have to configure durations or delays. They can be expressed in milliseconds, seconds, minutes, hours, days or years. Here are a few examples:

* `250ms` a quarter of a second
* `30s` 30 seconds
* `5m` 5 minutes
* `1h` one hour
* `3d` three days
* `1y` one year

Notes:

* A year is defined as being 365 days.
* A day is defined as being 24 hours.
* An hour is defined as being 60 minutes.
* A minute is defined as being 60 seconds.
* You cannot combine several units in the same string, and every number is an integer. To express one minute and 30 seconds, you can use the seconds unit and write `90s`. You cannot write `1m30s` or `1.5m`

## Credentials

Some grabbers, or even conkw itself, may need credentials. See [this dedicated page](MONITOR.md) to figure how to make them available to conkw.

## Formatting numbers

**WARNING**: Number formatting isn't today at a level I think would be acceptable. This is going to change at some point. The good news is that since the actual formatting is just a list of keywords, backward compatibility will be trivial so you can use whatever is in there knowing it will be supported for a long time.

Number formatting is expressed by a keyword in the directive of an expression. That's when you need your expression to be formatted, not to be used as a number of course.

Here I will just list the keywords that actually exists and a few usage examples

Note that in order to test this formatting by yourself, you can just open any ConkW page, press F12, open the console and type `ConkW.getProperLabel("datarate",100)` where the first argument is the keyword and the second one is the value you want to format.

As a last note, be aware that some of these formattings output a space padded string to provide easily aligned numbers. They are meant to be displayed in a `cw-label` that has a CSS of `white-space: pre`, 

* `size` Data size (HDD, RAM, etc.), on 6 characters
    * `1.1` -> `1.10B&nbsp;`
    * `1024` -> `1.00KB`
    * `10241254745` -> `9.54GB`
* `datarate` Data rate per second, 8 characters
    * `10241254745` -> `9.54GB/s`
    * `1024` -> `1.00KB/s`
    * `100` -> `100.B&nbsp;/s`
* `temp` Temperature in °C
    * `1024` -> `1024&deg;C`
* `wtemp` Temperature in °
    * `10` -> `10&deg;`
* `cpu` Percentage of CPU usage, 4 characters. Should be between 0 and 100.
    * `10` -> `10.0%`
    * `1` -> `1.00%`
    * `100` -> `100.%`
* `prc` Alias for `cpu`
* `prc01` Same as `prc` but the input is between 0 and 1.
    * `0.01` -> `1.00%`
    * `1` -> `100.%`
    * `0.1` -> `10.0%`
* `stockchangeprc` Percentage with a sign in front, not constant length. 1 means 100%.
    * `-0.1` -> `-10.00`
    * `-3` -> `-300.00`
    * `0.123` -> `+12.30`
* `stockchange` Same as `stockchangeprc` except the input is not divided by 100.
    * `-0.1` -> `-0.10`
    * `-3` -> `-3.00`
    * `0.123` -> `+0.12`
* `time` Display a duration in readable form from a number of seconds
    * `1` -> `1s`
    * `65` -> `1m 5s`
    * `123456` -> `1d 10h 17m 36s`
* `time_ms` Same as `time` but divides the input by 1000 before proceeding.
    * `10` -> `0s`
    * `1000` -> `1s`
    * `65000` -> `1m 5s`
    * `123456000` -> `1d 10h 17m 36s`
* `time_ns` Same as `time_ms` but divides the input by 1000000000 before proceeding (nanoseconds).
* `time_small_ns` Takes a number of nanoseconds and display it as a number of milliseconds with 6 digits. After 1e12, number get truncated and the display is erroneous.
    * `1000` -> `0.0010ms`
    * `1231000` -> `1.2310ms`
    * `1.23e9` -> `1230.0ms`
    * `1000` -> `0.0010ms`
* `load` Output the number, padded on the right with spaces to a 5 chars long string. Works with positive numbers below 100. Spaces are represented with `_` for better readability.
    * `1` -> `1____`
    * `1.2` -> `1.2__`
    * `1.2356` -> `1.236`
    * `10.2356` -> `10.24`
    * `100.2356` -> `100.236`
* `rpm` Format the number as an int padded on the keft in a 4 char string and append ` rpm` to it. Works with numbers below 10k. Spaces are represented with `_` for better readability.
    * `1000` -> `1000_rpm`
    * `1` -> `___1_rpm`
    * `1.23456` -> `___1_rpm`
    * `10000` -> `10000 rpm`
* `rawint` Output the int representation of the number, rounded.
    * `1123456.8` -> `1123457`
    * `1123.8` -> `1124`
* `hhmm` Converts a number of minutes to a "HH:MM" label. Turns out it also works to convert a number of seconds to a "MM:SS" label and is used as such to display spotify times.
    * `60` -> `01:00`
    * `65` -> `01:05`
    * `12` -> `00:12`
    * `5341` -> `89:01`
* `tstohhmmss` Extract the hour, minute and second of a millisecond timestamp and format it accordingly
    * `1624784665944` (The result of `Date.now()` in my browser at the time of this writing) -> `11:04:25`
    * `1612032547850` -> `19:49:07`
* `tstohhmm` Same as above, without the seconds.
    * `1624784665944` (The result of `Date.now()` in my browser at the time of this writing) -> `11:04`
    * `1612032547850` -> `19:49`
* `tsstohhmm` Same as above but with a timestamp in seconds.
    * `1624784665` -> `11:04`
    * `1612032547` -> `19:49`
* `tsstoh` Extract the hour part of a timestamp in seconds
    * `1624784665` -> `11h`
    * `1612032547` -> `19h`
    * `1624774665` -> `8h`
* `tstodow` Extract the day of the week part of a timestamp in milliseconds, in English.
    * `1624784665944` (The result of `Date.now()` in my browser at the time of this writing) -> `Sunday`
    * `1612032547850` -> `Saturday`
* `tsstodow` Extract the day of the week part of a timestamp in seconds, in English.
    * `1624784665` -> `Sunday`
    * `1612032547` -> `Saturday`
* `tsstodow3` Same as above but only returns the first three characters.
    * `1624784665` -> `Sun`
    * `1612032547` -> `Sat`
* `tsstodate` Extract the date part of a timestamp in seconds, in the format YYYY-MM-DD
    * `1624784665` -> `2021-06-27`
    * `1612032547` -> `2021-01-30`
* `tstodate` Same as above with a timestamp in milliseconds
    * `1624784665944` (The result of `Date.now()` in my browser at the time of this writing) -> `2021-06-27`
    * `1612032547850` -> `2021-01-30`
* `tsstodatetime` Extract the date and time part of a timestamp in seconds, in the format YYYY-MM-DD HH:MM
    * `1624784665` -> `2021-06-27 11:04`
    * `1612032547` -> `2021-01-30 19:49`
* `tstodatetime` Same as above with a timestamp in milliseconds
    * `1624784665944` (The result of `Date.now()` in my browser at the time of this writing) -> `2021-06-27 11:04`
    * `1612032547850` -> `2021-01-30 19:49`
* `si` Make the number fit in a 5 char string, by appending a SI prefix if needed. Only works for big numbers (>1). Pad with a space if needed (represented below as a `_`)
    * `0.00000001` -> `0.00_`
    * `115` -> `115._`
    * `123456789` -> `123.M`
    * `1024` -> `1.02K`
* `currencyBig` Use the `si` formatting, with up to 5 significant digits, two decimal places, right-padded in a 8 char string. Spaces are represented as `_`.
    * `1000` -> `1000.00_`
    * `10000` -> `__10.00k`
    * `12.354` -> `__12.35_`
    * `1000000000` -> `1000.00M`
* `yesno` 
    * `1` -> `Yes`
    * Anything else -> `No`

## Error metric

A Metric is deemed to be in an error state if it respect some rule that has been defined in the ``

## Stale metric

TBD