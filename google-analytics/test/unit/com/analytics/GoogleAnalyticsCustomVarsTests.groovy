package com.analytics

import grails.test.*
import grails.test.mixin.*
import grails.util.Environment
import org.junit.*
import grails.util.Holders

@TestFor(GoogleAnalyticsCustomVarsTagLib)
class GoogleAnalyticsCustomVarsTests {
	
    static webPropertyID = "UA-123456-1"
    
    def tagLib

    @Before
    void setUp(){
        tagLib = applicationContext.getBean(GoogleAnalyticsCustomVarsTagLib) 
        Holders.config = [:]
    }

    void testNotDisplayCustomVarWhenIsNotEnabled() {
        setConfigVariables([enabled : false])

        assert tagLib.customVar([slot: 1,var_name: 'member_type', var_value: 'individual', scope: 2]) == ""
    }


    void testDisplayCustomVarWhenIsEnabled() {
        setConfigVariables([enabled : true])

        def custom_var_code = tagLib.customVar([slot: 1,var_name: 'member_type', var_value: 'individual', scope: 2])
        
        assert custom_var_code.contains("_gaq.push(['_setCustomVar',1,'member_type','individual',2]);")
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
