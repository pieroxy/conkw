# conkw documentation - BingNewsGrabber

This is the news grabber. It relies on the [rapid api bing news call](https://rapidapi.com/microsoft-azure-org-microsoft-cognitive-services/api/bing-news-search1).

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.bingnews.BingNewsGrabber`
* *Default instance name:* `bingnews`

## Use cases

* You want to display the lates news headlines in some part of the world.

## Usage
In order to use it, you need to:

* Create an account on their website.
* Choose a plan (the Basic plan works fine and is free),
* Get your API key
* Use that api key in the config file.
* Fill in your country ISO 2-letter code in the config file.

To extract news from several countries, add more than once this grabber to your config file.

Note that the basic plan is limited to 1000 requests per month, which amounts roughly to a bit more than 1 request every hour. Note again that if you use several grabbers with the same token, they will all count against the same 1krq/month quota. 

The default refresh rate has been set to once every 1 hour. Adjust this value as you see fit.

## Metrics:

* `num.news_size` The number of headlines returned.

For every news, where _n_ goes from 0 to `num.news_size-1`:

* `news_imageurl_n`: The URL of the image associated to the headline.
* `news_providerimageurl_n`: The URL of the logo of the news provider.
* `news_name_n`: The content of the news.
* `news_url_n`: The URL to get more details on the news.

## Configuration

Here is the sample config portion:
```jsonc
    {
      "implementation":"net.pieroxy.conkw.webapp.grabbers.bingnews.BingNewsGrabber",
      "parameters": {
        "ttl":"1h", // The time between two refreshes. 
        "countrycode":"fr", // To get French news.
        "key":"your bing news rapidapi key here"
      }
    },
```

As this is a `TimeThrottledGrabber`, you can define `ttl` and `errorTtl`. [See here for more details](CONFIGURE.md). The default ttl is 1 hour.

## Final notes


For reference, [here is the playground for the api](https://rapidapi.com/microsoft-azure-org-microsoft-cognitive-services/api/bing-news-search1).
