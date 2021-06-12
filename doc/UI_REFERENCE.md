# conkw documentation - UI Reference

## Specific elements

### The `body` tag

The `body` tag containsd global flags for the entire page. It can take two attributes:

* `cw-options` This attribute contains a comma separated list of options for the UI. 
  * `noresize` Prevent the zoom option making sure your UI fit to your screen with no scrollbars.
* `cw-grabbers` This attribute represents a comma separated list of grabbers instance names. [See the configuration guide for more info.](CONFIGURE.md) 

### Specialized tags

Some HTML tags can be used through their ID. ConkW will then override their content (through `innerText` of `innerHTML`) every time it runs. Here is their list:

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

## Generic UI - the grabbed metrics

### Namespace

Any html element can be used to display data with conkw. For an element to be used by ConkW, it must have a `cw-ns` attribute, which stands for ConkW namespace. It is the name of a grabber instance, that must be declared in the `cw-grabbers` part of the `body` tag.

For example:

`<a href="" cw-ns="spotify">Some text</a>`

This instruct ConkW to look for other `cw-` attributes in the tag, and execute them in the context of the spotify data returned by the API.


TO BE CONTINUED