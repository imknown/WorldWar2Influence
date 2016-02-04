package com.dengdeng123.blowupplane.domain;

import java.io.Serializable;

public class FriendListItemDomain implements Serializable {

	private static final long serialVersionUID = -2089756859338036204L;

	private String sequenceNumber;
	private String friendId;
	private String friendName;

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
}
