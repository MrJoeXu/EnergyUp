package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.tencent.qc.stat.common.User;

import static com.google.zxing.BarcodeFormat.*;

public class QRCodeActivity extends Activity {
    private ImageView imageView;
    private Bitmap bitmap;
    private String uname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
           uname = currentUser.getEmail();
        }
        else {
            Intent i = new Intent(QRCodeActivity.this, UserLogin.class);
            startActivity(i);
        }

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMartix = multiFormatWriter.encode(uname, QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMartix);
            //user.put("bitmap", bitmap);
        } catch (WriterException e1) {
            e1.printStackTrace();
        }
        /**
        final AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("userName", uname);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObj, AVException e) {
                if (e == null) {
                    //int urate = (int) avUser.get("value");
                    bitmap = (Bitmap) avObj.get("bitmap");
                    //Toast.makeText(FinishTransactionActivity.this, "User found", Toast.LENGTH_LONG);
                    //avgRating.setText("User" + uname + "rating is:" + avUser.get("value"));
                } else {
                    e.printStackTrace();
                    //Toast.makeText(FinishTransactionActivity.this, "User not found", Toast.LENGTH_LONG);
                }
            }
        });
         */


        imageView = (ImageView)this.findViewById(R.id.imageView);
        //Bitmap bitmap =
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrcode, menu);
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
