package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

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

    //post the id to the server
    public void acceptPost(View v){
        AVUser thisUser = AVUser.getCurrentUser();
        String email = thisUser.getEmail();

        AVObject message = new AVObject("Log");
        message.put("renter",email);

        //转到一个页面？或者等信号
        final Intent displayIntent = new Intent(this, BorrowTimer.class);
        startActivity(displayIntent);

        finish();
    }
}
