package com.example.v_jarj.wgu;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

@SuppressWarnings("deprecation")
public class CourseNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClassName("com.example.v_jarj.wgu", CoursesListActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent;
        if (intent.getStringExtra("Type").equals("starting")) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("WGU Course Reminder")
                .setContentText("You have a course " + intent.getStringExtra("Type") +
                        " today: " + intent.getStringExtra("Title"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (intent.getStringExtra("Type").equals("starting")) {
            NotificationManagerCompat.from(context).notify(0, mBuilder.build());
        } else {
            NotificationManagerCompat.from(context).notify(1, mBuilder.build());
        }
    }
}
