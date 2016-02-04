package com.dengdeng123.blowupplane.entity;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/** 存放飞机具体坐标 */
@Table(name = "FlySonDate")
// 建议加上注解， 混淆后表名不受影响
public class PlaneOffsetEntity implements Serializable {

	private static final long serialVersionUID = 2000782754428127572L;

	// 如果主键没有命名名为id或_id的时，需要为主键添加此注解
	@Id
	// int, long类型的id默认自增，不想使用自增时添加此注解
	@NoAutoIncrement
	@Column(column = "id")
	private int id;

	@Foreign(column = "FatherID", foreign = "id")
	private PlaneMapEntity planeMapEntity;

	@Column(column = "Head")
	private String handpieceOffset;

	@Column(column = "Direction")
	private String direction;

	public PlaneMapEntity getPlaneMapEntity() {
		return planeMapEntity;
	}

	public void setPlaneMapEntity(PlaneMapEntity planeMapEntity) {
		this.planeMapEntity = planeMapEntity;
	}

	public String getHandpieceOffset() {
		return handpieceOffset;
	}

	public void setHandpieceOffset(String handpieceOffset) {
		this.handpieceOffset = handpieceOffset;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	// @Foreign(column = "parentId", foreign = "id")
	// public ForeignLazyLoader<Parent> parent;
	// @Foreign(column = "parentId", foreign = "isVIP")
	// public List<Parent> parent;
	// @Foreign(column = "parentId", foreign = "id")
	// public Parent parent;

	// // Transient使这个列被忽略，不存入数据库
	// @Transient
	// public String willIgnore;

	// public static String staticFieldWillIgnore; // 静态字段也不会存入数据库
}
