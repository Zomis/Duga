package com.skiwi.githubhooksechatservice.mvc.beans;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.events.github.CreateEvent;

public class GithubEventFilter {

	public Stream<AbstractEvent> filter(Stream<AbstractEvent> stream, String interestingEvents) {
    	Set<Predicate<AbstractEvent>> wantedEvents = Arrays.stream(interestingEvents.split(","))
    			.map(str -> predicateMatch(str))
    			.collect(Collectors.toSet());

    	return stream.filter(ev -> wantedEvents.stream().anyMatch(pred -> pred.test(ev)));
	}
	
    private Predicate<AbstractEvent> predicateMatch(String str) {
    	switch (str) {
    		case "*":
    			return ev -> true;
			case "create-tag":
				return ev -> asEvent(ev, CreateEvent.class, cr -> cr.getRefType().equals("tag"));
			case "create-repository":
				return ev -> asEvent(ev, CreateEvent.class, cr -> cr.getRefType().equals("repository"));
    		default: return ev -> ev.getClass().getSimpleName().equals(str);
    	}
	}

	private <E extends AbstractEvent> boolean asEvent(AbstractEvent ev, Class<E> clazz, Predicate<E> ifPresent) {
		if (clazz.isAssignableFrom(ev.getClass())) {
			return ifPresent.test(clazz.cast(ev));
		}
		return false;
	}

}
