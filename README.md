grails-plugin-google-analytics
==============================

* v2.1
- Support for custom vars
- Update main asyn tracking code
- Support for load tracking code when JqueryDom is ready
- Support for add custom var when JqueryDom is ready

example:
grailsApplication.config.google.analytics.jQueryDomReady = true (old version support)
<ga:customVar slot="3" variable_name="test" variable_value="checking" scope="1" />
<ga:customVar slot="4" variable_name="test" variable_value="wrong" scope="3" />