package com.analytics

import grails.test.*
import grails.util.Environment
import grails.test.mixin.*
import org.junit.*
import grails.util.Holders

@TestFor(UniversalAnalyticsTagLib)
class UniversalAnalyticsTests {
	
    static webPropertyID = "UA-123456-1"

    static expectedAsynch = """
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');


  ga('create', '${webPropertyID}', 'auto');


  ga('send', 'pageview');

</script>"""
    
    def tagLib

    @Before
    void setUp(){
        tagLib = applicationContext.getBean(UniversalAnalyticsTagLib) 
        Holders.config = [:]
    }

    void testTrackPageviewUniversalDefaultDisabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        assert tagLib.trackPageviewUniversal() == ""
    }

    void testTrackPageviewUniversalExplicitlyEnabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled : true])

        assert deleteBlankSpaces(tagLib.trackPageviewUniversal().toString()) == deleteBlankSpaces(expectedAsynch)

    }

    void testTrackPageviewUniversalDefaultDisabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables()

        assert tagLib.trackPageviewUniversal() == ""
    }

    void testTrackPageviewUniversalExplicitlyEnabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled : true])

        assert deleteBlankSpaces(tagLib.trackPageviewUniversal().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewUniversalDefaultEnabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        assert deleteBlankSpaces(tagLib.trackPageviewUniversal().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewUniversalExplicitlyDisabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled : false])

        assert tagLib.trackPageviewUniversal() == ""
    }

    void testTrackPageviewUniversalEnabled() {
        setConfigVariables([enabled : true])

        assert deleteBlankSpaces(tagLib.trackPageviewUniversal().toString()) == deleteBlankSpaces(expectedAsynch)
    }

    void testTrackPageviewUniversalDisabled() {
        setConfigVariables([enabled : false])

        assert tagLib.trackPageviewUniversal() == ""
    }

    void testTrackPageviewUniversalNoWebPropertyIDButExplicitlyEnabled() {
        setConfigVariables([enabled : true, webPropertyID: null])

        assert tagLib.trackPageviewUniversal() == ""
    }

    void testTrackPageviewUniversalWithWebPropertyIDAsAttributte() {
        setConfigVariables([
                        enabled : true, 
                        webPropertyID: ['UA-123456-1']
                            ])

        def ga_tracking_code = tagLib.trackPageviewUniversal( webPropertyID: 'UA-123456-2')

        assert ga_tracking_code.contains("ga('create', 'UA-123456-2', 'auto');")
    }

    void testTrackPageviewUniversalCustomTrackingCodeAsStringAttr() {
        
        setConfigVariables([enabled : true])

        def ga_tracking_code = tagLib.trackPageviewUniversal(customTrackingCode: "{'cookieDomain': 'foo.example.com','cookieName': 'myNewName','cookieExpires': 20000}").toString()
         
        assert  ga_tracking_code.contains("ga('create', 'UA-123456-1', {'cookieDomain': 'foo.example.com','cookieName': 'myNewName','cookieExpires': 20000});")
    }

    void testCustomDomainsInUniversalAreRenderBeforePageview() {
        
        setConfigVariables([enabled : true])

        tagLib.customDimension(slot_index: "1",dimension_value: "some value").toString()
        def ga_tracking_code = tagLib.trackPageviewUniversal()
        
        assert  pageScope['dimension1'] == [slot_index: "1",dimension_value:"some value"]
        assert  ga_tracking_code.contains("ga('set', 'dimension1', 'some value');")
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
