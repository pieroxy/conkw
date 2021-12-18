# conkw documentation - YahooFinanceGrabber

This is the finance grabber. It relies on the [rapid api](https://rapidapi.com/apidojo/api/yahoo-finance1) call.

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.yahooFinance.YahooFinanceGrabber`
* *Default instance name:* `yahoof`

## Use cases

* You want to monitor the stock price of a symbol, somewhere, available on Yahoo Finance.

## Usage
In order to use it, you need to:

* Create an account on their website.
* Choose a plan (the Basic plan works fine and is free),
* Get your API key
* Use that api key in the config file.
* Search for your favorite company or cryptocurrency symbol.
* Copy and paste the symbol and country of exchange in the config file.

Note that if you want to extract more stock prices, you need to create one instance of this extractor per stock price you want.

Note that the basic plan is limited to 500 requests per month, which amounts roughly to 1 request every two hours. Note again that if you use several grabbers with the same token, they will all count against the same 500rq/month quota. Hence, the default refresh rate has been set to once every 5 hours. Adjust this value as you see fit.

## Configuration

Here is the sample config portion:
```jsonc
    {
      "implementation": "net.pieroxy.conkw.webapp.grabbers.yahooFinance.YahooFinanceGrabber",
      "name":"apple",
      "config": {
        "ttl":"5h", // Adjust as you see fit.
        "symbol":"AAPL",
        "region":"US",
        "key":"your yahoo finance rapidapi key here"
      }
    },
```

As this is a `TimeThrottledGrabber`, you can define `ttl` and `errorTtl`. [See here for more details](CONFIGURE.md). The default ttl is 5 hours.

## Metrics:

* `num.priceAvg50Days`: The average price in the last 50 days.
* `num.price`: The current price.
* `num.change`: The daily change, in local currency.
* `num.changeprc`: The daily change in %.

* `str.marketCapFmt`: The total market valuation for the symbol, formatted.
* `str.name`: The human readable name for the symbol.
* `str.currencySymbol`: The symbol of the local currency (used for price and changes).
* `str.currency`: The currency used for price and changes.

## Final notes

There are a *lot* of metrics in the API that are not extracted, such as volume, hints, exchange symbol, etc ... 

If you need them, please provide a pull request or just open an issue. For reference, [here is the playground for the api](https://rapidapi.com/apidojo/api/yahoo-finance1).
