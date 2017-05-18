package com.analytics

import grails.test.mixin.TestFor
import grails.util.Holders
import org.grails.config.PropertySourcesConfig
import spock.lang.Specification

@TestFor(GoogleAnalyticsCustomVarsTagLib)
class GoogleAnalyticsCustomVarsTests extends Specification {
	
    static webPropertyID = "UA-123456-1"

    void setup(){
        setConfigVariables()
    }

    void testNotDisplayCustomVarWhenIsNotEnabled() {
        given:
        setConfigVariables([enabled : false])

        expect:
        tagLib.customVar([slot: 1,var_name: 'member_type', var_value: 'individual', scope: 2]) == ""
    }


    void testDisplayCustomVarWhenIsEnabled() {
        given:
        setConfigVariables([enabled : true])

        def custom_var_code = tagLib.customVar([slot: 1,var_name: 'member_type', var_value: 'individual', scope: 2])

        expect:
        custom_var_code.contains("_gaq.push(['_setCustomVar',1,'member_type','individual',2]);")
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
