package com.jager.risitaslaugh;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Set;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RisitasLaughWidgetConfigureActivity RisitasLaughWidgetConfigureActivity}
 */
public class RisitasLaughWidget extends AppWidgetProvider
{
       public static final String LAUGH_IDS = "laugh_ids";

       static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
       {
              Set<String> chosenIndexes = RisitasLaughWidgetConfigureActivity.loadChosenIndexPrefs(context, appWidgetId);
              if (chosenIndexes == null) return;

              String[] indexArray = {};
              if (chosenIndexes != null) indexArray = chosenIndexes.toArray(new String[0]);
              // Construct the RemoteViews object
              RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.risitas_laugh_widget);

              Intent svc = new Intent(context, PlayerService.class);
              svc.setAction(PlayerService.START_PLAYER);
              svc.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
              Bundle bundle = new Bundle();
              bundle.putStringArray(LAUGH_IDS, indexArray);
              svc.putExtras(bundle);
              int requestcode = appWidgetId;  // a request code-nak egyedinek kell lennie, különben ugyanaz a PendingIntent objektum jön létre, hiába különböznek az extra adatok!

              try
              {
                     PendingIntent pendingIntent;
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                     {
                            //Toast.makeText(context, "create pendingintent with req code: " + requestcode, Toast.LENGTH_SHORT).show();
                            pendingIntent = PendingIntent.getForegroundService(context, requestcode, svc, 0);
                            //Toast.makeText(context, "pendingintent created with req code: " + requestcode, Toast.LENGTH_SHORT).show();
                     } else
                            pendingIntent = PendingIntent.getService(context, requestcode, svc, 0);

                     views.setOnClickPendingIntent(R.id.risitas_img, pendingIntent);


                     // Instruct the widget manager to update the widget

                     appWidgetManager.updateAppWidget(appWidgetId, views);
              } catch (Exception e)
              {
                     Toast.makeText(context, "Failed to update widget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
              }
       }

       @Override
       public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
       {
              Toast.makeText(context, "Update widgets", Toast.LENGTH_SHORT).show();
              // There may be multiple widgets active, so update all of them
              for (int appWidgetId : appWidgetIds)
              {
                     updateAppWidget(context, appWidgetManager, appWidgetId);
              }
       }

       @Override
       public void onDeleted(Context context, int[] appWidgetIds)
       {
              super.onDeleted(context, appWidgetIds);
              PlayerService.deleteUnusedWidgetInstances(appWidgetIds);

              // When the user deletes the widget, delete the preference associated with it.
              for (int appWidgetId : appWidgetIds)
              {
                     RisitasLaughWidgetConfigureActivity.deleteChosenIndexPrefs(context, appWidgetId);
              }
       }

       @Override
       public void onEnabled(Context context)
       {
              // Enter relevant functionality for when the first widget is created
       }

       @Override
       public void onDisabled(Context context)
       {
              // Enter relevant functionality for when the last widget is disabled
       }
}

