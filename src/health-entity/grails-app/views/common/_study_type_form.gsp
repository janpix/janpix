<r:require module="jquerynestable" />
<r:script>
	$(document).ready(function() {
		$('#study-nestable-${inputname}').nestable();
		$('#study-nestable-${inputname} .dd3-content').click(function(e) {
			$('#study-nestable-${inputname} .dd-item .dd3-content').removeClass('selected');
			$(e.currentTarget).addClass('selected');
			$('#${inputname}').val($(e.currentTarget).closest('.dd-item').data('id'));
		});		
	});
</r:script>
<input id="${inputname}" type="hidden" name="${inputname}" />
<div id="study-nestable-${inputname}" class="dd">
	<ol class="dd-list">
		<g:each in="${studyTypeRoots}" var="study">
			<g:render template="/common/study_type_nest" model="['study':study]" />
		</g:each>
	</ol>
</div>