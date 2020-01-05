package fr.tchatat.gotoesig;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import fr.tchatat.gotoesig.ui.roads.MesTrajetsFragment;
import fr.tchatat.gotoesig.ui.roads.TrajetMap;

/**
 * Created by francesco on 13/09/16.
 */
public class FirebaseMsgService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
        Log.e("refreshedToken", token);
        final String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("/users/"+uid+"/token");
        tokenRef.setValue(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Msg", "Message received ["+remoteMessage+"]");

        // Create Notification
        Intent intent = new Intent(this, MesTrajetsFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Message")
                .setSmallIcon(R.drawable.logo)
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1410, notificationBuilder.build());
    }
}
