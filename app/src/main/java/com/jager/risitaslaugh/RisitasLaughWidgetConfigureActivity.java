package com.jager.risitaslaugh;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

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
       private MediaPlayer player;

       View.OnClickListener mOnClickListener = new View.OnClickListener()
       {
              public void onClick(View v)
              {
                     final Context context = RisitasLaughWidgetConfigureActivity.this;

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

       private void playExampleLaugh(int soundID)
       {
              if (player != null && player.isPlaying()) player.stop();
              player = player.create(this, soundID);
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
              checkAllLaughs = findViewById(R.id.check_all_laughs);
              checkAllLaughs.setOnCheckedChangeListener(this);
              panelLaughs = findViewById(R.id.panel_laughs);

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
                     params.setMargins(3, 5, 3, 10);
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
              Set<String> chosenIndexes = prefs.getStringSet(PREF_PREFIX_KEY + appWidgetId, null);
              return chosenIndexes;
       }

       static void deleteChosenIndexPrefs(Context context, int appWidgetId)
       {
              SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
              prefs.remove(PREF_PREFIX_KEY + appWidgetId);
              prefs.apply();
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
                            if (checkBox.getId() != buttonView.getId()) checkBox.setChecked(isChecked);
                     }
              }
              else
              {
                     if (!isChecked)
                     {
                            checkAllLaughs.setOnCheckedChangeListener(null);
                            checkAllLaughs.setChecked(false);
                            checkAllLaughs.setOnCheckedChangeListener(this);
                     }
                     else
                     {
                            int soundId = WidgetInstance.getSoundResources()[buttonView.getId() - CHECKBOX_BASE_ID];
                            playExampleLaugh(soundId);
                     }
              }
       }
}
