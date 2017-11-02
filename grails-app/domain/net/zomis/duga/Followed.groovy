package net.zomis.duga;

class Followed {
	
	String name
	long lastChecked
	long lastEventId
	String roomIds
	Integer followType = 0
	String interestingEvents
	
	boolean isUser() {
		return followType == 1
	}

    @Override
    public String toString() {
        return "Followed{" +
                "name='" + name + '\'' +
                ", lastChecked=" + lastChecked +
                ", lastEventId=" + lastEventId +
                ", roomIds='" + roomIds + '\'' +
                ", followType=" + followType +
                ", interestingEvents='" + interestingEvents + '\'' +
                '}';
    }
}
