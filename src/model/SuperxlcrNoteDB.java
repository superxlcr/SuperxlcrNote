package model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.superxlcrnote.app.R;

import java.util.ArrayList;
import java.util.List;

import database.SuperxlcrNoteOpenHelper;

public class SuperxlcrNoteDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "superxlcr_note";
    /**
     * 数据库版本
     */
    public static final int VERSION = 4;
    /* TODO 玩家属性自定义*/
    public static String[] attr_name = {"身體素質", "快樂", "網頁編程", "遊戲編程", "應用編程", "眼界",
            "軟件設計", "計劃性", "課業", "硬件编程"};
    /* exp:#fcfe66 gold:#dde000*/
    public static String[] attr_color = {"#fb2c32", "#ffea00", "#00ff66", "#00f6ff",
            "#5836eb", "#ff7e00", "#6cff00", "#dff6f6", "#5765ff", "#c7e16d"};
    public static String[] attr_icon = {Integer.toString(R.drawable.health),
            Integer.toString(R.drawable.happy),
            Integer.toString(R.drawable.internet), Integer.toString(R.drawable.game),
            Integer.toString(R.drawable.software), Integer.toString(R.drawable.eyes),
            Integer.toString(R.drawable.design), Integer.toString(R.drawable.plan),
            Integer.toString(R.drawable.school),
            Integer.toString(R.drawable.hardware)};
    public static String[] attr_short_name = {"身體", "快樂", "網頁", "遊戲", "應用", "眼界",
            "軟件", "計劃", "課業", "硬件"};
    public static int[] attr_number = new int[attr_name.length];
    private static SuperxlcrNoteDB superxlcrNoteDB;
    private static SQLiteDatabase db;

    /**
     * 构造方法私有化
     */
    private SuperxlcrNoteDB(Context context) {
        SuperxlcrNoteOpenHelper dbHelper = new SuperxlcrNoteOpenHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取数据库实例
     */
    public synchronized static SuperxlcrNoteDB getInstance(Context context) {
        if (superxlcrNoteDB == null) {
            superxlcrNoteDB = new SuperxlcrNoteDB(context);
        }
        refreshAttr();
        return superxlcrNoteDB;
    }

    /**
     * 刷新玩家属性表的数据
     */
    private static void refreshAttr() { /* 保存数字*/
        for (int i = 0; i < attr_name.length; i++) {
            Cursor cursor = db
                    .rawQuery("select * from Person_attr where name = ? limit 1",
                            new String[]{attr_name[i]});
            if (cursor.moveToFirst()) {
                attr_number[i] = cursor.getInt(cursor.getColumnIndex("number"));
            } else {
                attr_number[i] = 0;
            }
            cursor.close();
        } /* 刷新属性数据*/
        db.execSQL("delete from Person_attr");
        for (int i = 0; i < attr_name.length; i++) {
            db.execSQL("insert into Person_attr (name , number , color , icon , " +
                            "shortName) " +
                            "values (?" +
                            " , ? " +
                            ", ? , ? , ?)",
                    new String[]{attr_name[i], Integer.toString(attr_number[i]),
                            attr_color[i], attr_icon[i], attr_short_name[i]});
        }
    }

    /**
     * 返回所有的玩家属性
     */
    public List<PersonAttr> getPersonAttr() {
        List<PersonAttr> list = new ArrayList<PersonAttr>();
        Cursor cursor = db.rawQuery("select * from Person_attr", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int number = cursor.getInt(cursor.getColumnIndex("number"));
                String color = cursor.getString(cursor.getColumnIndex("color"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                int icon_int = 0;
                boolean haveIcon = true;
                if (icon.isEmpty()) {
                    haveIcon = false;
                } else {
                    icon_int = Integer.parseInt(icon);
                }
                String shortName = cursor
                        .getString(cursor.getColumnIndex("shortName"));
                list.add(new PersonAttr(name, number, color, icon_int, haveIcon,
                        shortName));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 通過名字返回玩家属性
     */
    public PersonAttr getPersonAttrByName(String namePara) {
        PersonAttr personAttr = null;
        Cursor cursor = db
                .rawQuery("select * from Person_attr where name = ? LIMIT 1",
                        new String[]{namePara});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int number = cursor.getInt(cursor.getColumnIndex("number"));
            String color = cursor.getString(cursor.getColumnIndex("color"));
            String icon = cursor.getString(cursor.getColumnIndex("icon"));
            int icon_int = 0;
            boolean haveIcon = true;
            if (icon.isEmpty()) {
                haveIcon = false;
            } else {
                icon_int = Integer.parseInt(icon);
            }
            String shortName = cursor.getString(cursor.getColumnIndex("shortName"));
            personAttr = new PersonAttr(name, number, color, icon_int, haveIcon,
                    shortName);
        }
        cursor.close();
        return personAttr;
    }

    /**
     * 刷新玩家属性
     */
    public void updatePersonAttrByName(String name, int addNumber) {
        Cursor cursor = db
                .rawQuery("select * from Person_attr where name = ? LIMIT 1",
                        new String[]{name});
        if (cursor.moveToFirst()) {
            int number = cursor.getInt(cursor.getColumnIndex("number")) + addNumber;
            db.execSQL("update Person_attr set number = ? where name = ?",
                    new String[]{Integer.toString(number), name});
        }
        cursor.close();
    }

    /**
     * 将Working_task存入数据库
     */
    public void saveWorking_task(String description, int now_progress,
            int total_progress, String unit, String reward, String start_time,
            String note) {
        db.execSQL("insert into Working_task (description , now_progress , " +
                        "total_progress , " +
                        "unit , " +
                        "reward" +
                        " , start_time ," +
                        " note , today_work)values (? , ? , ? , ? , ? , ? , ? , -1)",
                new String[]{description, Integer.toString(now_progress),
                        Integer.toString(total_progress), unit, reward, start_time,
                        note});
    }

    /**
     * 将Finish_task存入数据库
     */
    public void saveFinish_task(String description, int total_progress, String unit,
            String reward, String start_time, String finish_time) {
        db.execSQL(
                "insert into Finish_task (description , total_progress , unit , " +
                        "reward , " +
                        "start_time , " +
                        "finish_time)" +
                        "values (? , ? , ? , ? , ? , ?)",
                new String[]{description, Integer.toString(total_progress), unit,
                        reward, start_time, finish_time});
    }

    /**
     * 将DailyTask存入数据库
     */
    public void saveDailytask(String description, String reward) {
        db.execSQL(
                "insert into Daily_task (description , continuous , finish_times ," +
                        " finish , " +
                        "reward)" +
                        "values (? , ? , ? " +
                        ", ? , ?)",
                new String[]{description, "0", "0", "0", reward});
    }

    /**
     * 返回数据库的working_task
     */
    public List<WorkingTask> getWorking_tasks() {
        List<WorkingTask> list = new ArrayList<WorkingTask>();
        Cursor cursor = db.rawQuery("select * from Working_task", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor
                        .getString(cursor.getColumnIndex("description"));
                int now_progress = cursor
                        .getInt(cursor.getColumnIndex("now_progress"));
                int total_progress = cursor
                        .getInt(cursor.getColumnIndex("total_progress"));
                String unit = cursor.getString(cursor.getColumnIndex("unit"));
                String reward = cursor.getString(cursor.getColumnIndex("reward"));
                String start_time = cursor
                        .getString(cursor.getColumnIndex("start_time"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                int todayWork = cursor.getInt(cursor.getColumnIndex("today_work"));
                WorkingTask task = new WorkingTask(id, description, now_progress,
                        total_progress, unit, reward, start_time, note, todayWork);
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 返回数据库的finish_task
     */
    public List<FinishTask> getFinish_tasks() {
        List<FinishTask> list = new ArrayList<FinishTask>();
        Cursor cursor = db
                .rawQuery("select * from Finish_task order by id desc", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor
                        .getString(cursor.getColumnIndex("description"));
                int total_progress = cursor
                        .getInt(cursor.getColumnIndex("total_progress"));
                String unit = cursor.getString(cursor.getColumnIndex("unit"));
                String reward = cursor.getString(cursor.getColumnIndex("reward"));
                String start_time = cursor
                        .getString(cursor.getColumnIndex("start_time"));
                String finish_time = cursor
                        .getString(cursor.getColumnIndex("finish_time"));
                FinishTask task = new FinishTask(id, description, total_progress,
                        unit, reward, start_time, finish_time);
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 返回数据库的DailyTask
     */
    public List<DailyTask> getDailyTasks() {
        List<DailyTask> list = new ArrayList<DailyTask>();
        Cursor cursor = db.rawQuery("select * from Daily_task", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor
                        .getString(cursor.getColumnIndex("description"));
                int continuous = cursor.getInt(cursor.getColumnIndex("continuous"));
                int finishTimes = cursor
                        .getInt(cursor.getColumnIndex("finish_times"));
                int finish = cursor.getInt(cursor.getColumnIndex("finish"));
                String reward = cursor.getString(cursor.getColumnIndex("reward"));
                DailyTask task = new DailyTask(id, description, continuous,
                        finishTimes, finish, reward);
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteWorking_task(WorkingTask task) {
        int id = task.getId();
        db.execSQL("delete from Working_task where id = ?",
                new String[]{Integer.toString(id)});
    }

    public void deleteFinish_task(FinishTask task) {
        int id = task.getId();
        db.execSQL("delete from Finish_task where id = ?",
                new String[]{Integer.toString(id)});
    }

    public void deleteDailyTask(DailyTask task) {
        int id = task.getId();
        db.execSQL("delete from Daily_task where id = ?",
                new String[]{Integer.toString(id)});
    }

    public WorkingTask getWorking_taskById(int id) {
        Cursor cursor = db.rawQuery("select * from Working_task where id = ?",
                new String[]{Integer.toString(id)});
        if (cursor.moveToFirst()) {
            String description = cursor
                    .getString(cursor.getColumnIndex("description"));
            int now_progress = cursor.getInt(cursor.getColumnIndex("now_progress"));
            int total_progress = cursor
                    .getInt(cursor.getColumnIndex("total_progress"));
            String unit = cursor.getString(cursor.getColumnIndex("unit"));
            String reward = cursor.getString(cursor.getColumnIndex("reward"));
            String start_time = cursor
                    .getString(cursor.getColumnIndex("start_time"));
            String note = cursor.getString(cursor.getColumnIndex("note"));
            int todayWork = cursor.getInt(cursor.getColumnIndex("today_work"));
            WorkingTask task = new WorkingTask(id, description, now_progress,
                    total_progress, unit, reward, start_time, note, todayWork);
            cursor.close();
            return task;
        }
        cursor.close();
        return new WorkingTask(id, "該任務不存在", 0, 100, "%", "無", "1442918668", "", -1);
    }

    public FinishTask getFinishTaskById(int id) {
        Cursor cursor = db.rawQuery("select * from Finish_task where id = ?",
                new String[]{Integer.toString(id)});
        if (cursor.moveToFirst()) {
            String description = cursor
                    .getString(cursor.getColumnIndex("description"));
            int total_progress = cursor
                    .getInt(cursor.getColumnIndex("total_progress"));
            String unit = cursor.getString(cursor.getColumnIndex("unit"));
            String reward = cursor.getString(cursor.getColumnIndex("reward"));
            String start_time = cursor
                    .getString(cursor.getColumnIndex("start_time"));
            String finish_time = cursor
                    .getString(cursor.getColumnIndex("finish_time"));
            FinishTask task = new FinishTask(id, description, total_progress, unit,
                    reward, start_time, finish_time);
            cursor.close();
            return task;
        }
        cursor.close();
        return new FinishTask(id, "該任務不存在", 100, "%", "無", "1442918668",
                "1442918668");
    }

    public DailyTask getDailyTaskById(int id) {
        Cursor cursor = db.rawQuery("select * from Daily_task where id = ?",
                new String[]{Integer.toString(id)});
        if (cursor.moveToFirst()) {
            String description = cursor
                    .getString(cursor.getColumnIndex("description"));
            int continuous = cursor.getInt(cursor.getColumnIndex("continuous"));
            int finishTimes = cursor.getInt(cursor.getColumnIndex("finish_times"));
            int finish = cursor.getInt(cursor.getColumnIndex("finish"));
            String reward = cursor.getString(cursor.getColumnIndex("reward"));
            DailyTask task = new DailyTask(id, description, continuous, finishTimes,
                    finish, reward);
            cursor.close();
            return task;
        }
        cursor.close();
        return new DailyTask(id, "該任務不存在", 0, 0, 0, "無");
    }

    public void changeWorking_taskNowProgressById(int id, int now_progress,
            int todayWork) {
        db.execSQL(
                "update Working_task set now_progress = ?, today_work = ? where id" +
                        " = ?", new String[]{Integer.toString(now_progress),
                        Integer.toString(todayWork), Integer.toString(id)});
    }

    public void changeWorking_taskNoteById(int id, String note) {
        db.execSQL("update Working_task set note = ? where id = ?",
                new String[]{note, Integer.toString(id)});
    }

    public void dailyTaskConfirm(int id) {
        int continuous = 0, finishTimes = 0, finish = 0;
        Cursor cursor = db.rawQuery("select * from Daily_task where id = ?",
                new String[]{Integer.toString(id)});
        if (cursor.moveToFirst()) {
            continuous = cursor.getInt(cursor.getColumnIndex("continuous"));
            finishTimes = cursor.getInt(cursor.getColumnIndex("finish_times"));
            finish = cursor.getInt(cursor.getColumnIndex("finish"));
        }
        cursor.close();
        if (finish == 0) {
            finish++;
            continuous++;
            finishTimes++;
            db.execSQL("update Daily_task set finish = ?, continuous = ?, " +
                    "finish_times = ? " +
                    "where id " +
                    "= ?", new String[]{Integer.toString(finish),
                    Integer.toString(continuous), Integer.toString(finishTimes),
                    Integer.toString(id)});
        }

    }

    public void refreshDailyTask() {
        // 今日未完成无法连续
        Cursor cursor = db
                .rawQuery("select * from Daily_task where finish = 0", null);
        int id = 0;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"));
                db.execSQL(
                        "update Daily_task set finish = 0, continuous = 0 where id" +
                                " = ?", new String[]{Integer.toString(id)});
            } while (cursor.moveToNext());
        }
        cursor.close();
        // 今日完成可连续
        cursor = db.rawQuery("select * from Daily_task where finish = 1", null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"));
                db.execSQL("update Daily_task set finish = 0 where id = ?",
                        new String[]{Integer.toString(id)});
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public int getFinishDailyTaskNumber() {
        Cursor cursor = db.rawQuery(
                "select count(*) as number from Daily_task where finish = 1", null);
        int number = 0;
        if (cursor.moveToFirst()) {
            number = cursor.getInt(cursor.getColumnIndex("number"));
        }
        cursor.close();
        return number;
    }

    public int getTotalDailyTaskNumber() {
        Cursor cursor = db
                .rawQuery("select count(*) as number from Daily_task", null);
        int number = 0;
        if (cursor.moveToFirst()) {
            number = cursor.getInt(cursor.getColumnIndex("number"));
        }
        cursor.close();
        return number;
    }

    public int getWorkingTaskNumber() {
        Cursor cursor = db.rawQuery("select count(*) as number from Working_task", null);
        int number = 0;
        if (cursor.moveToFirst()) {
            number = cursor.getInt(cursor.getColumnIndex("number"));
        }
        cursor.close();
        return number;
    }

    public int getWorkingTaskTodayWorkNumber() {
        Cursor cursor = db
                .rawQuery("select count(*) as number from Working_task where today_work != -1", null);
        int number = 0;
        if (cursor.moveToFirst()) {
            number = cursor.getInt(cursor.getColumnIndex("number"));
        }
        cursor.close();
        return number;
    }

    public void resetWorkingTaskTodayWork() {
        db.execSQL("update Working_task set today_work = -1"); // 全部更新为未完成
    }

}
