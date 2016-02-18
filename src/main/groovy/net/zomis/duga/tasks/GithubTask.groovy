package net.zomis.duga.tasks

import net.zomis.duga.DugaBotService
import net.zomis.duga.HookStringification
import net.zomis.duga.chat.BotRoom
import net.zomis.duga.GithubBean
import net.zomis.duga.github.GithubEventFilter;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.zomis.duga.Followed;

public class GithubTask implements Runnable {
	
	private final GithubBean githubBean;
    private final HookStringification stringify;
    private final DugaBotService bot;

	public GithubTask(GithubBean githubBean, HookStringification stringify, DugaBotService bot) {
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
                println 'followed: ' + followed
                followed.sort(Comparator.comparingLong({follow -> follow.getLastChecked()}));
                followed.stream()
                        .limit(API_LIMIT)
                        .forEach({ scanFollowed(it) });
            }
    	}
    	catch (Exception ex) {
            println 'Error: ' + ex
    		ex.printStackTrace(System.out);
    	}
    }

	private void scanFollowed(final Followed follow) {
    	long update = Instant.now().getEpochSecond();
    	List<Object> events;
		try {
            println 'Before scan: ' + follow
			events = githubBean.fetchEvents(follow);
            println 'events: ' + events.size()
		} catch (IOException e) {
            println 'exc: ' + e
    		return;
    	}

    	BotRoom params = bot.room(follow.roomIds);

    	Stream<Object> stream = events.stream();
    	stream = GithubEventFilter.filter(stream, follow.getInterestingEvents());
        def postEvents = stream.collect(Collectors.toList())
        println 'filtered events: ' + postEvents.size()

    	postEvents.forEach({ev -> post(ev, follow.getLastEventId(), params)});
    	
    	long eventId = events.stream().mapToLong({ev -> Long.parseLong(ev.id)}).max().orElse(follow.getLastEventId());
    	System.out.println("Update : " + eventId);
        follow.lastChecked = update
        follow.lastEventId = eventId
        follow.save(flush: true)
	}
    
	private void post(event, long lastEventId, BotRoom params) {
	    if (Long.parseLong(event.id) > lastEventId) {
			System.out.println("POST: " + event);
            List<String> list = new ArrayList<>()
            String type = (event.type as String)
                    .replaceAll('Event', '')
                    .replaceAll('([A-Z])', '_$1')
                    .substring(1)
                    .toLowerCase()
            event.payload.repository = event.repo
            event.payload.repository.full_name = event.repo.name
            event.payload.repository.html_url = event.repo.url.replaceFirst('api.github.com/repos', 'www.github.com')
            event.payload.sender = event.actor
            event.payload.sender.html_url = event.actor.url.replaceFirst('api.github.com/users', 'www.github.com')
            try {
                stringify."$type"(list, event.payload)
                bot.postChat(params.messages(list));
            } catch (Exception ex) {
                bot.postAsync(params.message("Exception $ex when processing event $event"));
            }
		} else {
    		System.out.println(event);
		}
    }

}
