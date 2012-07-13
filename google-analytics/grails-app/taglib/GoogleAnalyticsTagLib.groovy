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
    _gaq.push(['_setAccount', '${getWebPropertyID()}']);"""
            
            def customTrackingCode = attrs?.customTrackingCode ?: grailsApplication.config.google.analytics.customTrackingCode
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

            out << """
    
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
            out << """
<script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
    try {
        var pageTracker = _gat._getTracker("${getWebPropertyID()}");
        pageTracker._trackPageview();
    }
    catch (err) {
    }
</script>"""
        }
    }

    private isEnabled() {
        def enabled = grailsApplication.config.google.analytics.enabled
        
        // disable google analytics if web property id is not defined 
        if (!getWebPropertyID()) {
            enabled = false
        }
        else { 
            // enable google analytics by default for production environment        
            if (!(enabled instanceof Boolean) && Environment.current == Environment.PRODUCTION) {
                enabled = true
            }
        }

        return enabled
    }

    private getWebPropertyID() {
        return grailsApplication.config.google.analytics.webPropertyID
    }
}
