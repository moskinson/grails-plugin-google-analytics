import grails.test.*
import grails.util.Environment
import grails.test.mixin.*
import org.junit.*

@TestFor(GoogleAnalyticsTagLib)
class GoogleAnalyticsTagLibTests {
	
	// the web property id to execute the tests with
    static webPropertyID = "UA-123456-1"

   static expectedAsynch = """
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', '${webPropertyID}']);
    _gaq.push(['_trackPageview']);
    
    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(ga);
    })();
</script>"""

    static expectedTraditional = """
<script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
    try {
        var pageTracker = _gat._getTracker("${webPropertyID}");
        pageTracker._trackPageview();
    }
    catch (err) {
    }
</script>"""
    
    def tagLib

    @Before
    void setUp(){
        tagLib = applicationContext.getBean(GoogleAnalyticsTagLib) 
        tagLib.grailsApplication =  [ config: [:] ]
    }

    // *****************************************************
    // * ASYNCH TESTS
    // *****************************************************

    void testTrackPageviewAsynchDefaultDisabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchExplicitlyEnabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled : true])

        assert tagLib.trackPageviewAsynch().toString() == expectedAsynch
    }

    void testTrackPageviewAsynchDefaultDisabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables()

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchExplicitlyEnabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled : true])

        assert tagLib.trackPageviewAsynch().toString() == expectedAsynch
    }

    void testTrackPageviewAsynchDefaultEnabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        assert tagLib.trackPageviewAsynch().toString() == expectedAsynch
    }

    void testTrackPageviewAsynchExplicitlyDisabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled : false])

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchEnabled() {
        setConfigVariables([enabled : true])

        assert tagLib.trackPageviewAsynch().toString() == expectedAsynch
    }

    void testTrackPageviewAsynchDisabled() {
        setConfigVariables([enabled : false])

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchNoWebPropertyIDButExplicitlyEnabled() {
        setConfigVariables([enabled : true, webPropertyID: null])

        assert tagLib.trackPageviewAsynch() == ""
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsStringAttr() {
        setConfigVariables([enabled : true])

        def expected = """
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', '${webPropertyID}']);
    customTrackingCode();
    
    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(ga);
    })();
</script>"""

        assert tagLib.trackPageviewAsynch(customTrackingCode: "customTrackingCode();").toString() == expected
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsListAttr() {
        setConfigVariables([enabled : true])

        def expected = """
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', '${webPropertyID}']);
    _gaq.push(['_setClientInfo', true]);
    _gaq.push(['_setDetectFlash', false]);
    _gaq.push(['_setCampaignCookieTimeout', 31536000000]);
    _gaq.push(['custom', 'value']);
    _gaq.push(['_trackPageview']);
    
    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(ga);
    })();
</script>"""

        assert tagLib.trackPageviewAsynch(customTrackingCode: [[_setClientInfo: true], [_setDetectFlash: false], [_setCampaignCookieTimeout: 31536000000], ["custom": "value"], "_trackPageview"]).toString() == expected
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsStringAttrInConfig() {
        setConfigVariables([enabled : true, customTrackingCode : "customTrackingCode();"])

        def expected = """
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', '${webPropertyID}']);
    customTrackingCode();
    
    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(ga);
    })();
</script>"""

        assert tagLib.trackPageviewAsynch(customTrackingCode: "customTrackingCode();").toString() == expected
    }

    void testTrackPageviewAsynchCustomTrackingCodeAsListAttrInConfig() {
        setConfigVariables([enabled : true,
                            customTrackingCode : [
                                        _setDetectFlash: true,
                                        _setDetectFlash: false,
                                        _setCampaignCookieTimeout: 31536000000,
                                        'custom': 'value',
                                        _trackPageview: null
                                    ]])
       

        def expected = """
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', '${webPropertyID}']);
    _gaq.push(['_setClientInfo', true]);
    _gaq.push(['_setDetectFlash', false]);
    _gaq.push(['_setCampaignCookieTimeout', 31536000000]);
    _gaq.push(['custom', 'value']);
    _gaq.push(['_trackPageview']);
    
    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(ga);
    })();
</script>"""

        assert tagLib.trackPageviewAsynch(customTrackingCode: [[_setClientInfo: true], [_setDetectFlash: false], [_setCampaignCookieTimeout: 31536000000], ["custom": "value"], "_trackPageview"]).toString() == expected
    }

    // *****************************************************
    // * TRADITIONAL TESTS
    // *****************************************************

    void testTrackPageviewDefaultDisabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables()

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewExplicitlyEnabledInDevelopment() {
        setEnvironment(Environment.DEVELOPMENT)
        setConfigVariables([enabled: true])

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDefaultDisabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables()

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewExplicitlyEnabledInTest() {
        setEnvironment(Environment.TEST)
        setConfigVariables([enabled: true])

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDefaultEnabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables()

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewExplicitlyDisabledInProduction() {
        setEnvironment(Environment.PRODUCTION)
        setConfigVariables([enabled: false])

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewEnabled() {
        setConfigVariables([enabled: true])

        assert tagLib.trackPageviewTraditional().toString() == expectedTraditional
    }

    void testTrackPageviewDisabled() {
        setConfigVariables([enabled: false])

        assert tagLib.trackPageviewTraditional() == ""
    }

    void testTrackPageviewNoWebPropertyIDButExplicitlyEnabled() {
        setConfigVariables([enabled: true, webPropertyID: null ])

        assert tagLib.trackPageviewTraditional() == ""
    }

    private setEnvironment(environment) {
        Environment.metaClass.static.getCurrent = { ->
            return environment
        }
    }

    private setConfigVariables(customParams = [:]){
        tagLib.grailsApplication.config = [ 
            google: [
                analytics:[ webPropertyID : "${webPropertyID}"] + customParams
            ]]
    }
}
