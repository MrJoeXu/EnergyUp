package lxx_team.energyup;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        // Set up the scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        integrator.setOrientationLocked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_code, menu);
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            Intent requestIntent = new Intent(ScanCodeActivity.this, FinishTransactionActivity.class);
            startActivity(requestIntent);

            if(result.getContents() == null) {
                Log.d("MainActivity","Cancelled Scan");
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this,"Scanned: "+result.getContents(),Toast.LENGTH_LONG).show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Created by JoeXu on 4/23/16.package lxx_team.energyup;

     import android.app.Application;

     /**
     * Created by JoeXu on 4/23/16.
     */
    public static class EnergyUp extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            // Initialize parameter for this, AppId, AppKey
            AVOSCloud.useAVCloudUS();

            AVOSCloud.initialize(this, "3z95KghEnMhAKeF96q0btdQ3-MdYXbMMI", "tV0H3rwu8S4dvXiuFd9XC0y6");
        }
    }
}
