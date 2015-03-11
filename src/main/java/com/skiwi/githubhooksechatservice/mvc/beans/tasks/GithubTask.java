package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.model.Followed;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubEventFilter;
import com.skiwi.githubhooksechatservice.mvc.controllers.GithubHookController;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.service.GithubService;

public class GithubTask implements Runnable {
	
	private final GithubService githubService;
	private final GithubBean githubBean;
	private final GithubEventFilter eventFilter;
	private final GithubHookController controller;
	
	public GithubTask(GithubService githubService, GithubBean githubBean,
			GithubEventFilter filter, GithubHookController controller) {
		this.githubService = githubService;
		this.githubBean = githubBean;
		this.eventFilter = filter;
		this.controller = controller;
	}

	@Override
	public void run() {
    	final int API_LIMIT = 4; // 12 times per hour, 4 items each time = 48 requests. API limit is 60. Leaves some space for other uses.
    	try {
        	List<Followed> followed = new ArrayList<Followed>(githubService.getAll());
        	followed.sort(Comparator.comparingLong(follow -> follow.getLastChecked()));
        	followed.stream()
        		.limit(API_LIMIT)
        		.forEach(this::scanFollowed);
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	/* 
    	 * API call to https://api.github.com/rate_limit , read rate limit
    	 * API call to https://api.github.com/repos/Tejpbit/CodeIT/events?page=1
    	 * API call to https://api.github.com/users/Zomis/received_events/public -- ping the chat user
    	 * API call to https://api.github.com/repos/Tejpbit/CodeIT/commits?page=1&since=YYYY-MM-DDTHH:MM:SSZ
    	 * 
    	 * */
    }


    
	private void scanFollowed(final Followed follow) {
    	long update = Instant.now().getEpochSecond();
    	List<AbstractEvent> events;
		try {
			events = githubBean.fetchEvents(follow);
		} catch (IOException e) {
    		return;
    	}

    	WebhookParameters params = new WebhookParameters();
    	params.setPost(true);
    	params.setRoomId(follow.getRoomIds());

    	Stream<AbstractEvent> stream = events.stream();
    	stream = eventFilter.filter(stream, follow.getInterestingEvents());
    	
    	stream.forEach(ev -> post(ev, follow.getLastEventId(), params));
    	
    	long eventId = events.stream().mapToLong(ev -> ev.getId()).max().orElse(follow.getLastEventId());
    	System.out.println("Update : " + eventId);
    	githubService.update(follow.getName(), update, eventId, follow.getFollowType() == 1);
	}
    
	private void post(AbstractEvent event, long lastEventId, WebhookParameters params) {
	    if (event.getId() > lastEventId) {
			System.out.println("POST: " + event);
    		controller.post(params, event);
		}
		else {
    		System.out.println(event);
		}
    }

}
