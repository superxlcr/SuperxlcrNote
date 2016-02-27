package util.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superxlcrnote.app.R;

import java.util.List;

import model.FinishTask;
import model.PersonAttr;
import model.SuperxlcrNoteDB;

/**
 * Created by Superxlcr
 * On 2015/11/30
 */
public class FinishTaskExpandableListAdapter extends BaseExpandableListAdapter {

    private List<String> groupList;
    private List<List<FinishTask>> childList;
    private Context context;

    private int groupViewResourceId;
    private int childViewResourceId;

    private int TotalShortNameNumber = 3;

    private SuperxlcrNoteDB db;

    private boolean showExp = false;
    private boolean showGold = false;

    public FinishTaskExpandableListAdapter(Context context, int groupViewResourceId,
            List<String> groupList, int childViewResourceId,
            List<List<FinishTask>> childList) {
        this.context = context;

        this.groupViewResourceId = groupViewResourceId;
        this.groupList = groupList;

        this.childViewResourceId = childViewResourceId;
        this.childList = childList;

        db = SuperxlcrNoteDB.getInstance(context);

    }

    // Group
    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(groupViewResourceId, parent, false);

            holder = new GroupViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.task_number = (TextView) convertView
                    .findViewById(R.id.task_number);
            holder.indicator = (ImageView) convertView.findViewById(R.id.indicator);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        holder.date.setText(groupList.get(groupPosition));
        holder.task_number
                .setText(Integer.toString(childList.get(groupPosition).size()));
        if (isExpanded) {
            holder.indicator
                    .setImageResource(R.drawable.finish_task_group_view_hide);
        } else {
            holder.indicator
                    .setImageResource(R.drawable.finish_task_group_view_show);
        }
        return convertView;
    }

    //Child
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(childViewResourceId, parent, false);

            holder = new ChildViewHolder();
            holder.task_id = (TextView) convertView.findViewById(R.id.task_id);
            holder.task_description = (TextView) convertView
                    .findViewById(R.id.task_description);
            holder.task_start_time = (TextView) convertView
                    .findViewById(R.id.task_start_time);
            holder.task_finish_time = (TextView) convertView
                    .findViewById(R.id.task_finish_time);
            holder.task_total_progress = (TextView) convertView
                    .findViewById(R.id.task_total_progress);
            holder.task_reward_layout = (LinearLayout) convertView
                    .findViewById(R.id.task_reward_layout);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        FinishTask item = childList.get(groupPosition).get(childPosition);
        holder.task_id.setText(Integer.toString(item.getId()));
        holder.task_description.setText(item.getDescription());
        holder.task_start_time.setText(item.getStartTime());
        holder.task_finish_time.setText(item.getFinishTime());
        String unit = item.getUnit();
        holder.task_total_progress
                .setText(Integer.toString(item.getTotalProgress()) + unit);
        holder.task_reward_layout.removeAllViews();
        String[] reward = null;
        reward = item.getReward().split("\\|");
        int shortNameNumber = 0;
        for (int i = 0; i < reward.length; i++) {
            String[] singleReward = reward[i].split("_");
            PersonAttr personAttr = null;
            TextView task_reward = new TextView(context);
            task_reward.setPadding(0, 0, 5, 0);
            task_reward.setText("");
            if (singleReward[0].equals("經驗值") && showExp == true) { // 經驗值
                task_reward.setText("Exp");
                task_reward.setTextColor(Color.parseColor("#fcfe66"));
            } else if (singleReward[0].equals("金幣") && showGold == true) { // 金幣
                task_reward.setText("金");
                task_reward.setTextColor(Color.parseColor("#dde000"));
            } else { // 其他
                personAttr = db.getPersonAttrByName(singleReward[0]);
                if (personAttr != null) {
                    task_reward.setText(personAttr.getShortName());
                    task_reward
                            .setTextColor(Color.parseColor(personAttr.getColor()));
                }
            }
            if (!task_reward.getText().toString().equals("") &&
                    shortNameNumber < TotalShortNameNumber) {
                if (shortNameNumber == 0) {
                    TextView tv = new TextView(context);
                    tv.setText("任務獎勵:");
                    tv.setTextColor(Color.parseColor("#ad9281"));
                    tv.setPadding(0, 0, 10, 0);
                    holder.task_reward_layout.addView(tv);
                }
                shortNameNumber++;
                holder.task_reward_layout.addView(task_reward);
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    class GroupViewHolder {
        TextView date, task_number;
        ImageView indicator;
    }

    class ChildViewHolder {
        TextView task_id, task_description, task_start_time, task_finish_time,
                task_total_progress;
        LinearLayout task_reward_layout;
    }


}
