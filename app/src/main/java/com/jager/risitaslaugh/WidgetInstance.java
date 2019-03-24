package com.jager.risitaslaugh;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Created by Jager on 2019.03.22.
 */
public class WidgetInstance implements MediaPlayer.OnCompletionListener
{
       public WidgetInstance(int widgetID, Context context, Intent intent, MediaStoppedHandler mediaStoppedHandler, String[] widgetIds)
       {
              this.widgetID = widgetID;
              this.context = context;
              this.intent = intent;
              this.mediaStoppedHandler = mediaStoppedHandler;
              setUsedIds(widgetIds);
       }

       private Intent intent;
       private Context context;
       private MediaPlayer player;
       private int widgetID;
       private boolean playing;
       private static Random rnd = new Random();
       private MediaStoppedHandler mediaStoppedHandler;

       private static final int[] SOUND_RESOURCES = {R.raw.risitas1, R.raw.risitas2, R.raw.risitas3, R.raw.risitas4, R.raw.risitas5, R.raw.risitas6, R.raw.risitas7, R.raw.risitas8, R.raw.risitas9, R.raw.risitas10};
       private int[] usedIds;


       public static int[] getSoundResources()
       {
              return SOUND_RESOURCES;
       }

       private void setUsedIds(String[] ids)
       {
              usedIds = new int[ids.length];
              for (int i = 0; i < ids.length; i++)
              {
                     usedIds[i] = Integer.parseInt(ids[i]);
              }
       }

       private int getRandomSoundID()
       {
              if (usedIds.length == 0) return SOUND_RESOURCES[rnd.nextInt(SOUND_RESOURCES.length)];
              return SOUND_RESOURCES[usedIds[rnd.nextInt(usedIds.length)]];
       }

       private void updateWidget(Intent intent, int imageID)
       {
//              Toast.makeText(context, "updateWidget with id: " + widgetID, Toast.LENGTH_SHORT).show();
              int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
              AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
              RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.risitas_laugh_widget);
              remoteViews.setImageViewResource(R.id.risitas_img, imageID);
              appWidgetManager.updateAppWidget(widgetId, remoteViews);
//              Toast.makeText(context, "updateWidget widget updated with id: " + widgetID, Toast.LENGTH_SHORT).show();
       }

       private void mediaStopped()
       {
              playing = false;
              updateWidget(intent, R.drawable.risitas_serious);
              player.release();
              player = null;
              mediaStoppedHandler.mediaStopped(widgetID);
       }

       public int getWidgetID()
       {
              return widgetID;
       }

       @Override
       public void onCompletion(MediaPlayer mp)
       {
              mediaStopped();
       }

       public void playMedia()
       {
              updateWidget(intent, R.drawable.risitas_laugh);
              int soundID = getRandomSoundID();
              player = player.create(context, soundID);
              player.setOnCompletionListener(this);
              player.start();
              playing = true;
       }

       public void stopMedia()
       {
              if (player != null && player.isPlaying())
              {
                     player.stop();
                     mediaStopped();
              }
       }

       public boolean isPlaying()
       {
              return (player!= null && player.isPlaying());
       }


}