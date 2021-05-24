# conkw documentation - Start exploring your conkw

The best place to start exploring the conkw you just installed is by typing `http://localhost:12789/`. Replace `localhost` by the name of the computer you installed conkw on if it is another computer.

There you will find a few things:

## The grabber reference

This is the list of the grabbers available to you in conkw. Each of them has a default UI that will show you what you can do with them. You can basically classify the grabbers in three category:

* The system grabbers. Extracting hdd, network, cpu, ram information and more.
* The external services grabbers. They extract stuff from available APIs on the internet. Spotify, weather, stock, news, etc. They almost all need some level of configuration to work, such as an API token. The exception is the HttpsCertGrabber which extracts the https cert expiration date of a given domain name. No need of an API key for this one.
* The bridges, meant for two different things:
    * Make it easy to import data that is not available to other grabbers. Conkw can read a file to import its content ([here](GRABBER_FILE.md)) or expose a REST API for anyone to call with metrics to inject ([here](GRABBER_EMI.md))
    * Allow a conkw instance to become an agent, running silently, not even exposing endpoints if you don't want to, that grabs data and send it to another central instance for example. You can build a mesh of conkw to monitor a complex setup with plenty of machines.


## The documentation

Maybe you're already there, but maybe you're reading this from github, or directly from the files you checked out. The documentation is also available as a website on your newly installed conkw instance !

## The github

A link to the github of conkw. Issues, community and source code, a precious resource !

## The custom UI

This is where you will customize your UI to your needs and tastes !

## An analog clock

On the left of the page, there is a clock. Click on it and you will change the clock face. There are a few available, we hope you'll get to like at least one.