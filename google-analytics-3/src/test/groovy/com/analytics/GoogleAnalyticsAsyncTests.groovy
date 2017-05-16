package com.analytics

import grails.test.mixin.TestFor
import grails.util.Environment
import grails.util.Holders
import org.grails.config.PropertySourcesConfig
import spock.lang.Specification

@TestFor(GoogleAnalyticsTagLib)
class GoogleAnalyticsAsyncTests extends Specification {

    static webPropertyID = "UA-123456-1"

    static expectedAsynch = """
<script type="text/javascript">
    var _gaq = _gaq || [];

    _gaq.push(['_setAccount', '${webPropertyID}']);
    _gaq.push(['_trackPageview']);

    (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
</script>"""


    def setup() {
        setConfigVariables()
    }

    void testTrackPageviewAsynchDefaultDisabledInDevelopment() {
        given:
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        expect:
        tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchExplicitlyEnabledInDevelopment() {
        given:
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled: true])

        expect:
        deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)

    }

    void testTrackPageviewAsynchDefaultDisabledInTest() {
        given:
        setEnvironment(Environment.TEST)
        setConfigVariables()

        expect:
        tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchExplicitlyEnabledInTest() {
        given:
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled: true])

        expect:
        deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewAsynchDefaultEnabledInProduction() {
        given:
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        expect:
        deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewAsynchExplicitlyDisabledInProduction() {
        given:
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled: false])

        expect:
        tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchEnabled() {
        given:
        setConfigVariables([enabled: true])

        expect:
        deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewAsynchDisabled() {
        given:
        setConfigVariables([enabled: false])

        expect:
        tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchNoWebPropertyIDButExplicitlyEnabled() {
        given:
        setConfigVariables([enabled: true, webPropertyID: null])

        expect:
        tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchWithWebPropertyIDList() {
        given:
        setConfigVariables([
                enabled      : true,
                webPropertyID: ['UA-123456-1', 'UA-123456-2', 'UA-123456-3']
        ])

        def ga_tracking_code = tagLib.trackPageviewAsynch()


        expect:
        ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-1']);")
        ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-2']);")
        ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-3']);")
    }

    void testTrackPageviewAsynchWithWebPropertyIDAsAttributte() {
        given:
        setConfigVariables([
                enabled      : true,
                webPropertyID: ['UA-123456-1']
        ])

        def ga_tracking_code = tagLib.trackPageviewAsynch(webPropertyID: 'UA-123456-2')

        expect:
        ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-2']);")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsStringAttr() {
        given:
        setConfigVariables([enabled: true])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: "customTrackingCode();").toString()


        expect:
        ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        ga_tracking_code.contains("customTrackingCode();")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsListAttr() {
        given:
        setConfigVariables([enabled: true])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: [[_setClientInfo: true], [_setDetectFlash: false], [_setCampaignCookieTimeout: 31536000000], ["custom": "value"], "_trackPageview"]).toString()

        expect:
        ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        ga_tracking_code.contains("_gaq.push(['_setClientInfo', true]);")
        ga_tracking_code.contains("_gaq.push(['_setDetectFlash', false]);")
        ga_tracking_code.contains("_gaq.push(['_setCampaignCookieTimeout', 31536000000]);")
        ga_tracking_code.contains("_gaq.push(['custom', 'value']);")
        ga_tracking_code.contains("_gaq.push(['_trackPageview']);")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsStringAttrInConfig() {
        given:
        setConfigVariables([enabled: true, customTrackingCode: "customTrackingCode();"])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: "customTrackingCode();").toString()

        expect:
        ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        ga_tracking_code.contains("customTrackingCode();")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsListAttrInConfig() {
        given:
        setConfigVariables([enabled           : true,
                            customTrackingCode: [
                                    _setDetectFlash          : true,
                                    _setDetectFlash          : false,
                                    _setCampaignCookieTimeout: 31536000000,
                                    'custom'                 : 'value',
                                    _trackPageview           : null
                            ]])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: [[_setClientInfo: true], [_setDetectFlash: false], [_setCampaignCookieTimeout: 31536000000], ["custom": "value"], "_trackPageview"]).toString()

        expect:
        ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        ga_tracking_code.contains("_gaq.push(['_setClientInfo', true]);")
        ga_tracking_code.contains("_gaq.push(['_setDetectFlash', false]);")
        ga_tracking_code.contains("_gaq.push(['_setCampaignCookieTimeout', 31536000000]);")
        ga_tracking_code.contains("_gaq.push(['custom', 'value']);")
        ga_tracking_code.contains("_gaq.push(['_trackPageview']);")
    }

    def setEnvironment(environment) {
        Environment.metaClass.static.getCurrent = { ->
            return environment
        }
    }

    def setConfigVariables(customParams = [:]) {
        def config = [
                google: [
                        analytics: [webPropertyID: "${webPropertyID}"] + customParams
                ]]
        Holders.config = new PropertySourcesConfig(config)
    }

    def deleteBlankSpaces(text_to_clean) {
        text_to_clean.trim().replace(' ', '')
    }
}
