package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

public class SendRequestActivity extends Activity {

    AVUser currentUser;
    int needEnergy, timeDuration;
    int maxDuration = 180;
    TextView errTextView;
    EditText editDuration, editEnergy;
    String insId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);


        // Move to MainActivity Later
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    insId = AVInstallation.getCurrentInstallation().getInstallationId();
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });


        // Move to MainActivity Later
        PushService.setDefaultPushCallback(this, BorrowChargerActivity.class);

        final Button btnSubmit = (Button) findViewById(R.id.btn_submit_request);
        errTextView = (TextView)findViewById(R.id.request_error_text);
        currentUser = AVUser.getCurrentUser();





        btnSubmit.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(getApplicationContext(), UserLogin.class);


            public void onClick(View v) {

                editDuration = (EditText) findViewById(R.id.edit_time_duration);
                editEnergy = (EditText) findViewById(R.id.edit_spend_energy);



                if (currentUser == null) { // Not login then direct to login

                    startActivity(intent);
                } else if (timeDuration > maxDuration) {

                    errTextView.setText("You can only borrow a charger no more than 3 hours...");
                } else if (editDuration.getText().toString().trim().length() == 0 || editEnergy.getText().toString().trim().length() == 0) {
                    errTextView.setText("Please enter required information!");
                } else {
                    needEnergy = Integer.parseInt(editEnergy.getText().toString());
                    timeDuration = Integer.parseInt(editDuration.getText().toString());
                    submitRequest();
                }


            }
        });
    }

    private void submitRequest() {
        String email = currentUser.getEmail();
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("email", email);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            public void done(AVObject avObject, AVException e) {
                int userEnergy = avObject.getInt("energy");
                if (needEnergy > userEnergy) {
                    errTextView.setText("You don't have enough Energy!");
                } else {
                    errTextView.setText("");
                    Intent intent = new Intent(getApplicationContext(), DisplayChargers.class);
                    startActivity(intent);
                }

            }
        });

    }


    private void uploadInstallationId() {
        AVQuery<AVObject> targetUser = new AVQuery<>("Location");
        targetUser.whereEqualTo("userId", currentUser.getEmail());
        targetUser.getFirstInBackground(new GetCallback<AVObject>(){

            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    avObject.put("installationID", insId);
                }
            }
        });
    }



}

