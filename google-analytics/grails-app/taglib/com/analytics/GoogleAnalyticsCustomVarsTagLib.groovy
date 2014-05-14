package com.analytics

class GoogleAnalyticsCustomVarsTagLib extends BaseTagLib {

	static final VISITOR_SCOPE = 1
	static final VISIT_SCOPE = 2
	static final PAGE_SCOPE = 3

	static namespace = "ga"

	def customVar = {attrs ->
		if (isEnabled()) {
			out << customVarFor(attrs.slot,attrs.var_name, attrs.var_value,attrs.scope)
		}
	}

	private customVarFor(slot_index,var_name,var_value,scope){
		
            out << render (template: '/customVarCode',
            			   plugin: 'google-analytics',
                           model: [
                           	slot_index: slot_index,
                           	var_name: var_name,
                           	var_value: var_value,
                           	scope: scope
                           ])
	}

}