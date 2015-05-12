package chok.chok.test4;

import android.app.ActionBar;
import android.app.Activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import chok.chok.test4.AccessTokenKeeper;
import chok.chok.test4.Constants;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import company.sina.weibo.sdk.openapi.StatusesAPI;
import company.sina.weibo.sdk.openapi.UsersAPI;
import company.sina.weibo.sdk.openapi.models.ErrorInfo;
import company.sina.weibo.sdk.openapi.models.Status;
import company.sina.weibo.sdk.openapi.models.StatusList;

public class duweibo extends ActionBarActivity {
    WeiboAdapter weiboAdapter;
    private static final String TAG = duweibo.class.getName();


    private Oauth2AccessToken mAccessToken;
    private StatusesAPI mStatusesAPI;
    public Handler handler;
    UsersAPI mUsersAPI;
    JSONArray jsonArray;
    Long id;
    JSONObject jsonObject;
    Runnable runnable;
    int tiaoshu = 10;
    HashMap<String, Bitmap> bitmaps = new HashMap<>();
    BitmapDrawable drawable;
    String title;
    Long chosenone;

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duweibo);


        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(duweibo.this, mAuthInfo);

        final ListView listView = (ListView) findViewById(R.id.listView);
        final ListView drawer = (ListView) findViewById(R.id.drawer);
//        listView.setVisibility(View.GONE);


//        final ImageView qidongtu = (ImageView) findViewById(R.id.qidongtu);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);

        getSupportActionBar().hide();
        registerForContextMenu(listView);//一定要绑定

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!AccessTokenKeeper.readAccessToken(duweibo.this).isSessionValid()) {
                    mSsoHandler.authorize(new AuthListener());
                } else {
                    try {
                        jsonArray = new JSONObject(mStatusesAPI.friendsTimelineSync(0L, 0L, tiaoshu, 1, false, 0, false)).getJSONArray("statuses");
                        Log.i("jsonarray", jsonArray + "");
                        title = new JSONObject(mUsersAPI.showSync(Long.parseLong(mAccessToken.getUid()))).getString("screen_name");
                        handler.sendEmptyMessage(10086);
                    } catch (JSONException e) {
                        e.getStackTrace();
                    }
                }

            }
        }).start();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 10086) {

                    weiboAdapter = new WeiboAdapter(duweibo.this, jsonArray, bitmaps, tiaoshu);
                    listView.setDivider(null);
                    listView.setBackgroundColor(0x000000);
                    listView.setAdapter(weiboAdapter);
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle(title);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initBitmaps(jsonArray);
                        }
                    }).start();
                } else if (msg.what == 1024) {
                    weiboAdapter.notifyDataSetChanged();
                }
            }
        };


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        if (listView.getLastVisiblePosition() == weiboAdapter.getCount() - 1) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        tiaoshu += 5;
                                        weiboAdapter.tiaoshu += 5;
                                        weiboAdapter.jsonArray = new JSONObject(mStatusesAPI.friendsTimelineSync(0L, 0L, weiboAdapter.tiaoshu, 1, false, 0, false)).getJSONArray("statuses");
                                        initBitmaps(weiboAdapter.jsonArray);
                                        weiboAdapter.bitmaps = bitmaps;
                                        handler.sendEmptyMessage(1024);
                                    } catch (JSONException e) {
                                        e.getStackTrace();
                                    }
                                }
                            }).start();

                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    chosenone = jsonArray.getJSONObject(position).getLong("id");
                } catch (JSONException e) {
                    e.getStackTrace();
                }
                listView.showContextMenu();//为毛view.showContextMenu()会stackoverflow
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        if (weiboAdapter!=null){
            weiboAdapter.toclickornottoclick = true;
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    protected void initBitmaps(final JSONArray jsonArray) {
        int i = 0;
        while (i < tiaoshu) {
            try {
                String touxiangurl = jsonArray.getJSONObject(i).getJSONObject("user").getString("profile_image_url");
                JSONArray tupianmen = jsonArray.getJSONObject(i).getJSONArray("pic_urls");
                int tupianshumu = tupianmen.length();
                if (!bitmaps.containsKey(touxiangurl)) {
                    Bitmap bitmap = urlstringToBitmap(touxiangurl);
                    bitmaps.put(touxiangurl, bitmap);
                    weiboAdapter.bitmaps = bitmaps;
                    handler.sendEmptyMessage(1024);

                }
                if (tupianshumu > 0) {
                    int i1 = 0;
                    while (i1 < tupianshumu) {
                        String tupianurl = tupianmen.getJSONObject(i1).getString("thumbnail_pic");
                        if (!bitmaps.containsKey(tupianurl)) {
                            Bitmap bitmap = urlstringToBitmap(tupianurl);
                            bitmaps.put(tupianurl, bitmap);
                            weiboAdapter.bitmaps = bitmaps;
                            handler.sendEmptyMessage(1024);
                        }
                        i1++;
                    }
                } else if (jsonArray.getJSONObject(i).has("retweeted_status") && jsonArray.getJSONObject(i).getJSONObject("retweeted_status").getJSONArray("pic_urls").length() > 0) {
                    int i1 = 0;
                    JSONArray beizhuangtu = jsonArray.getJSONObject(i).getJSONObject("retweeted_status").getJSONArray("pic_urls");
                    int beizhuangtushu = beizhuangtu.length();
                    while (i1 < beizhuangtushu) {
                        String tupianurl = beizhuangtu.getJSONObject(i1).getString("thumbnail_pic");
                        if (!bitmaps.containsKey(tupianurl)) {
                            Bitmap bitmap = urlstringToBitmap(tupianurl);
                            bitmaps.put(tupianurl, bitmap);
                            weiboAdapter.bitmaps = bitmaps;
                            handler.sendEmptyMessage(1024);

                        }
                        i1++;
                    }
                }
                i++;
            } catch (JSONException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("删除");
    }

    protected Bitmap urlstringToBitmap(String string) {

        URL url = null;
        Bitmap bitmap = null;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_duweibo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(duweibo.this, xieweibo.class);
                String edf = null;
                intent.putExtra("id", edf);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                new Thread(runnable).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getOrder()) {
            case 0:
                ;
                mStatusesAPI.destroy(chosenone, mListener);
        }
        return true;
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(duweibo.this, mAccessToken);
                Intent intent = new Intent(duweibo.this, duweibo.class);
                startActivity(intent);
                Toast.makeText(duweibo.this,
                        "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(duweibo.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(duweibo.this,
                    "授权取消", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(duweibo.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(duweibo.this,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(duweibo.this,
                            "操作成功",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(duweibo.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(duweibo.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}

