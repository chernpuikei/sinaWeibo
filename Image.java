package chok.chok.test4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Handler;


public class Image extends Activity {
    String string;
    Bitmap bitmap;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        final ImageView datu = (ImageView) findViewById(R.id.datu);

        Intent intent = getIntent();
        string = intent.getStringExtra("weibopic").replace("thumbnail", "large");
        Log.i("string",string);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                datu.setImageBitmap(bitmap);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap =urlstringToBitmap(string);
                handler.sendEmptyMessage(10086);
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        bitmap.recycle();
        super.onDestroy();
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
        getMenuInflater().inflate(R.menu.menu_image, menu);
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
