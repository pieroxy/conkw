# conkw documentation - what can you monitor ?

## Status

This project is being developped, so a lot more is coming.

## So, what can conkw monitor ?

ConkW comes with a (soon) lot of grabbers. Grabbers are classes which can monitor one thing or class of things. Music playing, stock market, news, system metrics, weather forecast, etc. You can read more about this in the following document: [List of all existing grabbers](LIST_GRABBERS.md).

But this list is not restrictive. There are many ways to monitor other things with ConkW. This is also handled by grabbers, that can ingest metrics from the outside, either by reading a file that can be generated by any other process, or by listening to a REST api. You can read more about this in the following document: [Monitor other stuff](EXTERNAL_MONITORING.md).

Last, you can also write your own grabber. Running nativaly inside the JVM, it may be easier to write, or just more efficient than a shell script that will spawn countless processes, `grep`, `sed`, `awk`... to get the same result as a few lines of Java. You can read more about this in the following document: [Write your own grabber](WRITE_A_GRABBER.md). And if you did and you think it is worth having in the default conkw distribution, you can submit a PR in GitHub