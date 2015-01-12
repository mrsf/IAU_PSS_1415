package pt.ipleiria.estg.meicm.iaupss.estgparking.notification;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import pt.ipleiria.estg.meicm.iaupss.estgparking.task.AlarmTask;

/**
 * @source http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 */
public class ScheduleService extends Service {

    private static final String TAG = "SCHEDULE_SERVICE";

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        ScheduleService getService() {
            return ScheduleService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    // This is the object that receives interactions from clients. See
    private final IBinder binder = new ServiceBinder();

    /**
     * Show an alarm for a certain date when the alarm is called it will pop up a notification
     */
    public void setAlarm(Calendar c) {
        // This starts a new thread to set the alarm
        // You want to push off your tasks onto a new thread to free up the UI to carry on responding
        new AlarmTask(this, c).run();
    }
}
