package lxx_team.energyup;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.avos.avoscloud.LogUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


@SuppressWarnings("ResourceType")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DisplayMapContentFragment extends Fragment implements LocationListener {

    private LocationManager locationManager;
    double myLatitude;
    double myLongtitude;
    private GoogleMap map;
    private LatLng defaultLatLng;
    private int zoomLevel = 14;
    private String phoneNum;


    boolean returnToMainFlag = false;

    String[] renterLoc = new String[1];
    Boolean[] renterflag = {false};
    Bitmap smallMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_display_map_content, container, false);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

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


        // --------------- Set Up Map ----------------------//

        map = ((MapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map != null) {
            map.getUiSettings().setCompassEnabled(true);
            map.setTrafficEnabled(true);
            map.setMyLocationEnabled(true);
            // Move the camera instantly to defaultLatLng.
            defaultLatLng = new LatLng(myLatitude, myLongtitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoomLevel));
        }

        // --------------- Set Up Marker ----------------------//
        int height = 140;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


        // --------------- if this is the one who borrow ----------------------//
        AVQuery avQuery = new AVQuery("Log");
        avQuery.setLimit(1);
        avQuery.orderByDescending("updateAt");
        avQuery.whereEqualTo("borrower", AVUser.getCurrentUser().getEmail());
        avQuery.findInBackground(new FindCallback<AVObject>() {

            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size() != 0 && list != null) {
                    for (AVObject o : list) {
                        renterLoc[0] = o.getString("renter");
                        renterflag[0] = false;
                    }
                }
                else{
                    renterflag[0] = true;
                }
            }
        });

        // --------------- if this is the one who rent ----------------------//
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
                    }
                }
            });
        }

        // --------------- if no such person find ----------------------//
        if(returnToMainFlag){
            //delete data
            AVQuery aQuery = new AVQuery("Log");
            aQuery.setLimit(3);
            aQuery.whereEqualTo("borrower", AVUser.getCurrentUser().getEmail());
            aQuery.findInBackground(new FindCallback<AVObject>() {

                @Override
                public void done(List<AVObject> list, AVException e) {
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

        // --------------- find the other user and add marker ----------------------//
        final AVGeoPoint userLocation = new AVGeoPoint(myLatitude, myLongtitude);
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
                        LatLng nearLatLng = new LatLng(o.getAVGeoPoint("location").getLatitude(), o.getAVGeoPoint("location").getLongitude());
                        map.addMarker(new MarkerOptions().position(nearLatLng).title("" + (counter + 1)).snippet("This is the snippet within the InfoWindow").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        counter++;
                    }
                } else {
                    Log.d("App", "Error: " + e.getMessage());
                }
            }
        });

        // --------------- update evey 10 second ----------------------//
        handler.postDelayed(runnable, 10 * 1000);

     //   getActivity().findViewById(R.id.id_title_right_btn).setBackgroundResource(R.drawable.l);

        // --------------- Verify Button ----------------------//
        // renterflag[0] = ture ====> renter
        // renterflag[0] = false ===> borrower
        final Button verify = (Button) getActivity().findViewById(R.id.verify_btn);


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!renterflag[0]) {
                    Intent requestIntent = new Intent(getActivity(), ScanCodeActivity.class);
                    startActivity(requestIntent);
                }
                else {
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
    public void onPause() {
        if (map != null) {
            map.setMyLocationEnabled(false);
            map.setTrafficEnabled(false);
        }
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.setTrafficEnabled(true);
        }
        handler.postDelayed(runnable, 10 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongtitude = location.getLongitude();
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

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                final AVGeoPoint userLocation = new AVGeoPoint(myLatitude, myLongtitude);
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
                                map.clear();
                                LatLng nearLatLng = new LatLng(o.getAVGeoPoint("location").getLatitude(), o.getAVGeoPoint("location").getLongitude());
                                map.addMarker(new MarkerOptions().position(nearLatLng).title("" + (counter + 1)).snippet("This is the snippet within the InfoWindow").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
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
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
