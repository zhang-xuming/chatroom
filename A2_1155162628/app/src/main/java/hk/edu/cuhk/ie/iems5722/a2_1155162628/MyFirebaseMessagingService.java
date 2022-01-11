package hk.edu.cuhk.ie.iems5722.a2_1155162628;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String POST = "Post TOKEN";

    public void onNewToken(String token){
        super.onNewToken(token);
        Log.d(TAG, "Token:"+token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        JSONObject json = null;

        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", "1155162628")
                .add("token", token)
                .build();
        String url = "http://34.150.66.89/api/a4/submit_push_token";
        OkHttpClient client = new OkHttpClient();
        Log.d(POST, url);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            json = new JSONObject(responseData);
            Log.d(POST, String.valueOf(json.get("status")));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(POST, "POST error!");
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {

            Log.d("chatroom_id", remoteMessage.getNotification().getTag());
            sendNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getTag(),
                    remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String chatroom_name, String chatroom_id, String message) {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("chatroom_id", chatroom_id);
        intent.putExtra("chatroom_name", chatroom_name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cuhk)
                .setContentTitle(chatroom_name)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }
}
