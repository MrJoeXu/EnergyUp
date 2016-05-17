package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;

import java.util.List;

public class SendRequestActivity extends Activity {

    AVUser currentUser;
    int needEnergy, timeDuration;
    int maxDuration = 180;
    TextView errTextView;
    EditText editDuration, editEnergy;
    String insId;
    int requestDevice;
    String chargerType = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        Intent mIntent = getIntent();
        Bundle b = mIntent.getExtras();
        if (b!=null){
            requestDevice = (int) b.get("REQUEST_DEVICE_ID");
        }
        else {
            requestDevice = -1;
        }
        Log.d("Test","num = " +requestDevice);
        //requestDevice = mIntent.getIntExtra("EXTRA_SESSION_ID", 0);
        //Toast.makeText(getApplicationContext(),
        //       requestDevice, Toast.LENGTH_LONG).show();

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
        PushService.setDefaultPushCallback(this, AcceptRequest.class);

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
                    //uploadInstallationId();
                    needEnergy = Integer.parseInt(editEnergy.getText().toString());
                    timeDuration = Integer.parseInt(editDuration.getText().toString());
                    submitRequest();
                }


            }
        });
    }

    private void submitRequest() {
        String email = currentUser.getEmail();
        final AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("email", email);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            public void done(AVObject avObject, AVException e) {
                int userEnergy = avObject.getInt("energy");
                if (needEnergy > userEnergy) {
                    errTextView.setText("You don't have enough Energy!");
                } else {
                    errTextView.setText("");
                    Intent intent = new Intent(getApplicationContext(), BorrowTimer.class);
                    startActivity(intent);
                    queryChargers();


                }

            }
        });

    }



    private void uploadInstallationId() {
        //Toast.makeText(getApplicationContext(),
        // currentUser.getEmail().toString(), Toast.LENGTH_LONG).show();
        AVQuery<AVObject> targetUser = new AVQuery<>("Inventory");
        targetUser.whereEqualTo("userId", currentUser.getEmail().toString());
        LogUtil.log.d(currentUser.getEmail());

        targetUser.getFirstInBackground(new GetCallback<AVObject>() {

            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    avObject.put("installationID", insId);
                    avObject.saveInBackground();
                } else {
                    LogUtil.log.d(e.getMessage());
                }
            }
        });
    }

    AVObject message = new AVObject("Log");

    private void queryChargers() {

        AVUser thisUser = AVUser.getCurrentUser();
        String email = thisUser.getEmail();

        //query inventory-find myself
        AVQuery<AVObject> mquery = new AVQuery<>("Inventory");
        //query inventory-find others
        AVQuery<AVObject> query = new AVQuery<>("Inventory");


        //find myself
        mquery.whereEqualTo("userId", email);
        mquery.setLimit(3);

        mquery.findInBackground(new FindCallback<AVObject>() {
            @Override

            public void done(List<AVObject> list, AVException e) {

                for (AVObject i : list) {
                    i.deleteInBackground();
                }
            }
        });

        //set myself to have nothing
        AVObject myself = new AVObject("Inventory");
        myself.put("userId", email);
        //for testing
        //myself.put("lighting",true);
        myself.put("installationID",insId);

        myself.saveInBackground();

        //Toast.makeText(getApplicationContext(),
        //        requestDevice, Toast.LENGTH_LONG).show();
        switch (requestDevice) {
            case 0:
                query.whereEqualTo("lighting", true);
                chargerType = "lighting charger";
                break;
            case 1:
                query.whereEqualTo("pins", true);
                chargerType = "3pins charger";
                break;
            case 2:
                query.whereEqualTo("miniusb", true);
                chargerType = "mini usb charger";
                break;
            case 3:
                query.whereEqualTo("microusb", true);
                chargerType = "micro usb charger";
                break;
            case 4:
                query.whereEqualTo("typec", true);
                chargerType = "type c charger";
                break;
            case 5:
                query.whereEqualTo("apple", true);
                chargerType = "Apple laptop charger";
                break;
            case 6:
                query.whereEqualTo("hp", true);
                chargerType = "HP laptop charger";
                break;
            case 7:
                query.whereEqualTo("dell", true);
                chargerType = "Dell laptop charger";
                break;

        }

        //put the borrow log in the background
        message.put("borrower", email);


        query.setLimit(5);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override

            public void done(List<AVObject> list, AVException e) {
                if(list != null){
                    int counter = 1;
                    for (AVObject i : list) {
                        //Toast.makeText(getApplicationContext(),
                                //"FOUND!!!!", Toast.LENGTH_LONG).show();
                        //avQuery
                        String pushTarget = i.getString("installationID");
                        message.put("receiver" + counter, i.getString("userId"));
                        pushNotification(pushTarget);
                        counter++;
                    }
                }
                //push the log to server
                message.saveInBackground();
            }
        });

    }

    private void pushNotification(String pushTarget) {

        Log.e("test","CAN PUSH");
        AVQuery pushQuery = AVInstallation.getQuery();
        pushQuery.whereEqualTo("installationId", pushTarget);
        AVPush.sendMessageInBackground("Someone want a " + chargerType + " from you", pushQuery, new SendCallback() {
            @Override
            public void done(AVException e) {

            }
        });
    }



}