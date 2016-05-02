package lxx_team.energyup;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RentTimer extends Activity {

    private int recLen = 8;
    private TextView txtView;
    RentTimer thistimer = this;

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
                //whe finish borrow
                final Intent displayIntent = new Intent(thistimer, MainActivity.class);
                startActivity(displayIntent);
                handler.removeCallbacks(runnable);
                finish();
            }
            String s = String.format("%02d:%02d",recLen/60, recLen%60);
            txtView.setText(s);
            handler.postDelayed(this, 1000);
        }

    };
}
