# conkw documentation - Customize the UI

This document exposes basic context in which the ConkW UI operates.

If you're already familiar with all this, hop on to the [UI Reference](UI_REFERENCE.md)

## Global principles

The conkw UI is a set of JavaScript and CSS files that can help you display dashboards in HTML. Any knowledge of these three technologies will help you tremendously in order to play with the UI.

One of the easiest way to build a UI is to look at the default UI of the grabbers and copy/paste it away in your own file. You can mostly do that with little knowledge of HTML, CSS or JavaScript.

Have a look at your conkw installation directory, that we will call `$CONKW_HOME` in these documents:

```shell
$ ls $CONKW_HOME
bin  config  data  log  tmp  ui  webapp
```

The two directories that are of interest to us are:
* `ui` This is the place to build your own UI. There is already a file in there `index.html`, and it is here for you to play with it. Hack it away, there is nothing in there you won't find back in the default grabbers UI. Updates of conkw will not replace anything you put in there.
* `webapp` This is the place for everything else you see: Documentation and default UI. Note that a reinstallation or an upgrade of conkw will override any change you will make here. You still might want to update the `$CONKW_HOME/webapp/ROOT/index.html` file which is the global index of your instance.

Of course, there is a lot of other ways to build a UI for conkw. After all, there is a REST api (`/api`) that you can call and you might want to write your own UI from scratch, using any technology you want. This documentation focuses on using the default UI for conkw.

## Basic HTML structure

Any file using conkw UI follows the same structure:

```html
<!DOCTYPE html>
<html>
    <head>
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, viewport-fit=cover"/>
        <meta charset="utf-8">
        <meta name="format-detection" content="telephone=no">
        <title>ConkW</title>
        <link href="../css/fonts.css" rel="stylesheet">
        <link href="../css/conkw-common.css" rel="stylesheet">
        <link href="../css/conkw.css" rel="stylesheet">
        <script src="/js/polyfills.js"></script>
        <script src="/js/date.js"></script>
        <script src="/js/index.js"></script>
        <link rel="icon" type="image/png" sizes="256x256" href="/icon.png">
        <link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
        <link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
        <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
        <link rel="manifest" href="/site.webmanifest">
        <link rel="mask-icon" href="/safari-pinned-tab.svg" color="#000066">
        <meta name="msapplication-TileColor" content="#000066">
        <meta name="theme-color" content="#ffffff">
        <style>
          cw-label {
            max-width: 200px;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        </style>
    </head>
    <body onload="ConkW.init()" cw-grabbers="proc,sys,spotify">
        <pagetitle><inlinelogo></inlinelogo><a href="/">CONKW</a></pagetitle>
        <columncontainer>
        ... The content here
        </columncontainer>
    </body>
</html>
```

Here are the details:

`<meta name="apple-mobile-web-app-capable" content="yes" />`

This instruct iOS devices that it can act as a PWA, and adding it to the home screen will hide the address bar.

`<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, viewport-fit=cover"/>`

This instruct mobile devices that the default zoom should be used and that the user cannot zoom/dezoom the UI.

`<meta charset="utf-8">`

The charset of your file.

`<meta name="format-detection" content="telephone=no">`

This instruct iOS devices to not try to hunt for phone numbers to display them as links. This will avoid false positives as well as making the browser faster.

`<title>ConkW</title>`

The title of the document.

`<link href="../css/fonts.css" rel="stylesheet">`

This references the fonts embedded by conkw. The LCD font is in there.

```html
<link href="../css/conkw-common.css" rel="stylesheet">
<link href="../css/conkw.css" rel="stylesheet">
```

This includes the styles of the default conkw UI.

`<script src="/js/polyfills.js"></script>`

This file includes a few polyfills. Only useful if you plan on using ConkW on an old browser. Not really harmful on modern browsers though.

```html
<script src="/js/date.js"></script>
<script src="/js/index.js"></script>
```

This includes the conkw UI javascript part.

```html
<link rel="icon" type="image/png" sizes="256x256" href="/icon.png">
<link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
<link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
<link rel="manifest" href="/site.webmanifest">
<link rel="mask-icon" href="/safari-pinned-tab.svg" color="#000066">
<meta name="msapplication-TileColor" content="#000066">
<meta name="theme-color" content="#ffffff">
```

This is the app icon, for multiple different devices.

`<style></style>`

You might want to override some styles in your file. This is a nice place to do it. Note that if you plan on having several html files, you might want to store your own CSS in an external files for better maintenability.

`<body onload="ConkW.init()" cw-grabbers="proc,sys,spotify">`

This is the body tag. Two things are required here. First you need to initialize ConkW. And you need to specify which grabbers it should fetch.

`<pagetitle><inlinelogo></inlinelogo><a href="/">CONKW</a></pagetitle>`

This is the default way to draw the page title.

```html
<columncontainer>
     ... The content here
</columncontainer>
```

The `columncontainer` element allows a column based display, that will adapt to your screen size. Columns are 350px wide.

## HTML Elements

Basically, ConkW UI will read stuff in html attributes prefixed by `cw-` to figure out where to render its data. CW is a shortcut for conkw. 


For example, let's have a look at the following `hgauge` element:

```html
<hgauge cw-bgcolor="#202040" 
        cw-ns="proc" 
        cw-hgauge0="#474:m:num::ramUsed" 
        cw-hgauge1="#252:m:num::ramCached" 
        cw-min="l:num::0" 
        cw-max="m:num::ramTotal" 
        style="height:3em;">
</hgauge>
```

In there, the `style` attribute is the regular html one, and all the other attributes are metadata for conkw rendering engine.


## UI Reference

To see all available attributes and their meaning, hop on to the [UI Reference](UI_REFERENCE.md)
