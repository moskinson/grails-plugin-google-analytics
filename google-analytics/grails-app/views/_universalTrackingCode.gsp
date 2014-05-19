<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  <g:if test="${custom_tracking_code}">
  ga('create', '${webPropertyID}', ${custom_tracking_code});
  </g:if>
  <g:else>
  ga('create', '${webPropertyID}', 'auto');
  </g:else>
  ga('send', 'pageview');

</script>