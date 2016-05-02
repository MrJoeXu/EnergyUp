package lxx_team.energyup;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


@SuppressWarnings("ResourceType")
public class DisplayChargers extends Activity implements LocationListener{

    double myLatitude;
    double myLongtitude;
    private boolean mapMode = false;



    protected Button btnSwitch;
    private DisplayListContentFragment fList;
    private DisplayMapContentFragment fMap;



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chargers);
        btnSwitch = (Button) findViewById(R.id.id_title_right_btn);


        btnSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button view = (Button) v;
                        view.getBackground().setColorFilter(Color.parseColor("#DFDDDC"), PorterDuff.Mode.MULTIPLY);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        if (!mapMode) {
                            if (fMap == null) {
                                fMap = new DisplayMapContentFragment();
                            }
                            transaction.replace(R.id.display_content, fMap);
                            transaction.commit();
                            btnSwitch.setBackgroundResource(R.drawable.list);
                            mapMode = true;
                        } else {
                            if (fList == null) {
                                fList = new DisplayListContentFragment();
                            }
                            transaction.replace(R.id.display_content, fList);
                            transaction.commit();
                            btnSwitch.setBackgroundResource(R.drawable.location);
                            mapMode = false;
                        }
                    case MotionEvent.ACTION_CANCEL: {
                        Button view = (Button) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        setDefaultFragment();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fList = new DisplayListContentFragment();
        transaction.replace(R.id.display_content, fList);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_chargers, menu);
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
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongtitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
