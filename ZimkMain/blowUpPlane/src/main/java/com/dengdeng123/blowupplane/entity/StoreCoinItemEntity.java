package com.dengdeng123.blowupplane.entity;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

//建议加上注解， 混淆后表名不受影响
@Table(name = "StoreTable")
public class StoreCoinItemEntity implements Serializable {

	private static final long serialVersionUID = 4471143014872499222L;

	// 如果主键没有命名名为id或_id的时，需要为主键添加此注解
	@Id
	// int, long类型的id默认自增，不想使用自增时添加此注解
	@NoAutoIncrement
	// 建议加上注解， 混淆后列名不受影响
	@Column(column = "PD_id")
	private String id;

	@Column(column = "PD_na")
	private String name;

	@Column(column = "PD_de")
	private String desc;

	@Column(column = "PD_key")
	private int itemKey;

	@Column(column = "PD_img")
	private int itemImg;

	@Column(column = "PD_num")
	private String howManyCoins;

	@Column(column = "PD_me")
	private String howMuchRmb;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getItemKey() {
		return itemKey;
	}

	public void setItemKey(int itemKey) {
		this.itemKey = itemKey;
	}

	public int getItemImg() {
		return itemImg;
	}

	public void setItemImg(int itemImg) {
		this.itemImg = itemImg;
	}

	public String getHowManyCoins() {
		return howManyCoins;
	}

	public void setHowManyCoins(String howManyCoins) {
		this.howManyCoins = howManyCoins;
	}

	public String getHowMuchRmb() {
		return howMuchRmb;
	}

	public void setHowMuchRmb(String howMuchRmb) {
		this.howMuchRmb = howMuchRmb;
	}
}
