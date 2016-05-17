package lxx_team.energyup;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DisplayListContentFragment extends Fragment implements LocationListener {

    private LocationManager locationManager;
    double myLatitude;
    double myLongtitude;

    boolean[] renterflag = {false};
    String[] renterLoc = new String[1];
    AVGeoPoint userLocation;

    boolean returnToMainFlag = false;
    private String phoneNum;


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

        //user location and others
        userLocation = new AVGeoPoint(myLatitude, myLongtitude);

        renterflag[0] = false;
        renterLoc[0] = "";
        //check if this is someone who borrow it
        AVQuery avQuery = new AVQuery("Log");
        avQuery.orderByDescending("updateAt");
        avQuery.setLimit(1);
        avQuery.whereEqualTo("borrower", AVUser.getCurrentUser().getEmail());
        avQuery.findInBackground(new FindCallback<AVObject>() {

            @Override
            public void done (List < AVObject > list, AVException e){
                if(list.size() != 0 && list != null) {
                    for (AVObject o : list) {
                        //set to query who rent it out
                        renterLoc[0] = o.getString("renter");
                        renterflag[0] = false;
                    }
                }
                else{
                    renterflag[0] = true;
                }
            }
        });

        // if it is someone who rent it out
        if(renterflag[0]){
            AVQuery aQuery = new AVQuery("Log");
            aQuery.orderByDescending("updateAt");
            aQuery.setLimit(1);
            aQuery.whereEqualTo("renter", AVUser.getCurrentUser().getEmail());
            aQuery.findInBackground(new FindCallback<AVObject>() {

                @Override
                public void done (List < AVObject > list, AVException e){
                    if(list.size() != 0 && list != null) {
                        for (AVObject o : list) {
                            renterLoc[0] = o.getString("borrower");
                        }
                    }
                    else{
                        Intent requestIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(requestIntent);
                        returnToMainFlag = true;
                        //getActivity().finish();
                    }
                }
            });
        }

        if(returnToMainFlag){
            //delete data
            AVQuery aQuery = new AVQuery("Log");
            aQuery.setLimit(3);
            aQuery.whereEqualTo("borrower", AVUser.getCurrentUser().getEmail());
            aQuery.findInBackground(new FindCallback<AVObject>() {

                @Override
                public void done(List< AVObject > list, AVException e) {
                    if (list != null) {
                        for (AVObject o : list) {
                            o.deleteInBackground();
                        }
                    }
                }
            });
            //return to main
            getActivity().finish();
        }

        AVQuery<AVObject> query = new AVQuery<AVObject>("Location");

        query.whereNear("location", userLocation);
        query.whereEqualTo("userId", renterLoc[0]);
        query.setLimit(1);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    int counter = 0;

                    for (AVObject o : list) {

                        double distance = o.getAVGeoPoint("location").distanceInMilesTo(userLocation);
                        double rounded = (double) Math.round(distance * 100.0) / 100.0;
                        String strDistance = rounded + "";
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

        //handler.postDelayed(runnable, 10 * 1000);
        return inflater.inflate(R.layout.fragment_display_list_content, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final Button verify = (Button) getActivity().findViewById(R.id.verify_btn);


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AVUser currentUser = AVUser.getCurrentUser();
                if (!renterflag[0]) {
                    Intent requestIntent = new Intent(getActivity(), ScanCodeActivity.class);
                    startActivity(requestIntent);
                } else {
                    Intent requestIntent = new Intent(getActivity(), QRCodeActivity.class);
                    startActivity(requestIntent);
                }
            }
        });

        final Button callBtn = (Button) getActivity().findViewById(R.id.call_btn);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findOtherPhoneNum();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 10 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                AVQuery<AVObject> query = new AVQuery<AVObject>("Location");

                query.whereNear("location", userLocation);
                query.whereEqualTo("userId", renterLoc[0]);
                query.setLimit(1);

                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            int counter = 0;

                            for (AVObject o : list) {

                                double distance = o.getAVGeoPoint("location").distanceInMilesTo(userLocation);
                                double rounded = (double) Math.round(distance * 100.0) / 100.0;
                                String strDistance = rounded + "";
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
            } finally {
                handler.postDelayed(runnable, 10 * 1000);
            }

        }
    };

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongtitude = location.getLongitude();
        userLocation = new AVGeoPoint(myLatitude, myLongtitude);
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

    public void findOtherPhoneNum() {
        // Get his/her phone num with his/her email by query _User




        String email = AVUser.getCurrentUser().getEmail();
        final AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("email", email);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    phoneNum = avObject.getString("mobilePhoneNumber");
                }
            }

        });

        String p = "tel:" + phoneNum;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(p));
        startActivity(callIntent);


    }
}

