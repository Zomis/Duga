package net.zomis.duga

class DugaFileConfig {

    String deployGreeting = "Monking!"
    String dailyRooms = "16134,14929,24299"
    String undeployGoodbyeText = "TTQW!!"
    String deployGreetingRooms = "20298"

    List<TaskData> tasks = Arrays.asList(
        new TaskData("10 0 0 * * *", "dailyStats;16134,14929"),
        new TaskData("0 0 * * * *", "github"),
        new TaskData("0 * * * * *", "comments"),
        new TaskData("4 0 0 * * *", "unanswered;8595;codereview;***RELOAD!*** There are %unanswered% unanswered questions (%percentage%% answered)"),
        new TaskData("0 45 23 * * *", "ratingdiff;20298;31562,23788;codereview"),
        new TaskData("0 0 */2 * * *", "mess;20298;The time is %time% and @Duga is alive"),
        new TaskData("0 */5 * * * *", "questionScan;codereview;answerInvalidation;8595")
    )
    
    List<Followed> followed = []

    // No users at the moment.
    List<User> users = []

    List<TaskData> getTasks() { tasks }

}
