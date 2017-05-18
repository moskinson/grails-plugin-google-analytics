package com.analytics

import grails.util.Holders

class UniversalAnalyticsTagLib extends BaseTagLib {

    static namespace = "ua"
    
    def trackPageview = { attrs ->
        
        if (isEnabled()){
            if (attrs?.webPropertyID){
                renderUniversalTrackingCodeFor(attrs.customTrackingCode)(attrs.webPropertyID)
            }
            else{
                forEachWebPropertyIdDo(renderUniversalTrackingCodeFor(attrs.customTrackingCode))
            }
        }
    }

    def customDimension = { attrs ->

        if (isEnabled()) {
            pageScope["dimension${attrs?.slot}"] = [   
                                                    slot: attrs?.slot,
                                                    dimension_value:  attrs?.dimension_value
                                                ]
        }
    }
    
    private collectCustomDimensions(){
        pageScope.getVariableNames()
                        .findAll{ it.toString().startsWith('dimension') }
                        .collect{ page_scope_element -> 

                            pageScope["$page_scope_element"]
                        }
    }

    private renderUniversalTrackingCodeFor(custom_tracking_code){
        return { web_property_id ->

                    out << render (template: '/universalTrackingCode',
                               plugin: 'google-analytics',
                               model: [
                                        webPropertyID: web_property_id,
                                        custom_tracking_code: custom_tracking_code,
                                        customDimensions: collectCustomDimensions()
                                        ])
        }
    }

}
