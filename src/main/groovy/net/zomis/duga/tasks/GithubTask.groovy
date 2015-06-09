package net.zomis.duga.tasks

import net.zomis.duga.DugaBot
import net.zomis.duga.HookStringification
import net.zomis.duga.chat.WebhookParameters
import net.zomis.duga.GithubBean
import net.zomis.duga.github.GithubEventFilter;

import java.time.Instant;
import java.util.stream.Stream;

import net.zomis.duga.Followed;

public class GithubTask implements Runnable {
	
	private final GithubBean githubBean;
    private final HookStringification stringify;
    private final DugaBot bot;

	public GithubTask(GithubBean githubBean, HookStringification stringify, DugaBot bot) {
		this.githubBean = githubBean
        this.stringify = stringify
        this.bot = bot
	}

	@Override
	public void run() {
    	final int API_LIMIT = 4; // 12 times per hour, 4 items each time = 48 requests. API limit is 60. Leaves some space for other uses.
    	try {
            Followed.withNewSession {data ->
                List<Followed> followed = Followed.list()
                followed.sort(Comparator.comparingLong({follow -> follow.getLastChecked()}));
                followed.stream()
                        .limit(API_LIMIT)
                        .forEach({ scanFollowed(it) });
            }
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

	private void scanFollowed(final Followed follow) {
    	long update = Instant.now().getEpochSecond();
    	List<Object> events;
		try {
			events = githubBean.fetchEvents(follow);
		} catch (IOException e) {
    		return;
    	}

    	WebhookParameters params = new WebhookParameters();
    	params.setPost(true);
    	params.setRoomId(follow.getRoomIds());

    	Stream<Object> stream = events.stream();
    	stream = GithubEventFilter.filter(stream, follow.getInterestingEvents());
    	
    	stream.forEach({ev -> post(ev, follow.getLastEventId(), params)});
    	
    	long eventId = events.stream().mapToLong({ev -> ev.id}).max().orElse(follow.getLastEventId());
    	System.out.println("Update : " + eventId);
        follow.lastChecked = update
        follow.lastEventId = eventId
        follow.save(flush: true)
	}
    
	private void post(event, long lastEventId, WebhookParameters params) {
	    if (event.id > lastEventId) {
			System.out.println("POST: " + event);
            List<String> list = new ArrayList<>()
            String type = (event.type as String)
                    .replaceAll('Event', '')
                    .replaceAll('([A-Z])', '_$1')
                    .substring(1)
                    .toLowerCase()
            stringify."$type"(list, event.payload)
    		bot.postChat(params, list);
		} else {
    		System.out.println(event);
		}
    }

}
