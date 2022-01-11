package hk.edu.cuhk.ie.iems5722.a2_1155162628;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listview;
    private List<Chatroom> rooms_list;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("IEMS5722");

        rooms_list = new ArrayList<>();

        ChatroomAdapter adapter = new ChatroomAdapter(this, rooms_list);
        listview = findViewById(R.id.chatroom_list);
        listview.setAdapter(adapter);

        flag = isGooglePlayServicesAvailable(MainActivity.this);
        Log.d("isGPSAvailable", String.valueOf(flag));


        new MyAsyncTask().execute("http://34.150.66.89/api/a3/get_chatrooms");


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("chatroom_id", rooms_list.get(i).getId());
                intent.putExtra("chatroom_name", rooms_list.get(i).getName());
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        flag = isGooglePlayServicesAvailable(MainActivity.this);
        Log.d("isGPSAvailable", String.valueOf(flag));

    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        ChatroomAdapter adapter;

        @Override
        protected void onPreExecute() {
            adapter = (ChatroomAdapter) listview.getAdapter();
        }

        @Override
        protected String doInBackground(String[] params) {
            String result = GetHttpRequest(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                String status = json.getString("status");
                JSONArray array = json.getJSONArray("data");
                for (int i=0;i<array.length();i++){
                    rooms_list.add(new Chatroom(array.getJSONObject(i).getString("name"),array.getJSONObject(i).getInt("id")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();

        }
    }
    private String GetHttpRequest(String urlString){
        InputStream inputStream = null;
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();

            String result=null;
            if(responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                Reader reader = new InputStreamReader(inputStream,"UTF-8");
                char[] buffer = new char[1024];
                reader.read(buffer);
                result = new String(buffer);
            }
            return result;

        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}