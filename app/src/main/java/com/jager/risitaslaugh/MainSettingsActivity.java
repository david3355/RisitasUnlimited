package com.jager.risitaslaugh;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainSettingsActivity extends AppCompatActivity implements View.OnClickListener
{

       Button btnUpdateWidgets;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_main_settings);

              btnUpdateWidgets = findViewById(R.id.btn_update_widgets);
              btnUpdateWidgets.setOnClickListener(this);
       }

       void updateAllWidgets()
       {
              AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
              int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), RisitasLaughWidget.class));
              for (int widgetId : ids)
              {
                     RisitasLaughWidget.updateAppWidget(this, appWidgetManager, widgetId);
              }
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
