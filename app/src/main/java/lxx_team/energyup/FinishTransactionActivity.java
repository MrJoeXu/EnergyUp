package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

public class FinishTransactionActivity extends Activity {

    private float rating;
    private Button button;
    private RatingBar ratingBar;
    private TextView avgRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_transaction);

        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                final float rate = ratingBar.getRating();
                updateRating(rate);
            }
        });

        avgRating = (TextView) findViewById(R.id.avg_rating);



        getRating("yx");

        button = (Button) findViewById(R.id.submit_rate);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRating("yx");
                Intent mainIntent = new Intent(FinishTransactionActivity.this, UserLogin.class);
                startActivity(mainIntent);
                finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finish_transaction, menu);

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

    public void updateRating(final float rate){
        rating = rate;
        button.setEnabled(true);
    }

    /**
     * Get rating for the given user name
     * @param uname String user name
     */
    public void getRating (final String uname){
        final AVQuery<AVObject> query = new AVQuery<>("Rating");
        query.whereEqualTo("userName", uname);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avUser, AVException e) {
                if (e == null) {
                    //int urate = (int) avUser.get("value");
                    Toast.makeText(FinishTransactionActivity.this, "User found", Toast.LENGTH_LONG);
                    avgRating.setText("" + avUser.get("value"));
                } else {
                    //e.printStackTrace();
                    Toast.makeText(FinishTransactionActivity.this, "User not found", Toast.LENGTH_LONG);
                }
            }
        });
    }

    public void submitRating(final String uname){
        Log.d("test", "onsubmit");
        AVQuery<AVObject> query = new AVQuery<>("Rating");
        query.whereEqualTo("userName",uname);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avUser, AVException e) {
                if (e == null) {
                    //float urate = (float) user.get("avgRating");

                    avUser.put("value", rating);
                    avUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Log.d("test", "success test" + rating);
                            } else {
                                Log.d("test", "error:" + e.getMessage());
                            }
                        }
                    });
                    //Log.d("test","success test"+rating);
                    Toast.makeText(FinishTransactionActivity.this, "User Updated", Toast.LENGTH_LONG);
                    //avgRating.setText("User"+uname+"rating is:"+urate);
                } else {
                    //e.printStackTrace();
                    Toast.makeText(FinishTransactionActivity.this, "User not found", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
