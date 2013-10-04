grails-plugin-google-analytics
==============================


## Introduction

This plugin provides a simple taglib to embed Google Analytics pageview tracking to your Grails application.


## Installation

```
grails install-plugin google-analytics
```

From Grails 2.0
```
plugins {
    compile ":google-analytics:2.1.1"
}
```

## Usage

### Add Web Property ID to Configuration

Add your Web Property ID to grails-app/config/Config.groovy:

```
google.analytics.webPropertyID = "UA-xxxxxx-x"
```


### Include Google Analytics tracking code to your page

Add the <ga:trackPageview /> tag to your view(s). If you want all your pages to include the tracking code, just add it to the main.gsp layout. As recommended by Google place this as last script in the <head> section.

```
<html>
    <head>
        ..
        <ga:trackPageview />
    </head>
    <body>
        ..
    </body>
</html>
```

## Sensible Defaults

The plugin uses sensible defaults. By default, when adding <ga:trackPageview /> to your views/layouts, only running in production will output the tracking code. Thus not in development and test.

This behaviour can be overridden by explicitly enabling/disabling Google Analytics in Config.groovy.

```
google.analytics.enabled = true
```

or

```
google.analytics.enabled = false
```

## Asynchronous vs. Traditional tracking code

Since version 1.0 of this plugin asynchronous tracking code is used by default when using <ga:trackPageview />.
If you want to use the old traditional tracking code code instead add this to grails-app/config/Config.groovy:

```
google.analytics.traditional = true
```

For traditional tracking code, make sure you put the tag just before the closing </body> tag instead of in the <head> element.

```
<html>
    <head>
        ..
    </head>
    <body>
        ..
        <ga:trackPageview />
    </body>
</html>
```

Note that the plugin also offers <ga:trackPageviewAsynch /> and <ga:trackPageviewTraditional /> tags to use the type of tracking code explicitly. This is mainly for backwards compatibility as the <ga:trackPageviewAsynch /> was needed for asynchronous tracking code prior to version 1.0 of this plugin.

## Tracking Customizations

If you want to customize the tracking code you can either provide the customization in grails-app/config/Config.groovy or in the tag itself. The customization can be a String of javascript code or a smart List with tracking code. The examples below speak for themselves.

#### Simple String configuration in Config.groovy

```
google.analytics.customTrackingCode = "_gaq.push(['_setDetectFlash', false]); _gaq.push(['_trackPageview']); _gaq.push(['_trackPageLoadTime']);"
```

Note that you have the _trackPageview manually when using any custom tracking code.

#### List configuration in Config.groovy

```
google.analytics.customTrackingCode = [
    [_setDetectFlash: false],
    [_setCampaignCookieTimeout: 31536000000],
    "_trackPageview"
]
```

Note that you can also provide a similar List to the tag itself as in the other example.

#### Custom code directly in tag

```
<ga:trackPageview customTrackingCode="_gaq.push(['_setDetectFlash', false]); _gaq.push(['_trackPageview']); _gaq.push(['_trackPageLoadTime']);" />
```

#### Custom to use Jquery for load GA when DOM is ready. Avoid bad position of ga tag ,such before css and JS making the site load with errors and slowly. (old version support, by default do not use dom ready)
```
	grailsApplication.config.google.analytics.jQueryDomReady = true 
```

#### Use GA custom vars
```
	<ga:customVar slot="3" var_name="test" var_value="checking" scope="1" />
	<ga:customVar slot="4" var_name="test" var_value="wrong" scope="3" />
```



## TO DO
* Improve custom vars, control slots usage...


## Version History
* **2.1.1**
	* Support for custom vars
	* Update main async tracking code
	* Support for load tracking code when JqueryDom is ready
	* Support for add custom var when JqueryDom is ready

* **2.0**
	* Remove log.warn of deprecated ConfigurationHolder
	* Upgraded to Grails 2.0.x

* **1.0**
	* Added support for customized tracking code
	* Asynch tracking code is now used instead of traditional code
	* Upgraded to Grails 1.3.x

* **0.3**
	* Now supports Grails version 1.1.1 > *

* **0.2**
	* Added Asynchronous Tracking support

* **0.1**
	* First official release