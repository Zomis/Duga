package net.zomis.duga

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import net.zomis.duga.server.ArgumentsCheck

class DugaLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {

    fun execute(params: List<String>) {
        val args = ArgumentsCheck(params.toSet())

    }

    override fun handleRequest(
        input: Map<String, Any>?,
        context: Context
    ): Map<String, Any> {
        println(context.functionName)
        println(input)
        println(context)
//        context.logger.log("")
        return mapOf("key" to "result")
    }

}