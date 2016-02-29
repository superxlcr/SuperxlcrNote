package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.superxlcrnote.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import model.FinishTask;
import model.SuperxlcrNoteDB;
import util.adapter.FinishTaskExpandableListAdapter;

/**
 * Created by Superxlcr
 * 管理已完成任务界面的类
 */
public class FinishTaskActivity {
    // 活动实体
    private Activity activity = null;

    private List<FinishTask> finishTaskData;
    private List<String> groupList = new ArrayList<String>();
    private List<List<FinishTask>> childList = new ArrayList<List<FinishTask>>();
    private ExpandableListView listView;
    private FinishTaskExpandableListAdapter adapter;

    private SuperxlcrNoteDB db;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public FinishTaskActivity(Activity activity) {
        this.activity = activity;
    }

    private String nowDate;
    private int expendIndex = -1;

    public void onCreate() {

        // 初始化数据库
        db = SuperxlcrNoteDB.getInstance(activity);

        // 取得sharedPreferences
        sp = activity.getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // 初始化列表
        listView = (ExpandableListView) activity.findViewById(R.id.finish_task_list);
        refreshData();
        adapter = new FinishTaskExpandableListAdapter(activity,
                R.layout.finish_task_group_row, groupList,
                R.layout.finish_task_child_row, childList);
        listView.setAdapter(adapter);
        listView.setDivider(activity.getResources().getDrawable(R.color.color_bg2));
        listView.setChildDivider(
                activity.getResources().getDrawable(R.color.color_bg));
        listView.setDividerHeight(3);

        if (groupList.contains(nowDate)) {
            expendIndex = groupList.indexOf(nowDate);
            listView.expandGroup(expendIndex);
        }

        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                            int position, long id) {

                        // 判断点击是否子项
                        if (ExpandableListView.getPackedPositionType(id) ==
                                ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                            // 获取子项信息
                            long packedPos = ((ExpandableListView) parent)
                                    .getExpandableListPosition(position);
                            final int groupPosition = ExpandableListView
                                    .getPackedPositionGroup(packedPos);
                            final int childPosition = ExpandableListView
                                    .getPackedPositionChild(packedPos);

                            // 弹出对话框删除子项
                            new AlertDialog.Builder(activity).setTitle("刪除任務記錄")
                                    .setMessage("請問確定刪除該任務記錄:\"" +
                                            childList.get(groupPosition)
                                                    .get(childPosition)
                                                    .getDescription() +
                                            "\"嗎？").setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {

                                            // 删除子项并刷新界面
                                            FinishTask task = childList
                                                    .get(groupPosition)
                                                    .get(childPosition);
                                            db.deleteFinish_task(task);
                                            onResume();
                                            Toast.makeText(activity, "任務記錄刪除成功！",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("取消", null).show();

                            return true;
                        }

                        return false;
                    }
                });

        //		// 滑动移除监听器
        //		listView.setRemoveListener(new RemoveListener() {
        //
        //			// 滑动删除之后的回调方法
        //			@Override
        //			public void removeItem(RemoveDirection direction, int
        // position) {
        //
        //				final int finalPosition = position;
        //				new AlertDialog.Builder(activity)
        //						.setTitle("刪除任務記錄")
        //						.setMessage(
        //								"請問確定刪除該任務記錄:\""
        //										+ finishTaskData.get(finalPosition)
        //												.getDescription() + "\"嗎？")
        //						.setPositiveButton("確定",
        //								new DialogInterface.OnClickListener() {
        //
        //									@Override
        //									public void onClick(DialogInterface
        // dialog,
        //											int which) {
        //										FinishTask task = finishTaskData
        //												.get(finalPosition);
        //										db.deleteFinish_task(task);
        //										finishTaskAdapter.remove
        // (finishTaskAdapter
        //												.getItem(finalPosition));
        //										Toast.makeText(activity,
        // "任務記錄刪除成功！",
        //												Toast.LENGTH_SHORT).show();
        //									}
        //								}).setNegativeButton("取消", null).show();
        //
        //			}
        //		});
        // 点击监听器
        listView.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                            int groupPosition, int childPosition, long id) {
                        Intent intent = new Intent(activity,
                                ShowFinishTaskDetailActivity.class);
                        intent.putExtra("taskId",
                                childList.get(groupPosition).get(childPosition)
                                        .getId());
                        activity.startActivity(intent);
                        return true;
                    }
                });
    }

    /**
     * 更新列表数据
     */
    public void refreshData() {
        groupList.clear();
        childList.clear();
        finishTaskData = db.getFinish_tasks();
        ListIterator<FinishTask> lit = finishTaskData.listIterator();
        while (lit.hasNext()) {
            FinishTask task = lit.next();
            if (!groupList.contains(task.getFinishTime())) {
                groupList.add(task.getFinishTime());
            }
            int index = groupList.indexOf(task.getFinishTime());
            while (index >= childList.size()) {
                childList.add(new ArrayList<FinishTask>());
            }
            childList.get(index).add(task);
        }

        // 转换时间戳为格式时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        nowDate = sdf.format(new Date(System.currentTimeMillis()));
    }

    public void onResume() {
        // 刷新数据
        refreshData();
        adapter.notifyDataSetChanged();
        if (expendIndex != -1) {
            listView.collapseGroup(expendIndex);
            expendIndex = -1;
        }
        if (groupList.contains(nowDate)) {
            expendIndex = groupList.indexOf(nowDate);
            listView.expandGroup(expendIndex);
        }
    }
}
