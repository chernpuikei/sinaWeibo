package chok.chok.test4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Handler;

import java.util.logging.LogRecord;


public class user extends Activity {

    Bitmap datouxiang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        final ImageView touxiang = (ImageView)findViewById(R.id.usertouxiang);
        TextView nickname = (TextView)findViewById(R.id.usernickname);
        TextView userjianjie = (TextView)findViewById(R.id.userjianjie);
        TextView guanzhu = (TextView)findViewById(R.id.guanzhu);
        TextView fensi = (TextView)findViewById(R.id.fensi);
        TextView weiboshumu = (TextView)findViewById(R.id.weiboshumu);
        TextView diqu = (TextView)findViewById(R.id.diqu);



        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                touxiang.setImageBitmap(datouxiang);
            }
        };



        Intent intent = getIntent();
        String string = intent.getStringExtra("yonghuxinxi");
        try{
            final JSONObject jsonObject = new JSONObject(string);
            Log.i("jsonobject", jsonObject.toString());
            nickname.setText(jsonObject.getString("screen_name"));
            Log.i("uid",jsonObject.getLong("id")+"");
            userjianjie.setText(jsonObject.getString("description"));
            guanzhu.setText("关注数:"+jsonObject.getInt("friends_count"));
            fensi.setText("粉丝数:"+jsonObject.getInt("followers_count"));
            weiboshumu.setText("微博数目:"+jsonObject.getInt("statuses_count"));
            diqu.setText(jsonObject.getString("location"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String datu = jsonObject.getString("avatar_large");
                        datouxiang = urlstringToBitmap(datu);
                        handler.sendEmptyMessage(10086);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                }
            }).start();
//            touxiang.setImageBitmap(urlstringToBitmap());
        } catch (JSONException e) {
            e.getStackTrace();
        }

    }

    protected Bitmap urlstringToBitmap(String string){

        URL url = null;
        Bitmap bitmap = null;
        try{
            url = new URL(string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try{
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return bitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
