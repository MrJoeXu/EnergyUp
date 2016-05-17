package lxx_team.energyup;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by JoeXu on 4/23/16.package lxx_team.energyup;

 import android.app.Application;

 /**
 * Created by JoeXu on 4/23/16.
 */
public class EnergyUp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize parameter for this, AppId, AppKey
        AVOSCloud.useAVCloudUS();

        AVOSCloud.initialize(this, "3z95KghEnMhAKeF96q0btdQ3-MdYXbMMI", "tV0H3rwu8S4dvXiuFd9XC0y6");
    }
}