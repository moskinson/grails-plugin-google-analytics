package com.analytics

import grails.test.*
import grails.util.Environment
import grails.test.mixin.*
import org.junit.*
import grails.util.Holders

@TestFor(GoogleAnalyticsTagLib)
class GoogleAnalyticsTraditionalTests {
	
    static webPropertyID = "UA-123456-1"

    static expectedTraditional = """<script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
    try {
        var pageTracker = _gat._getTracker("${webPropertyID}");
        pageTracker._trackPageview();
    }
    catch (err) {
    }
</script>"""
    
    def tagLib

    @Before
    void setUp(){
        tagLib = applicationContext.getBean(GoogleAnalyticsTagLib) 
        Holders.config = [:]
    }

    void testTrackPageviewDefaultDisabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewExplicitlyEnabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled: true])

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDefaultDisabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables()

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewExplicitlyEnabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled: true])

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDefaultEnabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewExplicitlyDisabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled: false])

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewEnabled() {
        setConfigVariables([enabled: true])

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDisabled() {
        setConfigVariables([enabled: false])

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewNoWebPropertyIDButExplicitlyEnabled() {
        setConfigVariables([enabled: true, webPropertyID: null ])

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewTraditionalWithWebPropertyIDList() {
        setConfigVariables([
                        enabled : true, 
                        webPropertyID: ['UA-123456-1','UA-123456-2','UA-123456-3']
                            ])

        def ga_tracking_code = tagLib.trackPageviewTraditional()

        assert ga_tracking_code.contains('_gat._getTracker("UA-123456-1");')
        assert ga_tracking_code.contains('_gat._getTracker("UA-123456-2");')
        assert ga_tracking_code.contains('_gat._getTracker("UA-123456-3");')
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
