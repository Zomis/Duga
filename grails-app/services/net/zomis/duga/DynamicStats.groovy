package net.zomis.duga

import org.grails.web.json.JSONObject
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class DynamicStats {

    private static final Logger logger = LogManager.getLogger(DynamicStats.class)

    class DynamicStat {
        private Map<String, Integer> map = new LinkedHashMap<>()
        final String application

        DynamicStat(String application) {
            this.application = application
        }

        Map<String, Integer> current() {
            return new LinkedHashMap<String, Integer>(map)
        }

        void add(String name, int value) {
            map.merge(name, value, { a, b -> a + b })
        }

        String statsAsString() {
            StringBuilder result = new StringBuilder()
            def mapCopy = new LinkedHashMap<String, Integer>(map)
            for (def ee : mapCopy.entrySet()) {
                if (result.length() > 0) {
                    result.append(", ")
                }
                result.append(ee.key)
                result.append(": ")
                result.append(ee.value)
            }
            return result.toString()
        }
    }

    @Autowired
    Environment environment

    private Map<String, DynamicStat> stats = [:]

    List<DynamicStat> daily() {
        def result = new ArrayList<>(stats.values())
        stats.clear()
        return result
    }

    boolean isAuthenticated(String token, String application) {
        String knownApplications = environment.getProperty("dynStatApplications")
        String[] known = knownApplications.split(",")
        String found = known.find {it == application}
        if (found == null) {
            logger.error("Application '$application' not found. Known applications are $known")
            return false
        }
        String expectedToken = environment.getProperty("dynStat" + application)
        return expectedToken != null && !expectedToken.isEmpty() && expectedToken == token
    }

    Map<String, Integer> save(def json) {
        logger.info("Save Dynamic Stats: $json")
        String token = json.authToken
        String application = json.application
        if (!isAuthenticated(token, application)) {
            logger.warn("Application $application was not authenticated with token $token")
            return [authorized: 0]
        }

        stats.putIfAbsent(application, new DynamicStat(application))

        DynamicStat stat = stats.get(application)

        JSONObject stats = json.stats
        def entires = stats.entrySet()
        for (def ee : entires) {
            def statName = ee.key as String
            def statValue = ee.value as Integer

            stat.add(statName, statValue)
        }

        def current = stat.current()
        logger.info("Dynamic Stats for $application is now $current")

        return current
    }

}
