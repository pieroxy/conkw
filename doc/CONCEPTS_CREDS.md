# conkw documentation - Concepts / credentials

Some grabbers, or even conkw itself, may need credentials. Those are best known as `username/password` pairs. Given the sensitive natures of such data, having them right in the middle of the main config file is not necessarily a good idea. As a result, they can either be inlined in the config file (for better ease of use) or stored in a separate file (for easier security handling).

### Example of credentials externalized

* `config.jsonc`
```json
{
  "grabbers":[
    {
      "implementation":"net.pieroxy.conkw.webapp.grabbers.email.LatestUnreadMailsGrabber",
      "name":"mails",
      "config": {
        "ttl":"5m", // Default value
        "maxMessages":10, // Default value
        "accounts": [
          {
            "name": "my gmail account",
            "server":"imap.googlemail.com",
            "port":993,
            "credentialsRef":"gmailUser1"
          }
        ]
      }
    },
  ],
  "apiAuth":{
    "auth":false, // Set to true to activate API authentication 
    "sessionLifetime":"1y", // Delay after which every session expires.
    "sessionInactivityTimeout":"30d", // Delay after which every unused session expires.
    "users":[ // You can define here many users.
      {
        "credentialsRef":"interfaceUser"
      }
    ]
  },
  ...
}
```
* `credentials.jsonc`
```json
{
  "store":{
    "gmailUser1": {
      "id":"my_account@gmail.com",
      "secret":"password",
      "accessibleTo":[
        "net.pieroxy.conkw.webapp.grabbers.email.SpecificEmailCheckGrabber",
        "net.pieroxy.conkw.webapp.grabbers.email.LatestUnreadMailsGrabber"
      ]
    },
    "interfaceUser": {
      "id":"my_super_admin_user",
      "secret":"password",
      "accessibleTo":[
        "net.pieroxy.conkw.webapp.servlets.Api"
      ]
    }
  }
}
```

Please note:

* Each credentials in the `credentials.jsonc` is referred to by a name
* Each credentials are limited in scope to a set of grabbers, specified in the file. This to avoid all credentials being exposed to all grabbers
* To define credentials to be used in the API authentication, use the class `net.pieroxy.conkw.webapp.servlets.Api`

### Example of credentials inlined

* `config.jsonc`
```json
{
  "grabbers":[
    {
      "implementation":"net.pieroxy.conkw.webapp.grabbers.email.LatestUnreadMailsGrabber",
      "name":"mails",
      "config": {
        "ttl":"5m", // Default value
        "maxMessages":10, // Default value
        "accounts": [
          {
            "name": "my gmail account",
            "server":"imap.googlemail.com",
            "port":993,
            "credentials":{
              "id":"my_super_admin_user",
              "secret":"password"
            }
          }
        ]
      }
    },
  ],
  "apiAuth":{
    "auth":false, // Set to true to activate API authentication 
    "sessionLifetime":"1y", // Delay after which every session expires.
    "sessionInactivityTimeout":"30d", // Delay after which every unused session expires.
    "users":[ // You can define here many users.
      {
        "credentials":{
          "id":"my_super_admin_user",
          "secret":"password"
        }
      }
    ]
  },
  ...
}
```
