package service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import model.SuperxlcrNoteDB;

import broadcast.WidgetProvider;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class WidgetService extends Service {

	private Timer timer = null;
	private SimpleDateFormat sdf = null;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private SuperxlcrNoteDB db;

	/**
	 * 相关变量初始化
	 */
	private void init() {
		timer = new Timer();
		sdf = new SimpleDateFormat("yyyy年MM月dd日 EE");
		sp = getSharedPreferences("DailyTask", 0);
		editor = sp.edit();
		db = SuperxlcrNoteDB.getInstance(getApplicationContext());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化
		this.init();
		// 定时器发送广播
		// 延迟一分钟，隔一分钟发一次
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// 发送广播
				checkTime();
			}
		}, 1000, 60000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void checkTime() {
		String oldTime = sp.getString("date", "");
		String newTime = sdf.format(new Date());

		Intent intent = new Intent(this, WidgetProvider.class);

		// 新的一天~
		if (!oldTime.equals(newTime)) {
			// 修改sp
			editor.putString("date", newTime);
			editor.commit();

			// 刷新每日任务
			db.refreshDailyTask();
			// 刷新普通任务完成情况
			db.resetWorkingTaskTodayWork();

			// 普通任务
			intent.putExtra("workingTask", db.getWorkingTaskNumber());
			intent.putExtra("workingTaskTodayWork",
					db.getWorkingTaskTodayWorkNumber());

			// 每日任务
			intent.putExtra("finishDailyTask", 0);
			intent.putExtra("totalDailyTask", db.getTotalDailyTaskNumber());

		}

		// 发送广播，通知UI层时间改变了
		intent.putExtra("date", newTime);
		sendBroadcast(intent);

	}

}
