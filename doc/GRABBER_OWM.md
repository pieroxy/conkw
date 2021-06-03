# conkw documentation - OpenWeatherMapGrabber

This is the weather grabber. It relies on the [one call api](https://openweathermap.org/api/one-call-api) of the [open weather map website](https://openweathermap.org/).

* *Full name:* `net.pieroxy.conkw.webapp.grabbers.openweathermap.OpenWeatherMapGrabber`
* *Default instance name:* `owm`

## Usage
In order to use it, you need to:

* Create an account on their website
* Choose a plan (the free plan works fine),
* Generate an API key
* Use that api key in the config file.
* Search for your favorite city on their website.
* Copy and paste the latitude and longitude in the config file.

## Metrics always extracted:

* `str.location_name` The name of the place configured. This is extremely useful to make sure you're viewing the weather conditions from the proper place.

## Possible extractions:

### current
Extracts the current weather. 

Metrics:

* `num.current_clouds` The percentage of cloud coverage, between 0 and 1.
* `num.current_feels` What the temperature feels like, in degree celsius.
* `num.current_humidity` The percentage of humidity, between 0 and 1.
* `num.current_pressure` The atmospheric pressure, in hPa.
* `num.current_sunrise` The sunrise time, as a timestamp in seconds.
* `num.current_sunset` The sunset time, as a timestamp in seconds.
* `num.current_temp` The current temperature, in degree celsius.
* `num.current_visibility` The visibility, in meters.
* `num.current_wind_deg` The wind direction, in degrees.
* `num.current_wind_speed` The wind speen in km/h.
* `str.current_desc` The description of the current weather conditions. For example `clear sky`.
* `str.current_icon` The icon of the current weather condition, to be used as the `src` property of an `img` tag.
* `str.current_wind_speed_icon` The icon of the current wind speed, to be used as the `src` property of an `img` tag.

### minute
Extracts weather forecast on the next hour, mostly precipitations.

Metric, where `<i>` goes from `0` to `59`:

* `num.minutely_pim_<i>` precipitation volume in mm.

### hour
Extracts weather forecast on the next 48 hours, by the hour.

Metrics, where `<i>` goes from `0` to `47`:

* `str.hourly_desc_<i>` The description in plain text, for example `overcast clouds`.
* `num.hourly_dt_<i>` The timestamp of the forecast, timestamp unix.
* `num.hourly_humidity_<i>` The percentage of humidity, between 0 and 1.
* `str.hourly_icon_<i>` The icon of the weather condition, to be used as the `src` property of an `img` tag.
* `num.hourly_pop_<i>` The probability of precipitations, between 0 and 1.
* `num.hourly_pressure_<i>` The atmospheric pressure, in hPa.
* `num.hourly_rain_<i>` Rain volume for last hour, mm.
* `num.hourly_snow_<i>` Snow volume for last hour, mm.
* `num.hourly_temp_<i>` The temperature, in degree celsius.
* `num.hourly_temp_feels_<i>` What the temperature feels like, in degree celsius.
* `num.hourly_visibility_<i>` The visibility, in meters.
* `num.hourly_wind_speed_<i>` The wind speen in km/h.
* `num.hourly_wind_speed_beaufort_<i>` The wind speen in beaufort scale.
* `str.hourly_wind_speed_icon_<i>` The icon of the wind speed, to be used as the `src` property of an `img` tag.


### day
Extracts weather forecast on the next 7 days, by the day.

Metrics, where `i` goes from `0` to `7`:

* `str.daily_desc_<i>` The description in plain text, for example `overcast clouds`.
* `num.daily_dt_<i>` The timestamp of the forecast, timestamp unix.
* `num.daily_humidity_<i>` The percentage of humidity, between 0 and 1.
* `str.daily_icon_<i>` The icon of the weather condition, to be used as the `src` property of an `img` tag.
* `num.daily_pop_<i>` The probability of precipitations, between 0 and 1.
* `num.daily_pressure_<i>` The atmospheric pressure, in hPa.
* `num.daily_rain_<i>` Rain volume, mm.
* `num.daily_snow_<i>` Snow volume, mm.
* `num.daily_temp_max_<i>` Max daily temperature.
* `num.daily_temp_min_<i>` Min daily temperature.
* `num.daily_wind_speed_<i>` The wind speen in km/h.
* `num.daily_wind_speed_beaufort_<i>` The wind speen in beaufort scale.
* `str.daily_wind_speed_icon_<i>` The icon of the wind speed, to be used as the `src` property of an `img` tag.


### Final notes

There are a few metrics in the API that are not extracted, such as UV, dew point, wind gust, ... 

If you need them, please provide a pull request or just open an issue. For reference, [here is the documentation for the api](https://openweathermap.org/api/one-call-api).
