package com.dengdeng123.blowupplane.entity;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/** 存放闯关模式的每一小节的信息 */
// 建议加上注解， 混淆后表名不受影响
@Table(name = "Flydate" /* , execAfterTableCreated = "CREATE UNIQUE INDEX index_name ON parent(name,email)" */)
public class StoryModeStageEntity implements Serializable {

	private static final long serialVersionUID = 7418809942397764088L;

	/** 没有下一关 */
	public static final int NO_NEXT_STAGE = -1;

	// 如果主键没有命名名为id或_id的时，需要为主键添加此注解
	@Id
	// int, long类型的id默认自增，不想使用自增时添加此注解
	@NoAutoIncrement
	@Column(column = "T_id")
	private int id;

	// 建议加上注解， 混淆后列名不受影响
	@Column(column = "T_na")
	private String mapName;

	/** [0]未解锁, [1]已解锁 */
	@Column(column = "T_en")
	private int lockedState;

	@Column(column = "F_id")
	private int fatherId;

	/** 第几小节 */
	private int order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getLockedState() {
		return lockedState;
	}

	public void setLockedState(int lockedState) {
		this.lockedState = lockedState;
	}

	public int getFatherId() {
		return fatherId;
	}

	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
