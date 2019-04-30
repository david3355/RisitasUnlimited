package com.jager.risitaslaugh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainSettingsActivity extends AppCompatActivity implements View.OnClickListener
{
       Button btnUpdateWidgets;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_main_settings);

              String appId = getResources().getString(R.string.admob_app_id);
              MobileAds.initialize(this, appId);
              AdView mainAdView = findViewById(R.id.adview_main_menu);
              AdRequest adRequest = new AdRequest.Builder().build();
              mainAdView.loadAd(adRequest);


              btnUpdateWidgets = findViewById(R.id.btn_update_widgets);
              btnUpdateWidgets.setOnClickListener(this);
       }

       void updateAllWidgets()
       {
              int[] ids = PlayerService.updateAllWidgets(this);
              Toast.makeText(this, String.format("All %s widgets were updated", ids.length), Toast.LENGTH_SHORT).show();
       }

       @Override
       public void onClick(View v)
       {
              switch (v.getId())
              {
                     case R.id.btn_update_widgets:
                            updateAllWidgets();
                            break;
              }
       }
}
