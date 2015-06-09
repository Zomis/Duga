<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>

            </ul>
        </div>
        <div id="create-user" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${user}">
            <ul class="errors" role="alert">
                <g:eachError bean="${user}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form action="save">
                <h1>Generate a Github access token</h1>
                <a href="https://github.com/settings/tokens/new">Go to Github and generate a new personal access token</a><br>
                Make sure 'admin:repo_hook' is selected. You may uncheck everything else.<br>
                Enter the generated Github access token below.<br>
                Benefits of registering to @Duga are:
                <ul>
                    <li>Future-proof for <strong>when</strong> webhook link changes
                    <li>A lot easier to add a webhook
                    <li>Allows you to ping @Duga with commands
                    <li>Allows more github API requests
                    <li>Easy overview of repositories attached to @Duga
                </ul>
                Api Key: <input type="text" name="apikey"></input>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.register.label', default: 'Register')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
