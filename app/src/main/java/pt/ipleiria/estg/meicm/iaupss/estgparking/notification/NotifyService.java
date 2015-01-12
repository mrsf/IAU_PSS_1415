package pt.ipleiria.estg.meicm.iaupss.estgparking.notification;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import pt.ipleiria.estg.meicm.iaupss.estgparking.LoginActivity;
import pt.ipleiria.estg.meicm.iaupss.estgparking.MainActivity;

/**
 * This service is started when an Alarm has been raised
 *
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 *
 * @source http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
 */
public class NotifyService extends Service {

    private static final String TAG = "NOTIFY_SERVICE";

    private static final int NOTIFICATION = 123;
    public static final String INTENT_NOTIFY = "pt.ipleiria.estg.meicm.iaupss.estgparking.notification.INTENT_NOTIFY";
    private NotificationManager notificationManager;

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    // This is the object that receives interactions from clients
    private final IBinder binder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {
        // This is the 'title' of the notification
        CharSequence title = "Alarm!!";
        // This is the icon to use on the notification
        int icon = R.drawable.ic_dialog_alert;
        // This is the scrolling text of the notification
        CharSequence text = "Your notification time is upon us.";
        // What time to show on the notification
        long time = System.currentTimeMillis();

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), 0);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setContentIntent(contentIntent)
                .getNotification();

        // Set the info for the views that show in the notification panel.
        // notification.setLatestEventInfo(this, title, text, contentIntent);

        // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        notificationManager.notify(NOTIFICATION, notification);

        // Stop the service when we are finished
        stopSelf();
    }
}
