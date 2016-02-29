package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.superxlcrnote.app.R;

/**
 * Created by Superxlcr
 * On 2015/11/22
 * 欢迎界面，自动跳转到主界面，防止载入出现黑屏
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        SharedPreferences sp = getSharedPreferences("Person_info", 0);
        String name = sp.getString("name", "");
        TextView nameView = (TextView) findViewById(R.id.welcome_name);
        if (name.equals("")) {
            nameView.setVisibility(View.GONE);
        } else {
            nameView.setText(name);
        }

        // 检查activity是否在运行
        if (!MainListActivity.activityRunning) { // 未运行，延迟后跳转activity，避免产生黑屏

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this,
                            MainListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        } else {
            // 已运行，使用singleTop避免产生新的activity
            Intent intent = new Intent(WelcomeActivity.this, MainListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
