class GoogleAnalyticsGrailsPlugin {
    def version = "2.1.1"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]

    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def license = "APACHE"
    def title = "Grails Google Analytics Plugin" 
    def author = "Marcel Overdijk"
    def authorEmail = "marceloverdijk@gmail.com"
    def developers = [
            [ name: "Javier Moscardó", email: "moskinson@gmail.com" ] ]

    def scm = [ url: "https://github.com/moskinson/grails-plugin-google-analytics" ]
    def description = '''
                Include google analyics script, async and traditional
                Add custom vars
    '''

    def documentation = "http://grails.org/plugin/google-analytics"

}
