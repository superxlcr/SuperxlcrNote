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
 * �������������������
 */
public class FinishTaskActivity {
    // �ʵ��
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

        // ��ʼ�����ݿ�
        db = SuperxlcrNoteDB.getInstance(activity);

        // ȡ��sharedPreferences
        sp = activity.getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // ��ʼ���б�
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

                        // �жϵ���Ƿ�����
                        if (ExpandableListView.getPackedPositionType(id) ==
                                ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                            // ��ȡ������Ϣ
                            long packedPos = ((ExpandableListView) parent)
                                    .getExpandableListPosition(position);
                            final int groupPosition = ExpandableListView
                                    .getPackedPositionGroup(packedPos);
                            final int childPosition = ExpandableListView
                                    .getPackedPositionChild(packedPos);

                            // �����Ի���ɾ������
                            new AlertDialog.Builder(activity).setTitle("�h���΄�ӛ�")
                                    .setMessage("Ո���_���h��ԓ�΄�ӛ�:\"" +
                                            childList.get(groupPosition)
                                                    .get(childPosition)
                                                    .getDescription() +
                                            "\"�᣿").setPositiveButton("�_��",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {

                                            // ɾ�����ˢ�½���
                                            FinishTask task = childList
                                                    .get(groupPosition)
                                                    .get(childPosition);
                                            db.deleteFinish_task(task);
                                            onResume();
                                            Toast.makeText(activity, "�΄�ӛ䛄h���ɹ���",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("ȡ��", null).show();

                            return true;
                        }

                        return false;
                    }
                });

        //		// �����Ƴ�������
        //		listView.setRemoveListener(new RemoveListener() {
        //
        //			// ����ɾ��֮��Ļص�����
        //			@Override
        //			public void removeItem(RemoveDirection direction, int
        // position) {
        //
        //				final int finalPosition = position;
        //				new AlertDialog.Builder(activity)
        //						.setTitle("�h���΄�ӛ�")
        //						.setMessage(
        //								"Ո���_���h��ԓ�΄�ӛ�:\""
        //										+ finishTaskData.get(finalPosition)
        //												.getDescription() + "\"�᣿")
        //						.setPositiveButton("�_��",
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
        // "�΄�ӛ䛄h���ɹ���",
        //												Toast.LENGTH_SHORT).show();
        //									}
        //								}).setNegativeButton("ȡ��", null).show();
        //
        //			}
        //		});
        // ���������
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
     * �����б�����
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

        // ת��ʱ���Ϊ��ʽʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        nowDate = sdf.format(new Date(System.currentTimeMillis()));
    }

    public void onResume() {
        // ˢ������
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
