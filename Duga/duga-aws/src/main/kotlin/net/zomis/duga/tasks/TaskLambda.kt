package net.zomis.duga.tasks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class TaskLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {
    val githubAPI = System.getenv("GITHUB_API")

    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {



        TODO("Not implemented")
    }
}