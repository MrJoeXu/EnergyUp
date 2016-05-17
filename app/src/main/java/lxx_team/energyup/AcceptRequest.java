package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;

import java.util.List;

public class AcceptRequest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accept_request, menu);
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

    AVUser thisUser = AVUser.getCurrentUser();
    String email = thisUser.getEmail();
    public boolean flag;

    //post the id to the server
    public void acceptPost(View v){

        int counter = 1;

        flag = false;

        while(flag == false && counter < 6) {
            AVQuery query = new AVQuery("Log");
            query.whereEqualTo("receiver" + counter, email);
            query.orderByDescending("updateAt");
            query.setLimit(1);

            //AVQuery query = new AVQuery("Log");
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (avObject != null) {
                        if (avObject.getString("renter") == null || avObject.getString("renter").equals("") || avObject.getString("renter").equals("ll@lll.com") ) {
                            flag = true;
                            avObject.put("renter", email);
                            avObject.saveInBackground();

                            final Intent displayIntent = new Intent(getBaseContext(), DisplayChargers.class);
                            startActivity(displayIntent);

                            finish();
                        }
                    }
                }


            });

            counter++;
        }

            final Intent displayIntent = new Intent(this, MainActivity.class);
            startActivity(displayIntent);

            finish();
        }


    public void denyPost(View v){
        final Intent displayIntent = new Intent(this, MainActivity.class);
        startActivity(displayIntent);

        finish();
    }
}
