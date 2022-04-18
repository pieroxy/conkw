# conkw documentation - LatestUnreadMailsGrabber

This is the latest unread emails grabber. It fetches your unread emails from one or more IMAP sources and selects the most recent.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.email.LatestUnreadMailsGrabber`
* *Default instance name:* `mails`

## Use cases

* You want to display your most recent unread emails (and unread email count) in a dashboard.

## Configuration

```json
{
  "implementation":"net.pieroxy.conkw.webapp.grabbers.email.LatestUnreadMailsGrabber",
  "name":"mails",
  "config": {
    "ttl":"5m",
    "maxMessages":10,
    "accounts": [
      {
        "name": "my gmail account",
        "server":"imap.googlemail.com",
        "port":993,
        "credentialsRef":"myUserLoginPassword"
      },
      {
        "name": "my hotmail account",
        "server":"outlook.office365.com",
        "port":993,
        "credentialsRef":"TheUserCredentials"
      }
    ]
  }
}
```

* As this is a `TimeThrottledGrabber`, you can define `ttl` and `errorTtl`. [See here for more details](CONFIGURE.md). The default ttl is 5 minutes.
* `maxMessages` is the maximum number of messages you want to return for the UI. Default value is 10.
* `accounts` is the list of email accounts configurations.
    * Mails wil be mixed in the output, ordered by their receive date.
    * Each configuration is :
      * `name` is the name used for this configuration. Used for the UI to differentiate sources.
      * `server` is the server. Above you can find examples for outlook and gmail.
      * `port` is the port, usually 993.
      * `credentialsRef` Credentials for your user account. See [all about credentials](CONCEPTS_CREDS.md) for details
      * Note that the password of the credential is the password for the account. For Outlook accounts, it is the password of your account. For gmail accounts, you must [setup an application password](https://support.google.com/accounts/answer/185833) for ConkW and put it here.

## Metrics

Here are the metrics this grabber outputs:

* `num.mails_len` The number of emails returned. Will be betwen zero and `maxMessages-1`.
* `num.mails_unread_total` The total number of unread emails in the accounts configured.
* `str.grab_status` As grabbing can be slow, this grabber exposes its status. It can be `Loaded`, `Initializing` or `Loading...`. It is not recommended to use these values in a switch as those are labels subject to change or localization. Moreover, the `Loading` status will vary having zero to 3 dots at the end to mimic an animation.

For each email returned, the following metrics are exposed (Here for the 1st email, `0`):

* `num.mail_0_date` The timestamp of the email received date.
* `str.mail_0_account` The account this email came from. This is the name on the `imapConf` property. See the "Configuration" section above for more details.
* `str.mail_0_from` The email of the sender, and name if available, in the form `Name <email>`.
* `str.mail_0_subject` The subject of the email.
* `str.mail_0_name` The name of the account.
