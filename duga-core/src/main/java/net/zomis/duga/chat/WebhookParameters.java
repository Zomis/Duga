package net.zomis.duga.chat;

public class WebhookParameters {
	
	private String roomId;
	private Boolean post;
	
	public String getRoomId() {
		return roomId;
	}
	
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public void useDefaultRoom(String defaultRoomId) {
		if (roomId == null) {
			roomId = defaultRoomId;
		}
	}
	
	public boolean getPost() {
		return post == null ? true : post;
	}
	
	public void setPost(Boolean post) {
		this.post = post;
	}
	
	public static WebhookParameters toRoom(String roomId) {
		WebhookParameters params = new WebhookParameters();
		params.setPost(true);
		params.setRoomId(roomId);
		return params;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
		if (o == null) return false;
        if (getClass() != o.getClass()) return false;

        WebhookParameters that = (WebhookParameters) o;

        if (post != that.post) return false;
        if (!roomId.equals(that.roomId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roomId.hashCode();
        result = 31 * result + (post != null ? post.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Room " + roomId;
    }
	
}
