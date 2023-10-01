package io.github.wh201906.lanpo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private boolean canDrawOverlays = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
