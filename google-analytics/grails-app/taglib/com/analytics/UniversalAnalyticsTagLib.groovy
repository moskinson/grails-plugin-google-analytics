package com.analytics

import grails.util.Holders

class UniversalAnalyticsTagLib extends BaseTagLib {

    static namespace = "ga"
    
    def trackPageviewUniversal = { attrs ->
        
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
            pageScope["dimension${attrs?.slot_index}"] = [   
                                                    slot_index: attrs?.slot_index,
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
