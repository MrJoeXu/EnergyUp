package lxx_team.energyup;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DisplayListContentFragment extends Fragment implements LocationListener {

    private LocationManager locationManager;
    double myLatitude;
    double myLongtitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //---------- Get Current Location -----------//
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        Criteria crit = new Criteria();
        String locationProvider = locationManager.getBestProvider(crit, true);
        final Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            myLatitude = location.getLatitude();
            myLongtitude = location.getLongitude();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1000, this);
        }


        //Toast for current location
        String s = "Latitude: " + myLatitude + "," + "Longtitude: " + myLongtitude;
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();


        final AVGeoPoint userLocation = new AVGeoPoint(myLatitude, myLongtitude);
        AVQuery<AVObject> query = new AVQuery<AVObject>("Location");

        query.whereNear("location", userLocation);

        query.setLimit(0);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    int counter = 0;

                    for (AVObject o : list) {

                        double distance = o.getAVGeoPoint("location").distanceInMilesTo(userLocation);
                        double rounded = (double) Math.round(distance * 100.0) / 100.0;
                        String strDistance = rounded + "  miles";
                        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.fragment_display_list);
                        TextView tv = (TextView) layout.getChildAt(counter);
                        tv.setText(strDistance);
                        counter++;
                    }
                } else {
                    Log.d("App", "Error: " + e.getMessage());
                }
            }
        });

        return inflater.inflate(R.layout.fragment_display_list_content, container, false);

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

