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
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-user" class="content scaffold-create" role="main">
            Thank you for registering with @Duga!<br>
            One more step remains, but don't worry, it is an easy step.<br>
            Go to the Stack Exchange chat room <a href="http://chat.stackexchange.com/rooms/20298/dugas-playground">Duga's Playground</a>
             and ping @Duga with a one-time token that has been generated for you:<br>
            Write the following in chat: <pre>@Duga register '${key}'</pre>
            <br>
            @Duga should respond to you with the result.<br>
        </div>
    </body>
</html>
