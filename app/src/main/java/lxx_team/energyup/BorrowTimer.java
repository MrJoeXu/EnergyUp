package lxx_team.energyup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.io.IOException;
import java.util.List;

public class BorrowTimer extends Activity {

    private int recLen = 15;
    private TextView txtView;
    BorrowTimer thistimer = this;

    private PendingIntent pendingIntent;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_borrow_timer);
        txtView = (TextView)findViewById(R.id.txttime);


        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        private Thread currentThread;
        @Override
        public void run() {
            currentThread = Thread.currentThread();
            recLen--;
            if(recLen < 1){

                //get the lastest update
                AVQuery query = new AVQuery("Log");
                query.orderByDescending("updateAt");
                query.setLimit(1);


                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            for (AVObject o : list) {
                                //if we found the user
                                String s = o.getString("renter");
                                o.deleteInBackground();
                                AVObject logs = new AVObject("Log");
                                logs.put("renter", s);
                                logs.put("borrower", AVUser.getCurrentUser().getEmail());
                                logs.saveInBackground();

                                //go to the tracking page
                                final Intent displayIntent = new Intent(thistimer, DisplayChargers.class);
                                startActivity(displayIntent);
                                handler.removeCallbacks(runnable);
                                finish();

                            }
                        } else {
                            Log.d("App", "Error: " + e.getMessage());

                            //go to the main page
                            final Intent displayIntent = new Intent(thistimer, MainActivity.class);
                            startActivity(displayIntent);
                            handler.removeCallbacks(runnable);
                            finish();

                        }
                    }
                });
            }
            String s = String.format("%02d:%02d",recLen/60, recLen%60);
            txtView.setText(s);
            handler.postDelayed(this, 1000);
        }

    };

}



