# conkw documentation - HttpsCertGrabber

This is the https cert grabber. It will probe the domain names provided through http, grab the certificate and extract the expiration date. This allows monitoring of your https certs expiration date. It refreshes its values every three hours by default.



* *Full name:* `net.pieroxy.conkw.webapp.grabbers.HttpsCertGrabber`
* *Default instance name:* `httpscert`

## Use cases

* You want to monitor the expiration dates of some https certificates.
* You want a way to make sure your Let's Encrypt certs auto-renewal scripts work.

## Configuration
The configuration property is `names`. It contains a comma separated list of domain names. Additionally, it can contain a colon and a port number for those services that do not listen on the port 443.

For example: `"names":"apple.com,google.com:443,microsoft.com"`

As this is a `TimeThrottledGrabber`, you can define `ttl` and `errorTtl`. [See here for more details](CONFIGURE.md). The default ttl is 3 hours.

## Metrics

For each domain name (here `mysite.com`), this grabber will produce three metrics:

* `num.date_mysite.com` contains the unix timestamp in ms of the expiry date.
* `num.days_mysite.com` contains the number of days until expiration.
* `num.ts_mysite.com` contains the number of milliseconds until expiration.

It also produces the list of all names, as defined in the configuration:

* `str.alldomains` the comma separated list of domains in the configuration file.