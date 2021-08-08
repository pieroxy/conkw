# conkw documentation - SpecificEmailCheckGrabber

This is the specific email check grabber. It fetches an IMAP folder and look for emails following a defined pattern. It will then expose the date of the latest email it could find.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.email.SpecificEmailCheckGrabber`
* *Default instance name:* `specificmail`

## Use cases

* You want to monitor that your [borg](https://borgbackup.readthedocs.io/en/stable/) backup runs and reports no errors. This use-case is detailed below.
* More generally, this grabber considers your IMAP folder as a log and looks inside for the latest message conforming to a pattern.

## Configuration

```json
{
    "implementation":"net.pieroxy.conkw.webapp.grabbers.email.SpecificEmailCheckGrabber",
    "parameters": {
        "ttl":"5m",
        "folder":"YourImapFolder",
        "subjectRegexp":"Specify the subject regexp",
        "senderRegexp":"specify the sender email",
        "bodyRegexp":"Specify the body regexp",
        "imapConf" :"MyEmailAccount!!:imap.googlemail.com:993:my_email@gmail.com:mypassword"
    }
}
```

* `ttl` is the time between two runs. Default is `5m`.
* `folder` is the IMAP folder in which you look for an email
* `subjectRegexp` is the regexp used to check against the mail subject. May be ommited.
* `senderRegexp` is the regexp used to check against the mail sender address. May be ommited.
* `bodyRegexp` is the regexp used to check against the mail body. May be ommited.
* `imapConf` Your IMAP configuration. [See the LatestEmailsGrabber for more information](GRABBER_LATEST_EMAILS.md)

## Metrics

Here is the metric this grabber outputs:

* `num.last_seen` The timestamp of the latest message that matches the pattern.

## Usage with borg backup

### Borg backup ?

[borg](https://borgbackup.readthedocs.io/en/stable/) is a program that runs, deduplicate, compress, encrypt and keep a history of your backups. I'm not going to go into details here, but here is how I operate it:

* I have a crontab entry running every three hours that launches a script.
* This script launches a script inspired from the sample script on borg documentation: [Look for the script in the "Automating Backups" section](https://borgbackup.readthedocs.io/en/stable/quickstart.html#automating-backups)
* It then mails the output of the script to me.
* A filter in my gmail inbox moves the script to a folder and marks it as Read.

So, my script, running every three hours, looks something like:
```sh
#!/bin/bash

sh /somewhere/borg-run.sh > /tmp/backup.log 2>&1
cat /tmp/backup.log | mail -a "Content-Type: text/plain; charset=UTF-8" -s "Backups `date`" my_email@gmail.com
```

### How to use SpecificEmailCheckGrabber for this ?

Here is my configuration for the SpecificEmailCheckGrabber:

```json
{
    "implementation":"net.pieroxy.conkw.webapp.grabbers.email.SpecificEmailCheckGrabber",
    "parameters": {
        "ttl":"1h",
        "folder":"technicalStuff/Backups",
        "subjectRegexp":"Backups .*",
        "bodyRegexp":".*Backup and Prune finished successfully.*",
        "senderRegexp":"email@sending-address",
        "imapConf" :"account:imap.googlemail.com:993:my_email@gmail.com:my_app_password"
    }
}
```

* `ttl:1h` No need to be too agressive. Plus, a run might emmit a warning because a file was locked and it couldn't read it, so we'll set the threshold to 8 hours before assuming there is an issue.
* `folder:technicalStuff/Backups` This is where my gmail filter puts all emails sent by the backup script.
* `subjectRegexp:Backups .*` We wil only consider emails whose subject start with `Backups `.
* `senderRegexp:email@sending-address` I hardcoded the email of the sender.
* `bodyRegexp:.*Backup and Prune finished successfully.*` I only look for emails reporting a successful backup - ie with no warnings or errors. Those will have the sentence `Backup and Prune finished successfully` somewhere in their body.
* `imapConf` That's the configuration of my gmail account.

### What do I get from this ?

* In my dashboard, I have the time since the last successful backup.
* If it goes over 8 hours, it is shown in red. So, I will be alerted if any of the following conditions are true:
    * The borg backup did not fire twice in a row.
    * The borg backup failed twice in a row.
    * The borg backup reported warnings twice in a row.
    * The script failed to send the email twice in a row.
    * I upgraded the borg script and the output now looks different.
    * Conkw failed to get the emails, because I changed my gmail password.

These conditions all need some attention on my part, so it's good I get an alert.