package lxx_team.energyup;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.google.android.gms.location.FusedLocationProviderApi;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationUpdateService extends IntentService {

    double myLatitude;
    double myLongtitude;

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if(location != null){
            AVUser user = AVUser.getCurrentUser();
            String email;

            myLatitude = location.getLatitude();
            myLongtitude = location.getLongitude();

            AVGeoPoint myLoc = new AVGeoPoint(myLatitude,myLongtitude);

            if (user != null) {
                email = user.getEmail();
            } else {
                email = "xuziyaoshabi";
            }

            AVQuery<AVObject> avQuery = new AVQuery<>("Location");
            final AVObject log = new AVObject("Location");

            avQuery.whereEqualTo("userId", email);
            avQuery.setLimit(5);
            avQuery.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject o : list) {
                            o.deleteInBackground();
                        }
                    } else {
                        Log.d("App", "Error: " + e.getMessage());
                    }
                }
            });

            log.put("userId", email);
            log.put("location", myLoc);

            log.saveInBackground();

        }
    }

}
