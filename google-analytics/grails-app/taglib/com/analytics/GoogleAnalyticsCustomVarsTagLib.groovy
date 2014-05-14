package com.analytics

class GoogleAnalyticsCustomVarsTagLib {

	static final VISITOR_SCOPE = 1
	static final VISIT_SCOPE = 2
	static final PAGE_SCOPE = 3
	
	static namespace = "ga"

	def customVar = {attrs ->

		out << customVarFor(attrs.slot,attrs.var_name, attrs.var_value,attrs.scope)
	}

	private customVarFor(slot_index,var_name,var_value,scope){
		"""
		<script type="text/javascript">
			var _gaq =_gaq || [];
			_gaq.push(['_setCustomVar',$slot_index,'$var_name','$var_value',$scope]);
		</script>"""
	}

}