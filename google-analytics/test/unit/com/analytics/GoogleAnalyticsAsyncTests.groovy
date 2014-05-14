package com.analytics

import grails.test.*
import grails.util.Environment
import grails.test.mixin.*
import org.junit.*
import grails.util.Holders

@TestFor(GoogleAnalyticsTagLib)
class GoogleAnalyticsAsyncTests {
	
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
    
    def tagLib

    @Before
    void setUp(){
        tagLib = applicationContext.getBean(GoogleAnalyticsTagLib) 
        Holders.config = [:]
    }

    void testTrackPageviewAsynchDefaultDisabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchExplicitlyEnabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled : true])

        assert deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)

    }

    void testTrackPageviewAsynchDefaultDisabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables()

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchExplicitlyEnabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled : true])

        assert deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewAsynchDefaultEnabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        assert deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewAsynchExplicitlyDisabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled : false])

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchEnabled() {
        setConfigVariables([enabled : true])

        assert deleteBlankSpaces(tagLib.trackPageviewAsynch().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewAsynchDisabled() {
        setConfigVariables([enabled : false])

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchNoWebPropertyIDButExplicitlyEnabled() {
        setConfigVariables([enabled : true, webPropertyID: null])

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchWithWebPropertyIDList() {
        setConfigVariables([
                        enabled : true, 
                        webPropertyID: ['UA-123456-1','UA-123456-2','UA-123456-3']
                            ])

        def ga_tracking_code = tagLib.trackPageviewAsynch()

        assert ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-1']);")
        assert ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-2']);")
        assert ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-3']);")
    }

    void testTrackPageviewAsynchWithWebPropertyIDAsAttributte() {
        setConfigVariables([
                        enabled : true, 
                        webPropertyID: ['UA-123456-1']
                            ])

        def ga_tracking_code = tagLib.trackPageviewAsynch( webPropertyID: 'UA-123456-2')

        assert ga_tracking_code.contains("_gaq.push(['_setAccount', 'UA-123456-2']);")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsStringAttr() {
        
        setConfigVariables([enabled : true])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: "customTrackingCode();").toString()
         

        assert  ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        assert  ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        assert  ga_tracking_code.contains("customTrackingCode();")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsListAttr() {
        
        setConfigVariables([enabled : true])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: [[_setClientInfo: true], [_setDetectFlash: false], [_setCampaignCookieTimeout: 31536000000], ["custom": "value"], "_trackPageview"]).toString()

        assert  ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        assert  ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        assert  ga_tracking_code.contains("_gaq.push(['_setClientInfo', true]);")
        assert  ga_tracking_code.contains("_gaq.push(['_setDetectFlash', false]);")
        assert  ga_tracking_code.contains("_gaq.push(['_setCampaignCookieTimeout', 31536000000]);")
        assert  ga_tracking_code.contains("_gaq.push(['custom', 'value']);")
        assert  ga_tracking_code.contains("_gaq.push(['_trackPageview']);")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsStringAttrInConfig() {
        
        setConfigVariables([enabled : true, customTrackingCode : "customTrackingCode();"])

        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: "customTrackingCode();").toString()

        assert  ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        assert  ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        assert  ga_tracking_code.contains("customTrackingCode();")
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsListAttrInConfig() {
        
        setConfigVariables([enabled : true,
                            customTrackingCode : [
                                        _setDetectFlash: true,
                                        _setDetectFlash: false,
                                        _setCampaignCookieTimeout: 31536000000,
                                        'custom': 'value',
                                        _trackPageview: null
                                    ]])
        
        def ga_tracking_code = tagLib.trackPageviewAsynch(customTrackingCode: [[_setClientInfo: true], [_setDetectFlash: false], [_setCampaignCookieTimeout: 31536000000], ["custom": "value"], "_trackPageview"]).toString()

        assert  ga_tracking_code.contains("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';")
        assert  ga_tracking_code.contains("_gaq.push(['_setAccount', '${webPropertyID}']);")
        assert  ga_tracking_code.contains("_gaq.push(['_setClientInfo', true]);")
        assert  ga_tracking_code.contains("_gaq.push(['_setDetectFlash', false]);")
        assert  ga_tracking_code.contains("_gaq.push(['_setCampaignCookieTimeout', 31536000000]);")
        assert  ga_tracking_code.contains("_gaq.push(['custom', 'value']);")
        assert  ga_tracking_code.contains("_gaq.push(['_trackPageview']);")
    }

    private setEnvironment(environment) {
        Environment.metaClass.static.getCurrent = { ->
            return environment
        }
    }

    private setConfigVariables(customParams = [:]){
        Holders.config = [ 
            google: [
                analytics:[ webPropertyID : "${webPropertyID}"] + customParams
            ]]
    }
    
    private deleteBlankSpaces(text_to_clean){
        text_to_clean.trim().replace(' ','')
    }
}
