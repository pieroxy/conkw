# conkw documentation - Built-in UI reference

## Specific elements

### The `body` tag

The `body` tag contains global flags for the entire page. It can take a few attributes:

* `cw-options` This attribute contains a comma separated list of options for the UI. 
    * `noresize` Prevent the zoom option making sure your UI fit to your screen with no scrollbars.
* `cw-grabbers` This attribute represents a comma separated list of grabbers instance names. [See the configuration guide for more info.](CONFIGURE.md) 

### Specialized tags

Some HTML tags can be used through their `id`. ConkW will then override their content (through `innerText` or `innerHTML`) every time it runs. Here is their list:

* `cw-delay` This element will be updated with 4 integers in the form `1/2/3|4`, where `1` is the time elapsed since the last API call in ms, `2` is the number of millisecond of the current second on the server when the last call was performed (The UI tries to make it 50 to get fresh data), `3` is the number of millisecond elapsed for the API call and `4` the number of millisecond taken by the rendering engine to render the page.
* `cw-status` The global status of ConkW. `ok` when everything is allright and `n errors` when there are `n` errors to report, displayed in red. A simple click on the element shows a dialog with the errors.
* `cw-zoom` The global zoom level of the UI.

### Date special elements and clock.

To display the date, ConkW look into `cw-date` elements. Their ID will determine what to display in them, quite explicitely:

* `dayoftheweek` ex: `Saturday`
* `dayofmonth` ex: `12`
* `month` ex: `June`
* `year` ex: `2021`

The analog clock is displayed inside a tag that reads `cw-clock`. All is needed is something like:

```html
<cw-clock style="width:240px;height:240px;overflow: hidden;padding-top:3px;margin:auto" onclick="ConkW.dates.rotateClockFace(event, this)"></cw-clock>
```

Where the style is your own choice and the onclick is here to rotate the clockface when the user clicks on it. From there, ConkW will handle everything to have your clock displayed.

## Generic UI - Displaying the grabbed metrics

### Namespace

Any html element can be used to display data with conkw. For an element to be used by ConkW, it must have a `cw-ns` attribute, which stands for ConkW namespace. It is the name of a grabber instance, that must be declared in the `cw-grabbers` part of the `body` tag.

For example:

`<a href="" cw-ns="spotify">Some text</a>`

This instruct ConkW to look for other `cw-` attributes in the tag, and execute them in the context of the spotify data returned by the API.


Those attributes will assign behaviors to the html element. They are heavily based on the ConkW expressions. Let's have a look at them.

### Expressions

Expressions is the way the page author will tell ConkW which value should be used. Values can be used as content, title, color, sioze, etc. Basically any HTML attribute can be used.

Let's take a simple example:

`m:num:time:uptime`

#### The basics

All basic expressions are built on four values, separated by colons `:`. 

* **Class**. This is the type of expression. It can be:
    * `m` means a simple metric name, specified from the grabber specified in `cw-ns`.
    * `l` literal expression, such as "foo" or "2".
    * `e` extended expression. An ES6 template literal expression. Note that these are also supported on browsers that do not support ES6.
* **The data type**. It can be of two types and is mostly used for metrics expressions:
    * `str` A string expression
    * `num` A number expression
