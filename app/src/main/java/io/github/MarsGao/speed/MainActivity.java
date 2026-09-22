package io.github.MarsGao.speed;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {
    private static final float MIN_SPEED = 0.25f;
    private static final float MAX_SPEED = 4.0f;
    private static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText et = findViewById(R.id.editTextText);
        final Button button = findViewById(R.id.button);

        prefs = this.getSharedPreferences("speed", Context.MODE_PRIVATE);
        if (!prefs.contains("speed")) {
            prefs.edit().putFloat("speed", 1.5f).commit();
        }
        float initialSpeed = prefs.getFloat("speed", 1.5f);
        et.setText(String.valueOf(initialSpeed));
        makePrefsReadable();

        button.setOnClickListener(v -> {
            if (prefs == null) {
                Toast.makeText(getApplicationContext(), "未激活模块或不支持XSharedPreferences", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences.Editor e = prefs.edit();
                try {
                    float speed = Float.parseFloat(et.getText().toString());
                    if (!Float.isFinite(speed) || speed < MIN_SPEED || speed > MAX_SPEED) {
                        Toast.makeText(getApplicationContext(), "请输入 0.25 到 4.0 之间的倍速", Toast.LENGTH_LONG).show();
                        return;
                    }
                    e.putFloat("speed", speed);
                    if (e.commit()) {
                        makePrefsReadable();
                        Toast.makeText(getApplicationContext(), "设置成功，下一个视频生效", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException ignored) {
                    Toast.makeText(getApplicationContext(), "请输入有效数字", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void makePrefsReadable() {
        File dataDir = new File(getApplicationInfo().dataDir);
        dataDir.setReadable(false, false);
        dataDir.setReadable(true, true);
        dataDir.setExecutable(true, false);
        File prefsDir = new File(dataDir, "shared_prefs");
        if (prefsDir.exists()) {
            prefsDir.setReadable(true, false);
            prefsDir.setExecutable(true, false);
        }
        File prefsFile = new File(getApplicationInfo().dataDir + "/shared_prefs/speed.xml");
        if (prefsFile.exists()) {
            prefsFile.setReadable(true, false);
        }
    }
}
