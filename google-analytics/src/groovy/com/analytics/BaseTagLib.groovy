package com.analytics

import grails.util.Environment
import grails.util.Holders

public class BaseTagLib {

    def webPropertyID() {
        Holders.config.google.analytics.webPropertyID
    }

	def isEnabled() {
        
        if (!webPropertyID()) {
            return false
        }

        if ( isInvalidEnabledByConfig() && areInProduction() ) {
            return true
        }

        return isEnabledByConfig()
    }

    private isInvalidEnabledByConfig(){
        !(isEnabledByConfig() instanceof Boolean)
    }

    private areInProduction(){
        Environment.current == Environment.PRODUCTION
    }

    private isEnabledByConfig(){
        Holders.config.google.analytics.enabled
    }
}