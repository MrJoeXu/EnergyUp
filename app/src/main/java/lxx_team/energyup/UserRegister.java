package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class UserRegister extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        final EditText nameEdit = (EditText)findViewById(R.id.username);
        final EditText emailEdit = (EditText)findViewById(R.id.email_address);
        final EditText passwordEdit = (EditText)findViewById(R.id.password);
        final TextView errMsg = (TextView)findViewById(R.id.signUpErrMsg);

        final Button button = (Button) findViewById(R.id.btnSignUp);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String username = nameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                final AVUser user = new AVUser();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Log.d("App", "Sign Up Success!");
                            errMsg.setText("Success!");

                        } else {
                            Log.d("App", "Error: " + e.getMessage());
                            if (e.getCode() == 125) {
                                errMsg.setText("Invalid Email Address");
                            } else if (e.getCode() == 203) {
                                errMsg.setText("Email adress already taken!");
                            } else if (e.getCode() == 217) {
                                errMsg.setText("Invalid Username");
                            } else if (e.getCode() == 218) {
                                errMsg.setText("Invalid Password");
                            } else {
                                errMsg.setText(e.getMessage());
                            }

                        }

                    }
                });
                /*
                //generate qr code
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMartix = multiFormatWriter.encode(username, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMartix);
                    user.put("bitmap", bitmap);
                } catch (WriterException e1) {
                    e1.printStackTrace();
                }
                */
                Intent i = new Intent(UserRegister.this,QRCodeActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_register, menu);
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
