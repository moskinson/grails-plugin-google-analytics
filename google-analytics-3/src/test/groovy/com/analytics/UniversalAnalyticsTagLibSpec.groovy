package com.analytics

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.util.Environment
import grails.util.Holders

@TestFor(UniversalAnalyticsTagLib)
class UniversalAnalyticsTagLibSpec extends Specification {
	
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

    void setup(){
        tagLib = applicationContext.getBean(UniversalAnalyticsTagLib)
        Holders.config = [:]
    }

    def "when universal analytics is default disabled in development env"() {
        given:
            setEnvironment(Environment.DEVELOPMENT)
            setConfigVariables()

        expect: 
            applyTemplate("<ua:trackPageview />") == ''
    }

    def "when universal analytics is active in development env"() {
        given:
            setEnvironment(Environment.DEVELOPMENT)
            setConfigVariables([enabled : true])

        expect: 
            deleteBlankSpaces(applyTemplate("<ua:trackPageview />")) == deleteBlankSpaces(expectedAsynch)
    }

    def "when universal analytics is default disabled in test env"() {
       given:
            setEnvironment(Environment.TEST)
            setConfigVariables()

        expect: 
            applyTemplate("<ua:trackPageview />") == ''
    }

    def "when universal analytics is active in test env"() {
        given:
            setEnvironment(Environment.TEST)
            setConfigVariables([enabled : true])

        expect: 
            deleteBlankSpaces(applyTemplate("<ua:trackPageview />")) == deleteBlankSpaces(expectedAsynch)
    }
    
    def "when universal analytics is default enabled in production env"() {
        given:
            setEnvironment(Environment.PRODUCTION)
            setConfigVariables()

        expect: 
            deleteBlankSpaces(applyTemplate("<ua:trackPageview />")) == deleteBlankSpaces(expectedAsynch)
    }

    def "when universal analytics is explicit disabled in production env"() {
        given:
            setEnvironment(Environment.PRODUCTION)
            setConfigVariables([enabled : false])

        expect: 
            applyTemplate("<ua:trackPageview />") == ''
    }

    def "when universal analytics pageview is enabled"() {
       given:
            setConfigVariables([enabled : true])

        expect: 
            deleteBlankSpaces(applyTemplate("<ua:trackPageview />")) == deleteBlankSpaces(expectedAsynch)
    }

    def "when universal analytics pageview is disabled"() {
       given:
            setConfigVariables([enabled : false])

        expect: 
            applyTemplate("<ua:trackPageview />") == ''
    }

    def "when universal analytics pageview is enabled and has not web propery ID"() {
        given:
            setConfigVariables([enabled : true, webPropertyID: null])

        expect: 
            applyTemplate("<ua:trackPageview />") == ''
    }

    def "when universal analytics pageview is enabled and has web propery ID as an attribute"() {
        given:
            setConfigVariables([enabled : true, webPropertyID: ['UA-123456-1']])

        expect: 
            applyTemplate("<ua:trackPageview webPropertyID='UA-123456-2'/>").contains("ga('create', 'UA-123456-2', 'auto');")
    }

    def "when universal analytics pageview has custom tracking code"() {
        given:
            setConfigVariables([enabled : true, webPropertyID: ['UA-123456-1']])

        expect: 
            applyTemplate("""<ua:trackPageview customTrackingCode="{'cookieDomain': 'foo.example.com','cookieName': 'myNewName','cookieExpires': 20000}"/>""").contains("ga('create', 'UA-123456-1', {'cookieDomain': 'foo.example.com','cookieName': 'myNewName','cookieExpires': 20000});")
    }

    def "when universal analytics custom dimensions are stored in pagescope"() {
        given:
            setConfigVariables([enabled : true])
        and:
            tagLib.customDimension(slot: "1",dimension_value: "some value")

        expect:
            pageScope['dimension1'] == [slot: "1",dimension_value:"some value"]
        and: 
            tagLib.trackPageview().contains("ga('set', 'dimension1', 'some value');")
            
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
