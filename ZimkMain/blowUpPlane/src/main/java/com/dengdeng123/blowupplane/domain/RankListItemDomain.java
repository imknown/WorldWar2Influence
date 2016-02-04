package com.dengdeng123.blowupplane.domain;

import java.io.Serializable;

public class RankListItemDomain implements Serializable {

	private static final long serialVersionUID = 1381005093154751436L;

	private String rank;
	private String userId;
	private String nickname;
	private String shotdownTotal;

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getShotdownTotal() {
		return shotdownTotal;
	}

	public void setShotdownTotal(String shotdownTotal) {
		this.shotdownTotal = shotdownTotal;
	}

}
