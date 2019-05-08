package com.jager.risitaslaugh;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The configuration screen for the {@link RisitasLaughWidget RisitasLaughWidget} AppWidget.
 */
public class RisitasLaughWidgetConfigureActivity extends Activity implements CompoundButton.OnCheckedChangeListener
{
       private static final String PREFS_NAME = "com.jager.risitaslaugh.RisitasLaughWidget";
       private static final String PREF_PREFIX_KEY = "risitas_laugh_id_array_";
       private static final int CHECKBOX_BASE_ID = 100000;
       int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
       CheckBox checkAllLaughs;
       LinearLayout panelLaughs;
       Button addButton;
       private MediaPlayer player;

       View.OnClickListener mOnClickListener = new View.OnClickListener()
       {
              public void onClick(View v)
              {
                     final Context context = RisitasLaughWidgetConfigureActivity.this;
                     stopMediaPlayer();
                     releaseMediaPlayer();
                     // When the button is clicked, store the string locally
                     List<String> laughIndexes = getChosenSoundIndexes();
                     saveSelectedIndexPrefs(context, mAppWidgetId, laughIndexes);

                     // It is the responsibility of the configuration activity to update the app widget
                     AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                     RisitasLaughWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                     // Make sure we pass back the original appWidgetId
                     Intent resultValue = new Intent();
                     resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                     setResult(RESULT_OK, resultValue);
                     finish();
              }
       };

       public RisitasLaughWidgetConfigureActivity()
       {
              super();
       }

       private List<String> getChosenSoundIndexes()
       {
              List<String> indexes = new ArrayList<>();
              for (int i = 0; i < panelLaughs.getChildCount(); i++)
              {
                     CheckBox checkBox = (CheckBox) panelLaughs.getChildAt(i);
                     if (checkBox.getId() != checkAllLaughs.getId() && checkBox.isChecked())
                     {
                            indexes.add(String.valueOf(checkBox.getId() - CHECKBOX_BASE_ID));
                     }
              }
              return indexes;
       }

       private void stopMediaPlayer()
       {
              if (player != null && player.isPlaying())
              {
                     player.stop();
              }
       }

       private void releaseMediaPlayer()
       {
              if (player != null)
              {
                     player.release();
              }
       }

       private void playExampleLaugh(int soundID)
       {
              Log.d(RisitasLaughWidgetConfigureActivity.class.getName(), String.format("Playing example laugh sound %s", soundID));
              stopMediaPlayer();
              player = MediaPlayer.create(this, soundID);
              player.start();
       }

       @Override
       public void onCreate(Bundle icicle)
       {
              super.onCreate(icicle);

              // Set the result to CANCELED.  This will cause the widget host to cancel
              // out of the widget placement if the user presses the back button.
              setResult(RESULT_CANCELED);

              setContentView(R.layout.risitas_laugh_widget_configure);

              String appId = getResources().getString(R.string.admob_app_id);
              MobileAds.initialize(this, appId);
              AdView mainAdView = findViewById(R.id.adview_widget_config);
              AdRequest adRequest = new AdRequest.Builder().build();
              mainAdView.loadAd(adRequest);

              checkAllLaughs = findViewById(R.id.check_all_laughs);
              checkAllLaughs.setOnCheckedChangeListener(this);
              panelLaughs = findViewById(R.id.panel_laughs);
              addButton = findViewById(R.id.add_button);

              // Find the widget id from the intent.
              Intent intent = getIntent();
              Bundle extras = intent.getExtras();
              if (extras != null)
              {
                     mAppWidgetId = extras.getInt(
                             AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
              }

              // If this activity was started with an intent without an app widget ID, finish with an error.
              if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
              {
                     finish();
                     return;
              }

              int i = 0;
              for(int res : WidgetInstance.getSoundResources())
              {
                     CheckBox laughCheckbox = new CheckBox(this);
                     LayoutParams params = new LayoutParams(
                             LayoutParams.MATCH_PARENT,
                             LayoutParams.WRAP_CONTENT
                     );
                     params.setMargins(3, 5, 3, 5);
                     laughCheckbox.setLayoutParams(params);
                     laughCheckbox.setText(String.format("Laugh %s", i+1));
                     int id = CHECKBOX_BASE_ID + i;
                     i++;
                     laughCheckbox.setId(id);
                     laughCheckbox.setOnCheckedChangeListener(this);
                     panelLaughs.addView(laughCheckbox);
              }

              checkAllLaughs.setChecked(true);

              findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

//              mAppWidgetText.setText(loadChosenIndexPrefs(RisitasLaughWidgetConfigureActivity.this, mAppWidgetId));
       }

       // Write the prefix to the SharedPreferences object for this widget
       static void saveSelectedIndexPrefs(Context context, int appWidgetId, List<String> ids)
       {
              Log.d(RisitasLaughWidgetConfigureActivity.class.getName(), String.format("Saving preferences for widget: %s", appWidgetId));
              SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
              Set<String> idSet= new HashSet<>(ids);
              prefs.putStringSet(PREF_PREFIX_KEY + appWidgetId, idSet);
              prefs.apply();
       }

       // Read the prefix from the SharedPreferences object for this widget.
       // If there is no preference saved, get the default from a resource
       static Set<String> loadChosenIndexPrefs(Context context, int appWidgetId)
       {
              SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
              return prefs.getStringSet(PREF_PREFIX_KEY + appWidgetId, null);
       }

       static void deleteChosenIndexPrefs(Context context, int appWidgetId)
       {
              Log.d(RisitasLaughWidgetConfigureActivity.class.getName(), String.format("Removing preferences for widget: %s", appWidgetId));
              SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
              prefs.remove(PREF_PREFIX_KEY + appWidgetId);
              prefs.apply();
       }

       boolean isAtLeastOneChecked()
       {
              for (int i = 0; i < panelLaughs.getChildCount(); i++)
              {
                     CheckBox checkBox = (CheckBox) panelLaughs.getChildAt(i);
                     if (checkBox.isChecked()) return true;
              }
              return false;
       }

       @Override
       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
       {
              if (buttonView.getId() == R.id.check_all_laughs)
              {
                     checkAllLaughs.setText(isChecked? "Uncheck all": "Select all");
                     for (int i = 0; i < panelLaughs.getChildCount(); i++)
                     {
                            CheckBox checkBox = (CheckBox) panelLaughs.getChildAt(i);
                            if (checkBox.getId() != buttonView.getId())
                            {
                                   checkBox.setOnCheckedChangeListener(null);
                                   checkBox.setChecked(isChecked);
                                   checkBox.setOnCheckedChangeListener(this);
                            }
                     }
              }
              else
              {
                     if (!isChecked)
                     {
                            checkAllLaughs.setOnCheckedChangeListener(null);
                            checkAllLaughs.setChecked(false);
                            checkAllLaughs.setOnCheckedChangeListener(this);
                            stopMediaPlayer();
                     }
                     else
                     {
                            int soundId = WidgetInstance.getSoundResources()[buttonView.getId() - CHECKBOX_BASE_ID];
                            playExampleLaugh(soundId);
                     }
              }
              if(isAtLeastOneChecked()) addButton.setEnabled(true);
              else addButton.setEnabled(false);
       }
}

