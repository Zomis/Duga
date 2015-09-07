package net.zomis.duga.tasks.qscan

import net.zomis.duga.StackAPI

class StackMockAPI implements StackAPI {

    Map results = [:]

    StackMockAPI expect(String apiCall, def result) {
        results.put(apiCall, result)
        this
    }

    @Override
    def apiCall(String apiCall, String site, String filter) throws IOException {
        def result = results.get(apiCall)
        assert result : "No mock result defined for $apiCall. Keys are ${results.keySet()}"
        result
    }

}
