package com.analytics

import grails.test.mixin.TestFor
import grails.util.Environment
import grails.util.Holders
import org.grails.config.PropertySourcesConfig
import spock.lang.Specification

@TestFor(GoogleAnalyticsTagLib)
class GoogleAnalyticsTraditionalTests extends Specification {
	
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
    
    void setup(){
        setConfigVariables()
    }

    void testTrackPageviewDefaultDisabledInDevelopment() {
        given:
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        expect:
        tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewExplicitlyEnabledInDevelopment() {
        given:
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled: true])

        expect:
        tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDefaultDisabledInTest() {
        given:
        setEnvironment(Environment.TEST)
        setConfigVariables()

        expect:
        tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewExplicitlyEnabledInTest() {
        given:
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled: true])

        expect:
        tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDefaultEnabledInProduction() {
        given:
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        expect:
        tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewExplicitlyDisabledInProduction() {
        given:
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled: false])

        expect:
        tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewEnabled() {
        given:
        setConfigVariables([enabled: true])

        expect:
        tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDisabled() {
        given:
        setConfigVariables([enabled: false])

        expect:
        tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewNoWebPropertyIDButExplicitlyEnabled() {
        given:
        setConfigVariables([enabled: true, webPropertyID: null ])

        expect:
        tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewTraditionalWithWebPropertyIDList() {
        given:
        setConfigVariables([
                        enabled : true, 
                        webPropertyID: ['UA-123456-1','UA-123456-2','UA-123456-3']
                            ])

        def ga_tracking_code = tagLib.trackPageviewTraditional()

        expect:
        ga_tracking_code.contains('_gat._getTracker("UA-123456-1");')
        ga_tracking_code.contains('_gat._getTracker("UA-123456-2");')
        ga_tracking_code.contains('_gat._getTracker("UA-123456-3");')
    }


    void testTrackPageviewTraditionalWithWebPropertyIDAsAttributte() {
        given:
        setConfigVariables([
                        enabled : true, 
                        webPropertyID: ['UA-123456-1']
                            ])

        def ga_tracking_code = tagLib.trackPageviewTraditional( webPropertyID: 'UA-123456-2')

        expect:
        ga_tracking_code.contains('_gat._getTracker("UA-123456-2");')
    }

    private setEnvironment(environment) {
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
    
    private deleteBlankSpaces(text_to_clean){
        text_to_clean.trim().replace(' ','')
    }
}
