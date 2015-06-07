package net.zomis.duga.tasks

import net.zomis.duga.DugaBot
import net.zomis.duga.chat.WebhookParameters;

import java.text.MessageFormat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import net.zomis.duga.DailyInfo

import java.util.stream.Collectors;

public class StatisticTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(StatisticTask.class);
	private final DugaBot chatBot;
    private final List<WebhookParameters> rooms

    public StatisticTask(DugaBot chatBot, String rooms) {
		this.chatBot = chatBot;
        this.rooms = Arrays.stream(rooms.split(','))
                .map({String str -> WebhookParameters.toRoom(str)})
                .collect(Collectors.toList())
	}
	
    @Override
	public void run() {
		logger.info("time!");

        List<DailyInfo> results = DailyInfo.list()

//		List<DailyInfo> results = new ArrayList<>(dailyService.getAndReset());
    	results.sort(Comparator.comparing({DailyInfo ee -> ee.getName().toLowerCase()}));
    	
		for (WebhookParameters params : rooms) {

   			chatBot.postSingle(params, "***RELOAD!***");
    		
   			for (DailyInfo stat : results) {
   				StringBuilder str = new StringBuilder(MessageFormat.format("\\[[**{0}**]({1})\\]",
					stat.getName(), stat.getUrl()));
   				int startLength = str.length();
   				
   				addStat(str, stat.getCommits(), "commit");
   				addStat(str, stat.getIssuesOpened(), "opened issue");
   				addStat(str, stat.getIssuesClosed(), "closed issue");
   				
   				addStat(str, stat.getComments(), "issue comment");
   				addStat(str, stat.getAdditions(), "addition");
   				addStat(str, stat.getDeletions(), "deletion");
   				if (str.length() > startLength) {
   	       			chatBot.postSingle(params, str.toString());
   				}
   			}
    	}
        for (DailyInfo result : results) {
            result.issuesOpened = 0
            result.issuesClosed = 0
            result.commits = 0
            result.deletions = 0
            result.additions = 0
            result.comments = 0
            result.save()
        }
	}

	private static void addStat(StringBuilder str, int count, String text) {
		if (count > 0) {
			str.append(' ');
			str.append(count);
			str.append(' ');
			str.append(text);
			if (count > 1) {
				str.append('s');
			}
			str.append('.');
		}
	}

}
