package com.dengdeng123.blowupplane.entity;

import java.io.Serializable;
import java.util.List;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/** 存放地图信息 */
// 建议加上注解， 混淆后表名不受影响
@Table(name = "Flydate" /* , execAfterTableCreated = "CREATE UNIQUE INDEX index_name ON parent(name,email)" */)
public class PlaneMapEntity implements Serializable {

	private static final long serialVersionUID = 1657264475914113252L;

	// 如果主键没有命名名为id或_id的时，需要为主键添加此注解
	@Id
	// int, long类型的id默认自增，不想使用自增时添加此注解
	@NoAutoIncrement
	@Column(column = "id")
	private int id;

	// 建议加上注解， 混淆后列名不受影响
	@Column(column = "name")
	private String mapName;

	@Column(column = "num")
	private int planeNum;

	@Column(column = "createmen")
	private String createPersonName;

	@Column(column = "SeparateX")
	private int rangeX;

	@Column(column = "SeparateY")
	private int rangeY;

	// @Finder(valueColumn = "id", targetColumn = "parentId")
	// public FinderLazyLoader<Child> children; // 关联对象多时建议使用这种方式，延迟加载效率较高。

	// @Finder(valueColumn = "id",targetColumn = "parentId")
	// public Child children;

	@Finder(valueColumn = "id", targetColumn = "FatherID")
	private List<PlaneOffsetEntity> planeOffsetEntities;

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getPlaneNum() {
		return planeNum;
	}

	public void setPlaneNum(int planeNum) {
		this.planeNum = planeNum;
	}

	public String getCreatePersonName() {
		return createPersonName;
	}

	public void setCreatePersonName(String createPersonName) {
		this.createPersonName = createPersonName;
	}

	public int getRangeX() {
		return rangeX;
	}

	public void setRangeX(int rangeX) {
		this.rangeX = rangeX;
	}

	public int getRangeY() {
		return rangeY;
	}

	public void setRangeY(int rangeY) {
		this.rangeY = rangeY;
	}

	public List<PlaneOffsetEntity> getPlaneOffsetEntities() {
		return planeOffsetEntities;
	}

	public void setPlaneOffsetEntities(List<PlaneOffsetEntity> planeOffsetEntities) {
		this.planeOffsetEntities = planeOffsetEntities;
	}
}
