package broadcast;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.superxlcrnote.app.R;

import java.util.List;

import activity.WelcomeActivity;
import model.SuperxlcrNoteDB;
import service.WidgetService;

public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        SuperxlcrNoteDB db = SuperxlcrNoteDB.getInstance(context);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        // 更新日期
        if (intent.getStringExtra("date") != null) {
            // widget_time
            views.setTextViewText(R.id.widget_time, intent.getStringExtra("date"));
        }

        // 更新workingTask文字
        if (intent.getIntExtra("workingTask", -1) != -1) {
            int workingTask = intent.getIntExtra("workingTask", 0);
            int workingTaskTodayWork = intent.getIntExtra("workingTaskTodayWork", 0);
            views.setTextViewText(R.id.widget_working_task,
                    "普通任務：" + Integer.toString(workingTaskTodayWork) + " / " +
                            Integer.toString(workingTask));
        } else {
            int workingTask = db.getWorkingTaskNumber();
            int workingTaskTodayWork = db.getWorkingTaskTodayWorkNumber();
            views.setTextViewText(R.id.widget_working_task,
                    "普通任務：" + Integer.toString(workingTaskTodayWork) + " / " +
                            Integer.toString(workingTask));
        }

        // 更新dailyTask文字
        if (intent.getIntExtra("finishDailyTask", -1) != -1 &&
                intent.getIntExtra("totalDailyTask", -1) != -1) {
            int finishDailyTask = intent.getIntExtra("finishDailyTask", 0);
            int totalDailyTask = intent.getIntExtra("totalDailyTask", 0);
            views.setTextViewText(R.id.widget_daily_task,
                    "每日任務：" + Integer.toString(finishDailyTask) + " / " +
                            Integer.toString(totalDailyTask));
        } else {
            int finishDailyTask = db.getFinishDailyTaskNumber();
            int totalDailyTask = db.getTotalDailyTaskNumber();
            views.setTextViewText(R.id.widget_daily_task,
                    "每日任務：" + Integer.toString(finishDailyTask) + " / " +
                            Integer.toString(totalDailyTask));
        }

        // widget_btn
        Intent intentBtn = new Intent(context, WelcomeActivity.class);
        PendingIntent piBtn = PendingIntent.getActivity(context, 0, intentBtn, 0);
        views.setOnClickPendingIntent(R.id.widget_text_layout, piBtn);

        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(thisWidget);
        manager.updateAppWidget(appWidgetIds, views);

    }

    // RemoteViews 设置界面
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent service = new Intent(context, WidgetService.class);
        if (isServiceRunning(context, "WidgetService") == false) {
            context.startService(service);
        }

        SuperxlcrNoteDB db = SuperxlcrNoteDB.getInstance(context);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        // 更新文本
        int workingTask = db.getWorkingTaskNumber();
        int workingTaskTodayWork = db.getWorkingTaskTodayWorkNumber();
        views.setTextViewText(R.id.widget_working_task,
                "普通任務：" + Integer.toString(workingTaskTodayWork) + " / " +
                        Integer.toString(workingTask));
        int finishDailyTask = db.getFinishDailyTaskNumber();
        int totalDailyTask = db.getTotalDailyTaskNumber();
        views.setTextViewText(R.id.widget_daily_task,
                "每日任務：" + Integer.toString(finishDailyTask) + " / " +
                        Integer.toString(totalDailyTask));

        appWidgetManager.updateAppWidget(appWidgetIds[0], views);

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 用来判断服务是否运行.
     */
    private boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) ==
                    true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}
