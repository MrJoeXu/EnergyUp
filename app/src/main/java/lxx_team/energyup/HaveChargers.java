package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

public class HaveChargers extends Activity implements View.OnClickListener {

    AVUser currentUser;
    boolean[] selected = new boolean[10];
    String insId;
    boolean flag;

    protected void switchPhase(int i, View vi){
        if(selected[i]){
            selected[i] = false;
            Button view = (Button) vi;
            view.getBackground().clearColorFilter();
        }
        else{
            selected[i] = true;
            Button view = (Button) vi;
            view.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
    }

//    private void uploadInstallationId() {
//        //Toast.makeText(getApplicationContext(),
//        // currentUser.getEmail().toString(), Toast.LENGTH_LONG).show();
//        currentUser = AVUser.getCurrentUser();
//        AVQuery<AVObject> targetUser = new AVQuery<>("Inventory");
//        targetUser.whereEqualTo("userId", currentUser.getEmail().toString());
//        LogUtil.log.d(currentUser.getEmail());
//
//        targetUser.getFirstInBackground(new GetCallback<AVObject>() {
//
//            @Override
//            public void done(AVObject avObject, AVException e) {
//                if (e == null) {
//                    avObject.put("installationID", insId);
//                    avObject.saveInBackground();
//                } else {
//                    LogUtil.log.d(e.getMessage());
//                }
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_chargers);

        for(int i = 0; i < 10; i++){
            selected[i] = false;
        }

        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    insId = AVInstallation.getCurrentInstallation().getInstallationId();
                    flag = true;
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_have_chargers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_lighting:
                switchPhase(0, v);
                break;
            case R.id.btn_30pins:
                switchPhase(1, v);
                break;
            case R.id.btn_miniusb:
                switchPhase(2, v);
                break;
            case R.id.btn_microusb:
                switchPhase(3, v);
                break;
            case R.id.btn_typec:
                switchPhase(4, v);
                break;
            case R.id.btn_apple:
                switchPhase(5, v);
                break;
            case R.id.btn_hp:
                switchPhase(6, v);
                break;
            case R.id.btn_dell:
                switchPhase(7, v);
                break;
            case R.id.submit_button:

                break;
            default:
                break;
        }
    }

    public void sendMessge(View v) {
        AVUser currentUser = AVUser.getCurrentUser();
        String userId;
        AVQuery<AVObject> avQuery = new AVQuery<>("Inventory");
        final AVObject log = new AVObject("Inventory");

        if (currentUser != null) {
            userId = currentUser.getEmail();
        } else {
            userId = "xuziyaoshabi";
        }

        avQuery.whereEqualTo("userId", userId);
        avQuery.setLimit(5);
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject o : list) {
                        o.deleteInBackground();
                    }
                } else {
                    Log.d("App", "Error: " + e.getMessage());
                }
            }
        });

        log.put("userId", userId);
        log.put("lighting", selected[0]);
        log.put("pins", selected[1]);
        log.put("miniusb", selected[2]);
        log.put("microusb", selected[3]);
        log.put("typec", selected[4]);
        log.put("apple", selected[5]);
        log.put("hp", selected[6]);
        log.put("dell", selected[7]);
        if(flag) {
            log.put("installationID", insId);
        }
        log.saveInBackground();

        //final Intent displayIntent = new Intent(this, MainActivity.class);
        //startActivity(displayIntent);
        finish();
    }

}
