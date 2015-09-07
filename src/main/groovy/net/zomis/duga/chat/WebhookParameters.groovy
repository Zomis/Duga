package net.zomis.duga.chat;

class WebhookParameters {
	
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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        WebhookParameters that = (WebhookParameters) o

        if (post != that.post) return false
        if (roomId != that.roomId) return false

        return true
    }

    int hashCode() {
        int result
        result = roomId.hashCode()
        result = 31 * result + (post != null ? post.hashCode() : 0)
        return result
    }

    @Override
    String toString() {
        return 'Room ' + roomId
    }
	
}
