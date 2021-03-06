<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
	<!--Boton Superior-->
	<div class="row">
		<div class="col-sm-12">
			<g:link class="btn btn-small" action="index"><i class="icon-reorder"></i><g:message code="default.list.label" args="[entityName]" /></g:link>
		</div>
	</div>

	<!-- Titulo -->
	<div class="row">		
		<div class="box col-sm-12">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
		</div>
	</div>

	<!-- FlashMessage -->
	<g:if test="\${flash.message}">
		<div class="row">
			<div class="col-sm-12">
				<div class="alert alert-info">
						<button type="button" class="close" data-dismiss="alert">×</button>
						\${flash.message}
				</div>
			</div>
		</div>
	</g:if>

	<!--Errores -->
	<g:hasErrors bean="\${${propertyName}}">
		<div class="row">		
			<div class="col-sm-12">
				<div class="alert alert-danger">
				<ul class="errors" role="alert">
					<g:eachError bean="\${${propertyName}}" var="error">
					<li <g:if test="\${error in org.springframework.validation.FieldError}">data-field-id="\${error.field}"</g:if>><g:message error="\${error}"/></li>
					</g:eachError>
				</ul>
				</div>
			</div>
		</div>
	</g:hasErrors>

	<!-- Formulario -->
	<div class="row">		
		<div class="box col-sm-12">
			<div class="box-header" data-original-title>
				<h2><i class="icon-user"></i><span class="break"></span><g:message code="default.boxheader.label" args="[entityName]" /></h2>
				<div class="box-icon">
					<a href="#" class="btn-minimize"><i class="icon-chevron-up"></i></a>
				</div>
			</div>
			<div class="box-content">
				<g:form url="[resource:${propertyName}, action:'save']" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
					<fieldset class="col-sm-12">
						<g:render template="form"/>
						<div class="form-actions">								
							<g:submitButton name="create" class="btn btn-large btn-success" value="\${message(code: 'default.button.create.label', default: 'Create')}" />
								
						</div>
					</fieldset>
				</g:form>
			</div>
		</div>
	</div>



	</body>
</html>
