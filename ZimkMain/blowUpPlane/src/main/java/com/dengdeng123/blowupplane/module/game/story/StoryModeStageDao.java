package com.dengdeng123.blowupplane.module.game.story;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.entity.StoryModeStageEntity;

public class StoryModeStageDao {

	/**
	 * 根据 点击的地图控件Id, 找到 节点(Stage)的 集合
	 * 
	 * @param mapId
	 *            点击的地图控件Id
	 * @return 节点(Stage)的 集合
	 */
	public List<StoryModeStageEntity> findStoryModeStageByMapId(int mapId) {
		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		String name = "";

		if (mapId == R.id.map_ussr_germany_imgv) {
			name = "map苏德%";
		} else if (mapId == R.id.map_pacific_imgv) {
			name = "map太平洋%";
		} else if (mapId == R.id.map_china_imgv) {
			name = "map中国%";
		} else if (mapId == R.id.map_western_europe_imgv) {
			name = "map西欧%";
		}

		Cursor cursor = database.query("ThroughTable", null, "T_na like ?", new String[] { name }, null, null, "T_id");
		// Cursor cursor = database.rawQuery("select * from ThroughTable where T_na like ? order by T_id;", new String[] { name });

		List<StoryModeStageEntity> storyModeStageEntities = new ArrayList<StoryModeStageEntity>();

		StoryModeStageEntity storyModeStageEntity = null;

		int i = 1;

		while (cursor.moveToNext()) // ResulSet.next()
		{
			storyModeStageEntity = new StoryModeStageEntity();
			storyModeStageEntity.setId(cursor.getInt(cursor.getColumnIndex("T_id")));
			storyModeStageEntity.setMapName(cursor.getString(cursor.getColumnIndex("T_na")));
			storyModeStageEntity.setLockedState(cursor.getInt(cursor.getColumnIndex("T_en")));
			storyModeStageEntity.setFatherId(cursor.getInt(cursor.getColumnIndex("F_id")));
			storyModeStageEntity.setOrder(i++);

			storyModeStageEntities.add(storyModeStageEntity);
		}

		cursor.close();
		// database.close();

		return storyModeStageEntities;
	}

	/**
	 * 根据id解锁关卡
	 * 
	 * @param id
	 *            数据库的id
	 * @return 是否更新成功
	 */
	public boolean updateStoryModeStageById(String id) {
		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		String table = "ThroughTable";

		ContentValues values = new ContentValues();// 要修改的列的数据
		values.put("T_en", 1);

		String whereClause = "T_id =  ?";// where 条件
		String[] whereArgs = { id };// ? 占位符的值

		long rowsAffected = database.update(table, values, whereClause, whereArgs);

		// database.close();

		return (rowsAffected > 0) ? true : false;
	}

	// public boolean modifyLocalBTrainVersion(String[] sqls, String maxBTrainVersion) {
	// boolean success = true;// 是否操作成功
	// SQLiteDatabase db = BeginApplication.dbUtils.getDatabase();
	//
	// db.beginTransaction();
	//
	// try {
	// db.execSQL("update otherz set value = ? where key = 'local_b_train_version'", new Object[] { maxBTrainVersion });
	// db.setTransactionSuccessful();// 设置事务标志为成功，当结束事务时就会提交事务
	// } catch (Exception e) {
	// success = false;
	// e.printStackTrace();
	// } finally {
	// db.endTransaction();// 结束事务
	// }
	//
	// db.close();
	//
	// return success;
	// }
}
