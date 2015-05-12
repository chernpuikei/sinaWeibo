package chok.chok.test4;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import org.apache.http.HttpConnection;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import company.sina.weibo.sdk.openapi.CommentsAPI;
import company.sina.weibo.sdk.openapi.StatusesAPI;
import company.sina.weibo.sdk.openapi.models.ErrorInfo;
import company.sina.weibo.sdk.openapi.models.Status;
import company.sina.weibo.sdk.openapi.models.StatusList;


public class xieweibo extends ActionBarActivity {
    private static final String TAG = xieweibo.class.getName();


    private Oauth2AccessToken mAccessToken;
    private CommentsAPI mCommentsAPI;
    private StatusesAPI mStatusesAPI;
    EditText xie;
    JSONObject jsonObject;
    String string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xieweibo);
        string = getIntent().getStringExtra("id");
        xie = (EditText) findViewById(R.id.editText2);
        if (getIntent().getStringExtra("id")!=null&&getIntent().getStringExtra("id").equals("zhuanfa")){
            try{
                jsonObject = new JSONObject(getIntent().getStringExtra("joj"));
                xie.setText(jsonObject.getString("text"));
            }catch (JSONException e){
                e.getStackTrace();
            }

        }

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xieweibo, menu);
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
        } else if (id == R.id.action_send_now) {
            if (string != null){
                if (string.equals("zhuanfa")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                mStatusesAPI.repost(jsonObject.getLong("id"),xie.getText().toString(),0,mListener);
                            }catch(JSONException e){
                                e.getStackTrace();
                            }
//                            try{
//                                URL url = new URL("https://api.weibo.com/2/statuses/repost.json");
//                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                                urlConnection.setDoInput(true);
//                                urlConnection.setDoOutput(true);
//                                urlConnection.setUseCaches(false);
//
//                                urlConnection.setRequestMethod("POST");
//                                urlConnection.setRequestProperty("Content-type","application/json");
//
//                                urlConnection.connect();
//                                OutputStream outputStream = urlConnection.getOutputStream();
//                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//                                String string111 = "{\"access_token\":\""+mAccessToken.toString().split(",")[1].split(":")[1].trim()+"\",\"id\":"+jsonObject.getLong("id")+",\"status\":\""+URLEncoder.encode(xie.getText().toString(),"UTF-8")+"\"}";
//                                Log.i("ceshi",string111);
////                                bufferedWriter.append(string111);
////                                objectOutputStream.writeObject(new JSONObject(string111));
//                                outputStream.write(string111.getBytes());
////                                outputStream.flush();
//                                objectOutputStream.close();
//                                outputStream.close();
//                                urlConnection.disconnect();
//
//                            }catch (MalformedURLException e) {
//                                e.getStackTrace();
//                            }catch (IOException i){
//                                i.getStackTrace();
//                            }catch (JSONException j){
//                                j.getStackTrace();
//                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Long abc = Long.parseLong(string);
                            mCommentsAPI.createSync(xie.getText().toString(),abc,false);
                        }
                    }).start();
                }

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusesAPI.updateSync(xie.getText().toString(),null,null);
                    }
                }).start();
            }
        }

        return super.onOptionsItemSelected(item);
    }

//    private String getQuery(List<NameValuePair> params)
//    {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        for (NameValuePair pair : params)
//        {
//            if (first)
//                first = false;
//            else
//                result.append("&");
//try{
//            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));}catch (UnsupportedEncodingException e){
//    e.getStackTrace();
//            }
//        }
//
//        return result.toString();
//    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(xieweibo.this,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(xieweibo.this,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(xieweibo.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(xieweibo.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
