package google.analytics

import grails.plugins.*

class GoogleAnalyticsGrailsPlugin extends Plugin {
    def version = "2.3.3"
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
                Include google analytics script: Async, Traditional and Universal.
                Add custom vars
    '''

    def documentation = "http://grails.org/plugin/google-analytics"

}
