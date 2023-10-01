package io.github.wh201906.landport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class FloatingService extends Service
{
    private View overlayView;
    private LayoutParams overlayLayoutParams;

    @Override
    public void onCreate()
    {
        super.onCreate();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Context displayCtx = createDisplayContext(windowManager.getDefaultDisplay());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            displayCtx = displayCtx.createWindowContext(LayoutParams.TYPE_APPLICATION_OVERLAY, null);
        }

        overlayView = new View(displayCtx);

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


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(overlayView, overlayLayoutParams);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}