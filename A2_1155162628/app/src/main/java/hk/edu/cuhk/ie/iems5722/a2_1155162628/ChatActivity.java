package hk.edu.cuhk.ie.iems5722.a2_1155162628;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String user_id = "1111111111";
    private String user_name = "Niko";
    private ListView mlistview;
    private List<Msg> mlist;
    private EditText input;
    private Integer total_pages = 1;
    private Integer current_page = 1;
    private Integer chatroom_id;
    private String chatroom_name;
    private boolean isload = false;
    private int Ts;
    protected float beforeY;
    protected float afterY;
    protected int direction;

    private ArrayList<String> array_names = new ArrayList<>();
    private ArrayList<String> array_values = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.refresh:
                mlist.clear();
                String url = "http://34.150.66.89/api/a3/get_messages" + "?chatroom_id=" + chatroom_id + "&page=1";
                new MyAsyncTask().execute(url, "GET");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        chatroom_id = intent.getIntExtra("chatroom_id", 2);
        chatroom_name = intent.getStringExtra("chatroom_name");

        this.setTitle(chatroom_name);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mlistview = findViewById(R.id.msg_list);
        mlist = new ArrayList<>();
        input = (EditText) findViewById(R.id.input);

        MsgAdapter adapter = new MsgAdapter(this, mlist);
        mlistview.setAdapter(adapter);

        String url = "http://34.150.66.89/api/a3/get_messages" + "?chatroom_id=" + chatroom_id + "&page=1";
        ChatActivity.MyAsyncTask task = new ChatActivity.MyAsyncTask();
        task.execute(url,"GET");

        Ts = ViewConfiguration.get(this).getScaledTouchSlop();
        mlistview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        beforeY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        afterY = motionEvent.getY();
                        if (afterY - beforeY > Ts) {
                            direction = 0;
                        } else if (beforeY - afterY > Ts) {
                            direction = 1;
                        }
                        break;
                }
                return false;
            }
        });
        mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (direction == 0 && isload && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (current_page < total_pages) {
                        current_page++;
                        String url = "http://34.150.66.89/api/a3/get_messages" + "?chatroom_id=" + chatroom_id + "&page=" + current_page;
                        new MyAsyncTask().execute(url,"GET");
                    }else{
                        Toast.makeText(ChatActivity.this, "It is the last page.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                isload = (firstVisibleItem == 0);

            }
        });
        findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = input.getText().toString().trim();
                if (s.isEmpty() || s.equals("null")) {
                    Toast.makeText(ChatActivity.this, "Please enter the message", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    array_names.add("chatroom_id");
                    array_names.add("user_id");
                    array_names.add("name");
                    array_names.add("message");
                    array_values.add(chatroom_id.toString());
                    array_values.add(user_id);
                    array_values.add(user_name);
                    array_values.add(s);
                    MyAsyncTask postTask = new MyAsyncTask();
                    postTask.execute("http://34.150.66.89/api/a3/send_message", "POST");
                    input.setText("");
                }

            }

        });
    }


    private String PostHttpRequest(String urlString, ArrayList<String> para_names, ArrayList<String> para_values) {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Uri.Builder builder = new Uri.Builder();

            for (int i = 0; i < para_names.size(); i++) {
                builder.appendQueryParameter(para_names.get(i), para_values.get(i));
            }
            String query = builder.build().getEncodedQuery();

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            String result = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                char[] buffer = new char[1024];
                reader.read(buffer);
                result = new String(buffer);
                JSONObject json = new JSONObject(result);
                String status = json.getString("status");
                return status;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
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

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        private boolean flag;
        MsgAdapter adapter;
        @Override
        protected void onPreExecute() {
            adapter = (MsgAdapter) mlistview.getAdapter();
        }

        @Override
        protected String doInBackground(String[] params) {
            if(params[1].equals("GET") ){
                flag=true;
                String result = GetHttpRequest(params[0]);
                return result;
            }else if (params[1].equals("POST") ){
                flag=false;
                String status = PostHttpRequest(params[0], array_names, array_values);
                return status;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(flag){
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("status");
                    JSONObject data = json.getJSONObject("data");
                    current_page = data.getInt("current_page");
                    total_pages = data.getInt("total_pages");
                    JSONArray array = data.getJSONArray("messages");
                    for (int i=0;i<array.length();i++){
                        mlist.add(0,new Msg(array.getJSONObject(i).getString("message"),array.getJSONObject(i).getString("name"),array.getJSONObject(i).getString("message_time")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }else{
                if(result.equals("OK")){
                    mlist.clear();
                    new MyAsyncTask().execute("http://34.150.66.89/api/a3/get_messages" + "?chatroom_id=" + chatroom_id + "&page=1","GET");
                }else{
                    Toast.makeText(ChatActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
