package net.zomis.duga

import groovy.json.JsonSlurper
import net.zomis.duga.Followed;

public class GithubBean {
	
	public List fetchEvents(Followed follow) throws IOException {
		return fetchEvents(follow.getFollowType() == 1, follow.getName(), follow.getLastEventId());
	}

	private static Object[] fetchEventsByPage(boolean user, String name, int page) throws IOException {
   		String type = user ? "users" : "repos";
   		URL url = new URL("https://api.github.com/" + type + "/" + name + "/events?page=" + page);
        return new JsonSlurper().parse(url)
	}
	
	public static List<Object> fetchEvents(boolean user, String name, long lastEvent) throws IOException {
   		int page = 1;
        Object[] data = fetchEventsByPage(user, name, page);
   		if (data == null) {
   			return null;
   		}
		List<Object> list = new ArrayList<Object>(Arrays.asList(data));

		if (lastEvent >= 0) {
			boolean foundEvent = list.stream().anyMatch({ev -> ev.id >= lastEvent});
			while (!foundEvent) {
				data = fetchEventsByPage(user, name, page);
				if (data == null) {
					break;
				}
				list.addAll(Arrays.asList(data));
			}
		}
		list.sort(Comparator.comparingLong({event -> event.id}));
		return list;
	}

}
