package lxx_team.energyup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class BorrowChargerActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_charger);


        Button btn_lightning = (Button) findViewById(R.id.btn_lighting);
        btn_lightning.setOnClickListener(this); // calling onClick() method
        Button btn_30pins = (Button) findViewById(R.id.btn_30pins);
        btn_30pins.setOnClickListener(this);
        Button btn_miniusb = (Button) findViewById(R.id.btn_miniusb);
        btn_miniusb.setOnClickListener(this);
        Button btn_microusb = (Button) findViewById(R.id.btn_microusb);
        btn_microusb.setOnClickListener(this);
        Button btn_typec = (Button) findViewById(R.id.btn_typec);
        btn_typec.setOnClickListener(this);
        Button btn_apple = (Button) findViewById(R.id.btn_apple);
        btn_apple.setOnClickListener(this);
        Button btn_hp = (Button) findViewById(R.id.btn_hp);
        btn_hp.setOnClickListener(this);
        Button btn_dell = (Button) findViewById(R.id.btn_dell);
        btn_dell.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_borrow_charger, menu);
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
                startNextActivity(0);

                break;
            case R.id.btn_30pins:
                startNextActivity(1);

                break;
            case R.id.btn_miniusb:
                startNextActivity(2);

                break;
            case R.id.btn_microusb:
                startNextActivity(3);

                break;
            case R.id.btn_typec:
                startNextActivity(4);

                break;
            case R.id.btn_apple:
                startNextActivity(5);

                break;
            case R.id.btn_hp:
                startNextActivity(6);

                break;
            case R.id.btn_dell:
                startNextActivity(7);
                break;
        }
    }

    private void startNextActivity(int requestedDevice) {
        Intent intent = new Intent(BorrowChargerActivity.this, SendRequestActivity.class);
        intent.putExtra("REQUEST_DEVICE_ID", requestedDevice);
        startActivity(intent);
    }
}
