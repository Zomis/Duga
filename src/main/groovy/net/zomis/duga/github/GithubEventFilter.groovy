package net.zomis.duga.github;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubEventFilter {

	public static Stream<Object> filter(Stream<Object> stream, String interestingEvents) {
    	Set<Predicate<Object>> wantedEvents = Arrays.stream(interestingEvents.split(","))
    			.map({str -> predicateMatch(str)})
    			.collect(Collectors.toSet());

    	return stream.filter({ev -> wantedEvents.stream().anyMatch({pred -> pred.test(ev)})});
	}
	
    private static Predicate<Object> predicateMatch(String str) {
    	switch (str) {
    		case "*":
    			return {ev -> true}
			case "create-tag":
				return {ev -> ev.type == 'CreateEvent' && ev.payload.ref_type == 'tag'}
			case "create-repository":
				return {ev -> ev.type == 'CreateEvent' && ev.payload.ref_type == 'repository'}
    		default:
                return {ev -> ev.type == str}
    	}
	}

}
