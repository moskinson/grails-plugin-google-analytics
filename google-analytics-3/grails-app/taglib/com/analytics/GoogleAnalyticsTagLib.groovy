package com.analytics

import grails.util.Holders

class GoogleAnalyticsTagLib extends BaseTagLib {

    static namespace = "ga"

    def trackPageview = { attrs ->
        if (Holders.config.google.analytics.traditional) {
            out << trackPageviewTraditional(attrs)
        }
        else {
            out << trackPageviewAsynch(attrs)
        }
    }

    def trackPageviewAsynch = { attrs ->
        if (isEnabled()){
            
            if (attrs?.webPropertyID){
                renderAsyncTrackingCodeFor(attrs)(attrs.webPropertyID)
            }
            else{
                forEachWebPropertyIdDo(renderAsyncTrackingCodeFor(attrs))
            }
        }
    }

    def trackPageviewTraditional = { attrs ->
        
        if (isEnabled()){
            if (attrs?.webPropertyID){
                renderTraditionalTrackingCodeFor(attrs.webPropertyID)
            }
            else{
                forEachWebPropertyIdDo(renderTraditionalTrackingCodeFor)
            }
        }
    }

    private renderAsyncTrackingCodeFor(attrs){

        return { web_property_id ->
        out << """
<script type="text/javascript">
    var _gaq = _gaq || [];
    
    _gaq.push(['_setAccount', '${web_property_id}']);"""
            
        def customTrackingCode = attrs?.customTrackingCode ?: trackingCode()           

        if (customTrackingCode){
            renderCustomTrackingCode(customTrackingCode)
        }
        else {
            out << """
    _gaq.push(['_trackPageview']);
    """
        }

        renderGoogleAnalyticsAsyncTrackingCode()

        out << """
</script>"""

        }
    }

    private renderGoogleAnalyticsAsyncTrackingCode(){
        if (!pageScope.renderAsyncTrackingCode){
            pageScope.renderAsyncTrackingCode = true
            out << render (template: '/asyncTrackingCode',
                       plugin: 'google-analytics')
        }
    }

    private renderTraditionalTrackingCodeFor = { web_property_id ->
        out << render (template: '/traditionalTrackingCode',
                       plugin: 'google-analytics',
                       model: [webPropertyID: web_property_id])
    }

    private renderCustomTrackingCode(customTrackingCode){
        if (customTrackingCode instanceof String) {
                out << """
    ${customTrackingCode}"""
        }
        if (customTrackingCode instanceof List && !customTrackingCode.isEmpty()) {
            renderCustomTrackingCodeFromList(customTrackingCode)
        }
    }

    private renderCustomTrackingCodeFromList(customTrackingCode){
        customTrackingCode.each {
                    if (it instanceof Map) {
                        it.each { k, v ->
                            if (v instanceof String) {
                                out << """
    _gaq.push(['${k}', '${v}']);"""
                            }
                            else if (v instanceof Boolean) {
                                out << """
    _gaq.push(['${k}', ${v}]);"""
                            }
                            else if (v) {
                                out << """
    _gaq.push(['${k}', ${v}]);"""
                            }
                            else {
                                out << """
    _gaq.push(['${k}']);"""
                            }
                        }
                    }
                    else {
                        out << """
    _gaq.push(['${it}']);"""
                    }
                }
    }

    private trackingCode(){
        Holders.config.google.analytics.customTrackingCode
    }

}
