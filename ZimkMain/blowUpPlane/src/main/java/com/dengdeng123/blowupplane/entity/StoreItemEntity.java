package com.dengdeng123.blowupplane.entity;

import java.io.Serializable;

import android.view.View;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

// 建议加上注解， 混淆后表名不受影响
@Table(name = "StoreTable")
public class StoreItemEntity implements Serializable {

	private static final long serialVersionUID = 3197174283607521127L;

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

	@Column(column = "PD_me")
	private String coinNum;

	@Column(column = "PD_num")
	private int itemNum;

	@Column(column = "PD_key")
	private int itemKey;

	@Column(column = "PD_img")
	private int itemImg;

	private int visibility = View.GONE;

	private int numSelected = 1;

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

	public String getCoinNum() {
		return coinNum;
	}

	public void setCoinNum(String coinNum) {
		this.coinNum = coinNum;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
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

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public int getNumSelected() {
		return numSelected;
	}

	public void setNumSelected(int numSelected) {
		this.numSelected = numSelected;
	}
}
