package io.github.wh201906.lanpo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import androidx.core.app.NotificationCompat;

public class FloatingService extends Service
{
    private View overlayView;
    private LayoutParams overlayLayoutParams;
    private Notification notification;
    private boolean viewAdded = false;

    @Override
    public void onCreate()
    {
        super.onCreate();
        overlayView = new View(this);

        overlayLayoutParams = new LayoutParams(
                0, 0, 0, 0,
                LayoutParams.TYPE_SYSTEM_OVERLAY,
                LayoutParams.FLAG_NOT_FOCUSABLE
                        | LayoutParams.FLAG_NOT_TOUCHABLE
                        | LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            overlayLayoutParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        overlayLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        notification = createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        if (!viewAdded)
        {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(overlayView, overlayLayoutParams);
            viewAdded = true;
        }
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification createNotification()
    {
        String appName = getString(R.string.app_name);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_LOAD_MAINACTIVITY);
        PendingIntent loadActivityPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        intent.setAction(MainActivity.ACTION_EXIT);
        PendingIntent exitPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, appName)
                .setContentTitle(appName)
                .setContentText(appName)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(loadActivityPendingIntent)
                .addAction(R.mipmap.ic_launcher, getString(R.string.notification_exit), exitPendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(appName, appName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return builder.build();
    }

    @Override
    public void onDestroy()
    {
        if(viewAdded)
        {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(overlayView);
            viewAdded = false;
        }
        super.onDestroy();
    }
}
