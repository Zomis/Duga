beforeMethodCall { call ->
    if (isMethodCallExpression(call)) {
        ['clone', 'finalize', '']
        def methodName = call.methodAsString
        if (methodName == 'wait' ||
                methodName == 'clone' ||
                methodName == 'finalize' ||
                methodName == 'notify' ||
                methodName == 'notifyAll') {
            addStaticTypeError('Not allowed',call)
            handled = true
        }
    }
}

methodNotFound { receiver, name, argList, argTypes, call ->
    if (receiver.name == 'java.util.Map') {
        handled = true
        makeDynamic(call, classNodeFor(Map))
    }
}
