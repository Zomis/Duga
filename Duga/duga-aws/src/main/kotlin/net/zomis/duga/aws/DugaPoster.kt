package net.zomis.duga.aws

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class A : RequestHandler<String, String> {

    override fun handleRequest(input: String?, context: Context?): String {
        return ""
    }

}