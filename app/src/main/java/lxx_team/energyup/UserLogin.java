package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

@SuppressWarnings("unchecked")
public class UserLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);


        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        final EditText userEdit = (EditText)findViewById(R.id.username_login);
        final EditText passwordEdit = (EditText)findViewById(R.id.password_login);
        final TextView errMsg = (TextView)findViewById(R.id.logInErrMsg);

        final Button button = (Button) findViewById(R.id.btnSignUp);
        final Intent displayIntent = new Intent(this, MainActivity.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                AVUser.logInInBackground(user,password, new LogInCallback() {


                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            errMsg.setText("Success!");
                            startActivity(displayIntent);
                            finish();
                        } else {
                            errMsg.setText(e.getMessage());
                        }
                    }
                });
            }
        });


        final Button button1 = (Button) findViewById(R.id.btnRegister);
        final Intent signupIntent = new Intent(this, UserRegister.class);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
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
}
