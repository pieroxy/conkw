# conkw documentation - HttpsCertGrabber

This is the https cert grabber. It will probe the domain names provided through http, grab the certificate and extract the expiration date. This allows monitoring of your https certs expiration date. It refreshes its values every three hours.



* *Full name:* `net.pieroxy.conkw.webapp.grabbers.HttpsCertGrabber`
* *Default instance name:* `httpscert`

## Use cases

* You want to monitor the expiration dates of some https certificates.
* You want a way to make sure your Let's Encrypt certs auto-renewal scripts work.

## Configuration
The configuration property is `names`. It contains a comma separated list of domain names.

## Metrics

For each domain name (here `mysite.com`), this grabber will produce three metrics:

* `num.date_mysite.com` contains the unix timestamp in ms of the expiry date.
* `num.days_mysite.com` contains the number of days until expiration.
* `num.ts_mysite.com` contains the number of milliseconds until expiration.

It also produces the list of all names, as defined in the configuration:

 `str.alldomains` the comma separated list of domains in the configuration file.