# conkw documentation - Built-in UI available styles

This documentation is about guidelines on the default UIs. To get to the bottom of everything, press F12 and inspect the default UIs.

## Color set

The default conkw UI uses a set of colors that is dark. Whether it's daytime of nighttime, I feel that's the best theme. 

* The background color is `#000032`
* The text color is white
* The error background color is `#f40`
* The stale text color is `#888`
* The gauge default color is `#adffad`
* The gauge in a warning state is of the color `#800000`

As an example:

![](https://pieroxy.net/conkw/screenshots-doc/conkw_setup_colors.png?) 

* `pieroxy.net` is a regular label
* The `pieroxy.net` gauge is a regular gauge. Its color (the default color) is green.
* `o.i.fr` is a "warning" label
* The `o.i.fr` gauge is a gauge in the warning state. Its color is red.
* `i.fr` is a stale metric.
* The `i.fr` gauge is a gauge in the stale state. Its color is grey.

You might want to include your own CSS in your UI files and override one or many of these default styles. If you do, make sure the error and stale states are still easy to identify for the user looking at the dashboard. Looking at a UI that has no data anymore doesn't have a lot of value. The stale state if here to make sure you are aware some values are not present. The error state is also important for the user to be able to witness and detect.

## Fixed Layout

The default UI is meant to have a layout that fit the entire screen. 


## Columns based layout

