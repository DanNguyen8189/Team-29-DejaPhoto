package layout;

import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.team29.cse110.team29dejaphoto.DejaPhoto;
import com.team29.cse110.team29dejaphoto.R;

/**
 * Implementation of App Widget functionality.
 */
public class WIDGET_DejaPhoto extends AppWidgetProvider {

    private final String TAG = "WIDGET_DejaPhoto"; // Used for logging

    public static String SWIPE_RIGHT = "swipe right"; // Variable indicating a right swipe action
    public static String SWIPE_LEFT = "swipe left"; // Variable indicating a left swipe action

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId);

            // Set the onClickPendingIntent for the clickable view in each widget
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget__deja_photo);
            remoteView.setOnClickPendingIntent(R.id.rightArrow, getPendingSelfIntent(context, SWIPE_LEFT));
            remoteView.setOnClickPendingIntent(R.id.leftArrow, getPendingSelfIntent(context, SWIPE_RIGHT));

            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }
    }

    // Set the action to the intent, and return a PendingIntent using getBroadcast
    protected PendingIntent getPendingSelfIntent(Context context, String action){
        Intent intent = new Intent(context, WIDGET_DejaPhoto.class);
        intent.setAction(action);

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            //WidgetDejaPhotoConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        // Left Arrow was clicked - perform onClick action
        if (intent.getAction().equals(SWIPE_RIGHT)){
            Toast.makeText(context, "Setting Prev Wallpaper", Toast.LENGTH_SHORT).show();

            Intent serviceIntent = new Intent();
            serviceIntent.setAction("PREV_BUTTON");
            context.sendBroadcast(serviceIntent);
        }

        // Right Arrow was clicked - perform onClick action
        else  if (intent.getAction().equals(SWIPE_LEFT)) {
            Toast.makeText(context, "Setting Next Wallpaper", Toast.LENGTH_SHORT).show();

            Intent serviceIntent = new Intent();
            serviceIntent.setAction("NEXT_BUTTON");
            context.sendBroadcast(serviceIntent);

        }
    }
}


