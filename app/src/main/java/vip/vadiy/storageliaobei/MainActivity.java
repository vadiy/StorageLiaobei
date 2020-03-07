package vip.vadiy.storageliaobei;

import androidx.appcompat.app.AppCompatActivity;
import vip.vadiy.storageliaobei.Utils.DBUtil;
import vip.vadiy.storageliaobei.Utils.RootCMD;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    Context context;
    RadioButton rb_dump;
    RadioButton rb_upload;
    RadioButton rb_uuid;
    Button bt_doti;
    EditText et_data;
    EditText et_title;
    TextView tv_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        InitView();
        InitEvent();
        hasRoot();
    }

    private void InitView() {
        rb_dump = findViewById(R.id.rb_dump);
        rb_upload = findViewById(R.id.rb_upload);
        rb_uuid = findViewById(R.id.rb_uuid);
        bt_doti = findViewById(R.id.bt_doti);
        et_data = findViewById(R.id.et_data);
        et_title = findViewById(R.id.et_title);
        tv_root = findViewById(R.id.tv_root);
    }

    private void InitEvent() {
        tv_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasRoot();
            }
        });
        rb_dump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_upload.setChecked(false);
                    rb_uuid.setChecked(false);
                    bt_doti.setText("执行【" + rb_dump.getText().toString() + "】操作");
                }
            }
        });

        rb_upload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_dump.setChecked(false);
                    rb_uuid.setChecked(false);
                    bt_doti.setText("执行【" + rb_upload.getText().toString() + "】操作");
                }
            }
        });
        rb_uuid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_dump.setChecked(false);
                    rb_upload.setChecked(false);
                    bt_doti.setText("执行【" + rb_uuid.getText().toString() + "】操作");
                }
            }
        });
        bt_doti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> data = new HashMap<String, String>();
                String title = "";
                String result = "";
                String tag = "";
                if (rb_dump.isChecked()) {
                    et_title.setText("");
                    et_data.setText("");
                    data = DataUtil.dumpData(context);
                    if (data!=null){
                        title = data.get("title");
                        result = data.get("data");
                        et_title.setText(title);
                        et_data.setText(result);
                    }
                    if (result.isEmpty()) {
                        tag = rb_dump.getText().toString() + " 失败";
                    } else {
                        tag = rb_dump.getText().toString() + " 成功";
                    }
                } else if (rb_upload.isChecked()) {
                    data = DataUtil.insertData(context, et_data.getText().toString());
                    title = data.get("title");
                    DBUtil.log(TAG, "title=" + title);

                    if (title == null || title.isEmpty() == true) {
                        tag = rb_upload.getText().toString() + " 失败";
                    } else {
                        tag = rb_upload.getText().toString() + " 成功";
                        et_title.setText(data.get("title"));
                    }
                }else if(rb_uuid.isChecked()){
                    et_title.setText("");
                    et_data.setText("");
                    data = DataUtil.reSetDevice(context);
                    title = data.get("title");
                    if (title == null || title.isEmpty() == true) {
                        tag = rb_uuid.getText().toString() + " 失败";
                    } else {
                        tag = rb_uuid.getText().toString() + " 成功";
                        et_title.setText(data.get("title"));
                    }

                }
                Toast.makeText(context, tag, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void hasRoot() {
        if (RootCMD.isRooted()) {
            tv_root.setText("IS ROOT");
            tv_root.setTextColor(Color.parseColor("#2FDD0F"));
        } else {
            tv_root.setText("NO ROOT");
            tv_root.setTextColor(Color.parseColor("#F30622"));
        }
    }


}
