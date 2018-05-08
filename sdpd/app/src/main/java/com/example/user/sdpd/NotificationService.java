package com.example.user.sdpd;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationService extends Service {
    public NotificationService() {
    }

    private static final String TAG = "Servicenotif";
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    ArrayList<String> servicelist = new ArrayList<>();
    ArrayList<Long> t = new ArrayList<>();
    String muid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.d(TAG, "Service Started");
        //Toast.makeText(this, "Hi there!", Toast.LENGTH_SHORT).show();
        //generateNotification("Hey!");

        mDatabaseReference.child("Events").child(muid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Toast.makeText(notifications.this, dataSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                    //TheEvents ev = dataSnapshot.getValue(TheEvents.class);

                    //Toast.makeText(notifications.this, ss, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(NotificationService.this, "Hi there2!", Toast.LENGTH_SHORT).show();
                    Map<String, String> nMap = new HashMap<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        nMap.put(snapshot.getKey(), snapshot.getValue().toString());
                        //Toast.makeText(NotificationService.this, "Hi there3!", Toast.LENGTH_SHORT).show();
                    }
                    generateNotification(nMap.get("name"));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        /*
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.d(e.getMessage(),e.toString());
        }*/

    }
    private void generateNotification(String name) {
        try {
            Intent intent = new Intent(NotificationService.this, notifications.class);
            //intent.putExtra("name", name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("New event added, check out!")
                            .setContentText("Event Name: " + name)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
        catch(Exception e){
            Log.d("NotifServ", e.toString());
        }
    }


}
