package net.zomis.duga

import org.grails.web.json.JSONObject

class HookStringification {

    static String repository(JSONObject json) {
        return "\\[[$json.repository.full_name]($json.repository.html_url)\\]"
    }

    void ping(List<String> result, JSONObject json) {
        result << "${repository(json)} Ping: $json.zen"
    }

    List<String> postGithub(String type, JSONObject json) {
        List<String> result = new ArrayList<>()
        result << 'Github event: ' + type
        this."$type"(result, json)
        return result
    }

}
