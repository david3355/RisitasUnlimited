package com.jager.risitaslaugh;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerService extends Service implements MediaStoppedHandler
{
       public PlayerService()
       {
       }

       private final static int NOTIFICATION_ID = 255;
       private final static String CHANNEL_ID = "risitas_notifications";
       private static List<WidgetInstance> widgetInstances = new ArrayList<>();

       private BroadcastReceiver screenOffReceiver = new BroadcastReceiver()
       {
              @Override
              public void onReceive(Context context, Intent intent)
              {
                     stopAllMedia();
                     stopSelf();
              }
       };

       public static final String START_PLAYER = "startplayer";

       @Override
       public int onStartCommand(Intent intent, int flags, int startId)
       {
              registerScreenOffReceiver();
              operate(intent);
//              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || true)
//              {
//                     timer = new Timer("foregrounder") ;
//                     TimerTask task = new TimerTask()
//                     {
//                            @Override
//                            public void run()
//                            {
//                                   startForeground(NOTIFICATION_ID, buildNotification());
//                            }
//                     };
//                     timer.schedule(task, 4500);
//              }
              return super.onStartCommand(intent, flags, startId);
       }

       private Notification buildNotification()
       {
              NotificationManager notificationManager =
                      (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
              if (notificationManager == null) return null;

              NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID);
              notifBuilder.setContentTitle("Risitas is laughing")
//                      .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.id.risitas_img))
//                      .setSmallIcon(R.id.risitas_img)
                      .setStyle(new NotificationCompat.BigTextStyle());

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
              {
                     NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                             "Risitas laugh channel",
                             NotificationManager.IMPORTANCE_MIN);
                     notificationManager.createNotificationChannel(channel);
                     notifBuilder.setChannelId(CHANNEL_ID);
              }

              Notification notif = notifBuilder.build();
              notif.flags |= Notification.FLAG_AUTO_CANCEL;
              return notif;
       }

       private void operate(Intent intent)
       {
              if (intent == null) return;

              int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
              Bundle bundle = intent.getExtras();
              String[] laughIds = bundle.getStringArray(RisitasLaughWidget.LAUGH_IDS);
              WidgetInstance widget = getWidgetByID(widgetId);
              if (widget == null)
              {
                     widget = new WidgetInstance(widgetId, getApplicationContext(), intent, this, laughIds);
                     widgetInstances.add(widget);
              }
              if (!widget.isPlaying())
              {
                     widget.playMedia();
              } else widget.stopMedia();
       }

       private WidgetInstance getWidgetByID(int widgetID)
       {
              for (WidgetInstance winstance : widgetInstances)
              {
                     if (winstance.getWidgetID() == widgetID) return winstance;
              }
              return null;
       }

       private void stopAllMedia()
       {
              for (WidgetInstance winstance : widgetInstances)
              {
                     if (winstance.isPlaying()) winstance.stopMedia();
              }
       }

       private boolean isPlaying()
       {
              for (WidgetInstance winstance : widgetInstances)
              {
                     if (winstance.isPlaying()) return true;
              }
              return false;
       }

       public static void deleteUnusedWidgetInstances(int[] widgetIDs)
       {
              for (int wid : widgetIDs)
              {
                     removeWidgetInstanceByID(wid);
              }
       }

       private static void removeWidgetInstanceByID(int widgetID)
       {
              Iterator<WidgetInstance> iter = widgetInstances.iterator();
              while (iter.hasNext())
              {
                     WidgetInstance next = iter.next();
                     if (next.getWidgetID() == widgetID)
                     {
                            if (next.isPlaying()) next.stopMedia();
                            iter.remove();
                     }
              }
       }

       private void registerScreenOffReceiver()
       {
              IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
              registerReceiver(screenOffReceiver, filter);
       }

       @Override
       public IBinder onBind(Intent intent)
       {
              return null;
       }

       @Override
       public void onDestroy()
       {
              Toast.makeText(this, "DESTROY", Toast.LENGTH_SHORT).show();
              try{
              unregisterReceiver(screenOffReceiver);
              } catch (Exception e)
              {
                     Toast.makeText(this, "error while unregistering receiver: " + e.getMessage(), Toast.LENGTH_SHORT).show();
              }
              super.onDestroy();
       }

       @Override
       public void mediaStopped(int widgetID)
       {
              Toast.makeText(this, String.format("mediaStopped begins, running threads: %s", Thread.activeCount()), Toast.LENGTH_SHORT).show();
              if (!isPlaying())
              {
                     Toast.makeText(this, "mediaStopped: stopping service", Toast.LENGTH_SHORT).show();
                     try
                     {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            {
                                   stopForeground(true);
                            }

                            stopSelf();
                     } catch (Exception e)
                     {
                            Toast.makeText(this, "error while stopping svc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
              }
       }
}