package com.dengdeng123.blowupplane.module.user;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dengdeng123.blowupplane.base.BeginApplication;
import com.dengdeng123.blowupplane.base.Constant;
import com.dengdeng123.blowupplane.entity.PlaneOffsetEntity;
import com.dengdeng123.blowupplane.entity.UserEntity;

public class UserDao {

	/**
	 * 根据用户Id 查找用户
	 * 
	 * @param id
	 *            用户Id
	 * @return 用户信息
	 */
	public UserEntity findUserById(String id) {
		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		Cursor cursor = database.rawQuery("select * from Flyfight where UserId = ?", new String[] { id });

		UserEntity userEntity = null;

		if (cursor.moveToNext()) // ResulSet.next()
		{
			userEntity = new UserEntity();
			userEntity.setUserId(id);
			userEntity.setNickname(cursor.getString(cursor.getColumnIndex("nickName")));
			userEntity.setBombNum(cursor.getInt(cursor.getColumnIndex("Bomb")));
			userEntity.setCoinNum(cursor.getInt(cursor.getColumnIndex("money")));
			userEntity.setShotdownTotal(cursor.getInt(cursor.getColumnIndex("headCount")));
			userEntity.setHeadshotNum(cursor.getInt(cursor.getColumnIndex("headShot")));
		}

		cursor.close();
		// database.close();

		return userEntity;
	}

	/**
	 * 根据地图父ID, 查找飞机坐标数据
	 * 
	 * @param id
	 *            用户Id
	 * @return 飞机坐标集合
	 */
	public List<PlaneOffsetEntity> findAllPlaneOffsetByParentId(int id) {
		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		Cursor cursor = database.rawQuery("select * from FlySonDate where FatherID = ?", new String[] { String.valueOf(id) });

		List<PlaneOffsetEntity> planeOffsetEntities = new ArrayList<PlaneOffsetEntity>();

		PlaneOffsetEntity planeOffsetEntity = null;

		while (cursor.moveToNext()) // ResulSet.next()
		{
			planeOffsetEntity = new PlaneOffsetEntity();
			planeOffsetEntity.setDirection(cursor.getString(cursor.getColumnIndex("Direction")));
			planeOffsetEntity.setHandpieceOffset(cursor.getString(cursor.getColumnIndex("Head")));

			planeOffsetEntities.add(planeOffsetEntity);
		}

		cursor.close();
		// database.close();

		return planeOffsetEntities;
	}

	/**
	 * 更新用户的金币数和购买到道具数量
	 * 
	 * @param userId
	 *            用户ID
	 * @param coinLeft
	 *            购买后的金币数量
	 * @param typeName
	 *            购买的类型
	 * @param numBuy
	 *            购买的数量
	 * @return 是否更新成功
	 */
	public boolean updateUserInfo(String userId, int coinLeft, String typeName, int numBuy) {
		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		String table = "Flyfight";

		ContentValues values = new ContentValues();// 要修改的列的数据
		values.put("money", coinLeft);

		if ("炸弹".equals(typeName)) {
			values.put("Bomb", numBuy + BeginApplication.ue.getBombNum());
		} else if ("爆头".equals(typeName)) {
			values.put("headShot", numBuy + BeginApplication.ue.getHeadshotNum());
		}

		String whereClause = "UserId = ?";// where 条件
		String[] whereArgs = { userId };// ? 占位符的值

		long rowsAffected = database.update(table, values, whereClause, whereArgs);

		// database.close();

		return (rowsAffected > 0) ? true : false;
	}

	/**
	 * 更新游客的总共击杀了多少飞机
	 * 
	 * @param shotdownTotal
	 *            总共击杀了多少飞机
	 * @return 是否更新成功
	 */
	public boolean updateShotdownTotal(int shotdownTotal) {

		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		String table = "Flyfight";

		ContentValues values = new ContentValues();// 要修改的列的数据
		values.put("headCount", shotdownTotal);

		String whereClause = "UserId =  ?";// where 条件
		String[] whereArgs = { Constant.GUEST_ID };// ? 占位符的值

		long rowsAffected = database.update(table, values, whereClause, whereArgs);

		// database.close();

		return (rowsAffected > 0) ? true : false;
	}

	/**
	 * 更新游客的剩余的炸弹数量
	 * 
	 * @param bombNum
	 *            剩余的炸弹数量
	 * @return 是否更新成功
	 */
	public boolean updateBombNum(int bombNum) {

		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		String table = "Flyfight";

		ContentValues values = new ContentValues();// 要修改的列的数据
		values.put("Bomb", bombNum);

		String whereClause = "UserId =  ?";// where 条件
		String[] whereArgs = { Constant.GUEST_ID };// ? 占位符的值

		long rowsAffected = database.update(table, values, whereClause, whereArgs);

		// database.close();

		return (rowsAffected > 0) ? true : false;
	}

	/**
	 * 更新游客的剩余的爆头数量
	 * 
	 * @param bombNum
	 *            剩余的爆头数量
	 * @return 是否更新成功
	 */
	public boolean updateHeadshotNum(int headshotNum) {

		SQLiteDatabase database = BeginApplication.dbUtils.getDatabase();

		String table = "Flyfight";

		ContentValues values = new ContentValues();// 要修改的列的数据
		values.put("headShot", headshotNum);

		String whereClause = "UserId =  ?";// where 条件
		String[] whereArgs = { Constant.GUEST_ID };// ? 占位符的值

		long rowsAffected = database.update(table, values, whereClause, whereArgs);

		// database.close();

		return (rowsAffected > 0) ? true : false;
	}
}
