# conkw documentation - LatestUnreadMailsGrabber

This is the latest unread emails grabber. It fetches your unread emails from one or more IMAP sources and selects the most recent.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.LatestUnreadMailsGrabber`
* *Default instance name:* `mails`


## Configuration
```json
{
    "implementation":"net.pieroxy.conkw.webapp.grabbers.LatestUnreadMailsGrabber",
    "parameters": {
        "ttl":"5m",
        "imapConf0":"gmail:imap.googlemail.com:993:my_account@gmail.com:mypassword",
        "imapConf1":"hotmail:outlook.office365.com:993:my_email@hotmail.com:mypassword" 
    }
}
```

## Metrics
