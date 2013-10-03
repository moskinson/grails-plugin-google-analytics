import grails.util.Environment

class GoogleAnalyticsTagLib {

    static namespace = "ga"

    def grailsApplication

    def trackPageview = { attrs ->
        if (grailsApplication.config.google.analytics.traditional) {
            out << trackPageviewTraditional(attrs)
        }
        else {
            out << trackPageviewAsynch(attrs)
        }
    }

    def trackPageviewAsynch = { attrs ->
        if (isEnabled()) {
            out << """
<script type="text/javascript">
    var _gaq = _gaq || [];
    \$(function() {
    _gaq.push(['_setAccount', '${webPropertyID()}']);"""
            
            def customTrackingCode = attrs?.customTrackingCode ?: trackingCode()
            if (customTrackingCode instanceof String) {
                out << """
    ${customTrackingCode}"""
            }
            else if (customTrackingCode instanceof List && !customTrackingCode.isEmpty()) {
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
            else {
                out << """
    _gaq.push(['_trackPageview']);"""
            }

            out << """});
    
    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(ga);
    })();
</script>"""
        }
    }

    def trackPageviewTraditional = { attrs ->
		if (isEnabled()) {
            out << render template: '/traditionalTrackingCode',
                          model: [webPropertyID: webPropertyID()]
        }
    }

    private isEnabled() {
        
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

    private webPropertyID() {
        grailsApplication.config.google.analytics.webPropertyID
    }

    private isEnabledByConfig(){
        grailsApplication.config.google.analytics.enabled
    }

    private trackingCode(){
        grailsApplication.config.google.analytics.customTrackingCode
    }
}
