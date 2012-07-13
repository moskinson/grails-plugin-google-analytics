class GoogleAnalyticsGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]

    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Grails Google Analytics Plugin" 
    def author = "Marcel Overdijk, Javier Moscardó"
    def authorEmail = "marceloverdijk@gmail.com, moskinson@gmail.com"
    def description = '''
                Include google analyics script
    '''

    def documentation = "http://grails.org/plugin/google-analytics"

}
