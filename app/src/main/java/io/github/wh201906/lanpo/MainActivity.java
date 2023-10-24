package io.github.wh201906.lanpo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    public static final String ACTION_LOAD_MAINACTIVITY = "io.github.wh201906.lanpo.ACTION_LOAD_MAINACTIVITY";
    public static final String ACTION_EXIT = "io.github.wh201906.lanpo.ACTION_EXIT";
    private boolean canDrawOverlays = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w(TAG, "onCreate: " + getIntent());
        ProcessIntent();

        Button exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v ->
        {
            stopService(new Intent(this, FloatingService.class));
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
            {
                if (Settings.canDrawOverlays(MainActivity.this))
                {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_permission_granted), Toast.LENGTH_SHORT).show();
                    startFloatingService();
                }
                else
                {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_permission_denied), Toast.LENGTH_SHORT).show();
                }
            });
            mStartForResult.launch(intent);
        }
        else
        {
            canDrawOverlays = true;
        }

        if (canDrawOverlays)
        {
            startFloatingService();
        }
        else
        {
            Toast.makeText(this, getString(R.string.toast_no_permission), Toast.LENGTH_SHORT).show();
        }
    }

    private void startFloatingService()
    {
        Intent serviceIntent = new Intent(this, FloatingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(serviceIntent);
        }
        else
        {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Log.w(TAG, "onNewIntent: " + intent.toString());
        setIntent(intent);
        ProcessIntent();
    }

    private void ProcessIntent()
    {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == null) action = "(none)";

        Log.w(TAG, "ProcessIntent: " + intent + ", action: " + action);
        if (action.equals(ACTION_EXIT))
        {
            // same as exitButton.setOnClickListener(...)
            stopService(new Intent(this, FloatingService.class));
            finish();
        }
    }
}
