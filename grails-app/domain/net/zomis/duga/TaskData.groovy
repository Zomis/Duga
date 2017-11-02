package net.zomis.duga;

class TaskData {
	
	String cronStr
	String taskValue

    TaskData() {}

    TaskData(String cronStr, String taskValue) {
        this.cronStr = cronStr
        this.taskValue = taskValue
    }

}
