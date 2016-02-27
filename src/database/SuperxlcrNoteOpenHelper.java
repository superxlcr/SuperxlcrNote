package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SuperxlcrNoteOpenHelper extends SQLiteOpenHelper {

	/**
	 * 进行中任务表格
	 */

	public static final String CREATE_WORKING_TASK = "create table Working_task ("
			+ "id integer primary key autoincrement , "
			+ "description text , "
			+ "now_progress integer , "
			+ "total_progress integer , "
			+ "unit text , "
			+ "reward text , "
			+ "start_time text , "
			+ "note text , "
			+ "today_work integer)"; // 今日完成列，-1为未动工，其他为完成进度。

	/**
	 * 已完成任务表格
	 */

	public static final String CREATE_FINISH_TASK = "create table Finish_task ("
			+ "id integer primary key autoincrement , "
			+ "description text , "
			+ "total_progress integer , "
			+ "unit text , "
			+ "reward text , "
			+ "start_time text , " + "finish_time text)";

	/**
	 * 玩家属性表格
	 */
	public static final String CREATE_PERSON_ATTR = "create table Person_attr ("
			+ "id integer primary key autoincrement , "
			+ "name text , "
			+ "number int , "
			+ "color text , "
			+ "icon text , "
			+ "shortName text)";

	public static final String CREATE_DAILY_TASK = "create table Daily_task ("
			+ "id integer primary key autoincrement , " + "description text , "
			+ "continuous int , " + "finish_times int , " + "finish int , "
			+ "reward text)";

	public SuperxlcrNoteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_WORKING_TASK); // 创建进行中任务表格
		db.execSQL(CREATE_FINISH_TASK); // 创建已完成任务表格
		db.execSQL(CREATE_PERSON_ATTR); // 创建玩家属性表格
		db.execSQL(CREATE_DAILY_TASK); // 创建每日任务表格
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL("alter table Working_task add note text"); // 加入备注列
		case 2:
			db.execSQL(CREATE_DAILY_TASK); // 创建日常任务表格
		case 3:
			db.execSQL("alter table Working_task add today_work integer"); // 加入今日完成列
			db.execSQL("update Working_task set today_work = -1"); // 全部更新为未完成
		}
	}

}
