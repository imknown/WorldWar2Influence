package com.dengdeng123.blowupplane.entity;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/** 用户信息 */
// 建议加上注解， 混淆后表名不受影响
@Table(name = "Flyfight")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = -7519140057228810867L;

	// 如果主键没有命名名为id或_id的时，需要为主键添加此注解
	@Id
	// int, long类型的id默认自增，不想使用自增时添加此注解
	@NoAutoIncrement
	@Column(column = "id")
	private int id;

	// 建议加上注解， 混淆后列名不受影响
	@Column(column = "UserId")
	private String userId = "";

	@Column(column = "nickName")
	private String nickname = "";

	@Column(column = "Bomb")
	private int bombNum;

	@Column(column = "money")
	private int coinNum;

	@Column(column = "headShot")
	private int headshotNum;

	@Column(column = "delFlag")
	private int delFlag;

	@Column(column = "armor")
	private int armor;

	/** 拢共击落飞机数量 */
	@Column(column = "headCount")
	private int shotdownTotal;

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

	public int getBombNum() {
		return bombNum;
	}

	public void setBombNum(int bombNum) {
		this.bombNum = bombNum;
	}

	public int getCoinNum() {
		return coinNum;
	}

	public void setCoinNum(int coinNum) {
		this.coinNum = coinNum;
	}

	public int getHeadshotNum() {
		return headshotNum;
	}

	public void setHeadshotNum(int headshotNum) {
		this.headshotNum = headshotNum;
	}

	public int getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}

	public int getShotdownTotal() {
		return shotdownTotal;
	}

	public void setShotdownTotal(int shotdownTotal) {
		this.shotdownTotal = shotdownTotal;
	}
}
