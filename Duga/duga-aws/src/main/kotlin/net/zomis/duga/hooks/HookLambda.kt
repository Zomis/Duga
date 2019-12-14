package net.zomis.duga.hooks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class HookLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {
    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {
        val type = input!!["type"] as String? ?: return mapOf("error" to "No type specified")


        TODO("")
    }
}