* **The directive**. This will decide what to do with the value you're referring to. It depends on the context the expression is used for. For basic values expressions, it is the format of the number represented. Some examples:
    * `time` It takes a timestamp in seconds and output the time span it represents. For example `161` will result in `2m 41s`.
    * `size` Will format the input as a data size. For example `16101` will result in `15.7KB`.
    * `tstohhmmss` Will format the timestamp in an hour-minute-second string. For example `1624091231836` (the time of this writing) will result in `10:27:11`.
    * Note that some directives can contain the `:` character. The `:` needs to be written as `\:`, and conversely if you want the `\` character you will write it `\\`.
    * An empty directive will output whatever value untouched.
* **The value**. The last parameter can contain any character, it ends at the end of the string. Its value will be interpreted according to the **class** of the expression:
    * `l`. The value will be a literal. For numbers `2` or `3e8` for example. For strings, it can literaly be anything.
    * `m`. The value is the name of a metric in the proper datatype of the grabber whose namespace has been specified. See `Namespaces` above. More details below.
    * `e`. The value is an ES6 template literal expression. It can tap on the data provided by the grabber specified. More details below.


#### The metrics expressions

Those are expressions starting with `m:`.

Let's take the example of the `net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcGrabber` grabber, configured with an instance called `proc`. First, let's have a look at the data extracted by this grabber:

```json
{
  "timestamp": 1624092027928,
  "extracted": [
    "hostname",
    "battery",
    "mdstat"
  ],
  "extractor": "ProcGrabber",
  "name": "proc",
  "num": {
    "bat_exists": 0,
    "loadavg3": 1.8,
    "max$write_bytes_sdc": 162325547.09418836,
    "loadavg2": 1.56,
    "max$write_bytes_sdd": 25477120,
    "loadavg1": 2.47,
    "swapTotal": 68719472640
    ...
  },
  "timestamps": {},
  "elapsedToGrab": 23,
  "str": {
    "allbd": "sda,sdb,sdc,sdd,nvme0n1",
    "hostname": "pieroxy-dev",
    ...
  }
}
```

The interesting thing here is the `num` and `str` nodes. Depending on the data type of the expression, one or the other will be used. 

* The expression `m:str::hostname` will be evaluated as `pieroxy-dev`
* The expression `m:num::loadavg3` will be evaluated as `1.8`
* The expression `m:num:size:max$write_bytes_sdc` will be evaluated as `155.MB`, as it uses the `time` formatting.

#### The extended expressions

Those are expressions starting with `e:`. We will use the same data as the section above, with the `proc` sample data.

The value of these expressions is basically an ES6 template literal expression. The accessible model is the entire model of the grabber extraction as shown above. 

Examples with the model above:

* `e:num::${num.swapTotal*0.5}` will output `34359736320`, result of the computation of `68719472640*0.5`.
* `e:str::${num.bat_exists==1?'none':'block'}` will output `block`, as the value of `bat_exists` in the `num` datatype is `0`.

As you can see, this unlocks logic that you can apply to the metrics collected by ConkW for the purpose of the UI. All standard JS libraries are available here (such as `Math` for example), but the totality of the global scope is accessible, so any function or object you might add in the `window` object will be available here. The world is your oyster.

For example, if somewhere in my code I define `window.myValue = 35`, then I can use `myValue` as a global variable and the expression `e:num::${myValue}` will be evaluated as `35`.

As an illustration, here is one expression I use to compute the "amount of rain" that will happen:

`e:str::${Math.min(100, 10*num.daily_pop_1 * Math.max(num.daily_rain_1, num.daily_snow_1))}%`

### Value

Identified by the attribute `cw-value`. It will replace the content of the node through the `innerHTML` property, meaning:

* This will effectively wipe whatever was in the `div` originally
* Any HTML in the string will be rendered accordingly by the browser.

Let's look at an example:

```html
<div cw-ns="spotify" cw-value="m:str::album_artist"></div>
```

This empty DIV will be filled with the `album_artist` value of the strings extracted by the `spotify` grabber. After interpolation from the UI, it will look like:

```html
<div cw-ns="spotify" cw-value="m:str::album_artist">AC/DC</div>
```

### Value Warning

Any html tag with the `cw-value` attribute can have a `cw-value-warn` attribute. 

Its directive will be a condition upon which the html tag will be added the class `cw-error`, which renders in a red background in the default CSS. This is used to alert whoever is watching the dashboard of an abnormal condition. 

Here are the exhaustive list of directives one can use in a `cw-value-warn` expression:

* `isnot` will be red if the value is not equal to the expression resolution.
* `is` will be red if the value is equal to the expression resolution.
* `valuecontains` will be red if the value contains the expression resolution.
* `valueabove` will be red if the value is above the expression resolution.
* `valuebelow` will be red if the value is below the expression resolution.

Let's look at some examples in the default UIs provided:

```html
<cw-label cw-value-warn="l:num:valuebelow:20" cw-ns="proc" cw-value="m:num:prc:bat_prc"></cw-label>
```

This html tag will display the percentage of battery remaining in your laptop. It will be displayed on a red background if below 20%.

Note that the expression for the `cw-value-warn` is a literal, and uses the directive `valuebelow`.

```html
<cw-label cw-ns="proc" cw-value-warn="l:str:valuecontains:&lt;" cw-value="m:str::mdstatSummary"></cw-label>
```

This html tag will display the summary string of the mdadm arrays in the system. It will be displayed on a red background if it contains the character `<`. Per the documentation, this happens when the mdadm array is being reconstructed. As the performance of the array is greatly reduced during that time, it's best the user is aware of it.



### Properties based elements

Identified by the attribute `cw-prop-anything`. It will replace the html property `anything` with the value of the expression.

*Note* `cw-prop-innerHTML` is similar to `cw-value` seen above.

Apart from `innerHTML`, any html property can be set by ConkW. The two most useful that come to mind are `title` to define a tooltip, and `src` for images tags to define which image to go fetch. But any property is available. For `style` property, see the section right below.

Here is an example in the default UI for OpenWeatherMap:

```html
<IMG cw-ns="owm" cw-prop-src="m:str::hourly_icon_1" cw-prop-title="m:str::hourly_desc_1"/>
```

ConkW will set both the `title` and `src` property with whatever the owm grabber returns.

### Style based elements

Identified by the attribute `cw-style-anything`. It will replace the style property `anything` with the value of the expression.

Most used style properties are:

* `display` that allows to hide or show stuff depending on the expression provided.
* `width` that allows to set an element width, such as a gauge.
* `height` that allows to set an element height, such as a gauge.

Example with the ProcGrabber default UI:

```html
<div cw-ns="proc" cw-style-display="e:str::${num.bat_exists==1?'none':''}">
  No battery detected
</div>
<div cw-ns="proc" cw-style-display="e:str::${num.bat_exists!=1?'none':''}">
  Some stuff here irrelevant to the example.
</div>
```

So, depending if the `num.battery_remaining_prc` is defined in the oshi grabber

### Stale properties

TO BE DONE

### Gauges

TO BE DONE

### Gauges with history

TO BE DONE

### Multi-values holders

TO BE DONE