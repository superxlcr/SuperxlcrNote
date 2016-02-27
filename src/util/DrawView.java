package util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.PersonAttr;

/**
 * Created by Superxlcr
 * On 2015/11/20
 */
public class DrawView {

    // ���ƴ���ͼ��Ľ�������
    // mode: 0 +, 1 X
    public static Map<String, List<TextView>> drawShowRewardLayout(Context context,
            LinearLayout layout, int width, int height, int perRowNumber,
            int padding, List<PersonAttr>list, int mode) {

        // ����Layout�е�TextView
        Map<String, List<TextView>> viewMap = new HashMap<String, List<TextView>>();

        // ���ԭ����
        layout.removeAllViews();

        // ��������
        LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(
                width, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsReward = new LinearLayout.LayoutParams(
                width / perRowNumber, height);

        // �����н�������
        LinearLayout rowLayout = new LinearLayout(context);
        rowLayout.setLayoutParams(layoutParamsRow);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setPadding(padding, 0, padding, 0);

        // �н�����Ŀ������
        int ll_contain = 0;
        for (int i = 0; i < list.size(); i++) {
            // �ⲿ���
            LinearLayout rewardLayout = new LinearLayout(context);
            rewardLayout.setLayoutParams(layoutParamsReward);
            rewardLayout.setGravity(Gravity.CENTER);

            // ͼ��
            ImageView icon = new ImageView(context);
            if (list.get(i).isHaveIcon()) {
                icon.setImageResource(list.get(i).getIcon());
                rewardLayout.addView(icon);
            }

            // ���Q
            TextView attrName = new TextView(context);
            attrName.setText(list.get(i).getName());
            attrName.setTextColor(Color.parseColor(list.get(i).getColor()));
            attrName.setPadding(padding, 0, 0, 0);
            attrName.setWidth(180);
            attrName.setGravity(Gravity.CENTER_HORIZONTAL);
            rewardLayout.addView(attrName);

            // ����
            TextView sign = new TextView(context);
            if (mode == 0) {
                sign.setText("+");
            } else {
                sign.setText("X");
            }
            sign.setPadding(padding, 0, padding, 0);
            sign.setTextColor(Color.parseColor(list.get(i).getColor()));
            rewardLayout.addView(sign);

            // ����
            TextView number = new TextView(context);
            number.setText(Integer.toString(list.get(i).getNumber()));
            number.setTextColor(Color.parseColor(list.get(i).getColor()));
            rewardLayout.addView(number);

            // ����
            List<TextView> viewList = new ArrayList<TextView>();
            viewList.add(attrName);
            viewList.add(sign);
            viewList.add(number);
            viewMap.put(list.get(i).getName(), viewList);

            rowLayout.addView(rewardLayout);
            ll_contain++;

            if (ll_contain == perRowNumber) {
                layout.addView(rowLayout);
                // ���ɲ���
                rowLayout = new LinearLayout(context);
                rowLayout.setLayoutParams(layoutParamsRow);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setPadding(padding, 0, padding, 0);
                ll_contain = 0;
            }

        }
        layout.addView(rowLayout);

        return viewMap;
    }
}
