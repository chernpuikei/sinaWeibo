package chok.chok.test4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Handler;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import company.sina.weibo.sdk.openapi.CommentsAPI;
import company.sina.weibo.sdk.openapi.StatusesAPI;
import company.sina.weibo.sdk.openapi.models.CommentList;


public class weiboxiangqing extends Activity {

    private static final String TAG = duweibo.class.getName();


    private Oauth2AccessToken mAccessToken;
    private CommentsAPI mCommentsAPI;

    View[] viewHolders = new View[9];
    JSONObject jsonObject;
    Handler handler;
    Bitmap[] bitmaps = new Bitmap[10];
    Bitmap[] pinglunbitmaps = new Bitmap[20];
    PinglunAdapter pinglunAdapter;
    Long thechosenone;
    JSONArray pinglundejarei;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weiboxiangqing);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);

        final ViewGroup weiboweibo =(ViewGroup) findViewById(R.id.weiboweibo);

        Intent intent = getIntent();
        String string = intent.getStringExtra("weiboobject");

        LayoutInflater inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weibo_item,weiboweibo);
        TextView name = (TextView) findViewById(R.id.name);
        TextView date = (TextView) findViewById(R.id.date);
        TextView source = (TextView) findViewById(R.id.source);
        TextView text = (TextView) findViewById(R.id.text);
        TextView zhuanfa = (TextView) findViewById(R.id.zhuanfa);
        TextView pinglun = (TextView) findViewById(R.id.pinglun);
        TextView zan = (TextView) findViewById(R.id.zan);
        final ListView listView2 = (ListView) findViewById(R.id.listView2);
        final ImageView touxiang = (ImageView) findViewById(R.id.touxiang);
        final ImageView g11 = (ImageView) findViewById(R.id.imageView11);
        final ImageView g12 = (ImageView) findViewById(R.id.imageView12);
        final ImageView g13 = (ImageView) findViewById(R.id.imageView13);
        final ImageView g21 = (ImageView) findViewById(R.id.imageView21);
        final ImageView g22 = (ImageView) findViewById(R.id.imageView22);
        final ImageView g23 = (ImageView) findViewById(R.id.imageView23);
        final ImageView g31 = (ImageView) findViewById(R.id.imageView31);
        final ImageView g32 = (ImageView) findViewById(R.id.imageView32);
        final ImageView g33 = (ImageView) findViewById(R.id.imageView33);
        TextView retweet = (TextView) findViewById(R.id.retweet);

        registerForContextMenu(listView2);
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    thechosenone = pinglundejarei.getJSONObject(position).getLong("id");
                    Log.i("pinglundejarei",pinglundejarei.toString());
                    Log.i("pinglundejarei",pinglundejarei.getJSONObject(position).getLong("id")+"");
                    listView2.showContextMenu();
                }catch (JSONException e){
                    e.getStackTrace();
                }
                return true;
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==10086){
                    Log.i("收到信息",msg.what+"");
                    g11.setImageBitmap(bitmaps[0]);
                    g12.setImageBitmap(bitmaps[1]);
                    g13.setImageBitmap(bitmaps[2]);
                    g21.setImageBitmap(bitmaps[3]);
                    g22.setImageBitmap(bitmaps[4]);
                    g23.setImageBitmap(bitmaps[5]);
                    g31.setImageBitmap(bitmaps[6]);
                    g32.setImageBitmap(bitmaps[7]);
                    g33.setImageBitmap(bitmaps[8]);
                    touxiang.setImageBitmap(bitmaps[9]);
                } else if (msg.what == 1024){
                    Log.i("收到信息",msg.what+"");
                    View view = new View(weiboxiangqing.this);
                    listView2.setDivider(null);
                    listView2.setAdapter(pinglunAdapter);
                }

            }
        };

        viewHolders[0]=g11;
        viewHolders[1]=g12;
        viewHolders[2]=g13;
        viewHolders[3]=g21;
        viewHolders[4]=g22;
        viewHolders[5]=g23;
        viewHolders[6]=g31;
        viewHolders[7]=g32;
        viewHolders[8]=g33;              //因为这块东西

        try{
            jsonObject = new JSONObject(string);
            date.setText(jiexiriqi(jsonObject.getString("created_at")));
            source.setText(jiexilaiyuan(jsonObject.getString("source")));

            text.setText(jsonObject.getString("text"));

            zhuanfa.setText("转发("+jsonObject.getString("reposts_count")+")");
            pinglun.setText("评论("+jsonObject.getString("comments_count")+")");
            zan.setText("赞(" + jsonObject.getString("attitudes_count") + ")");

            name.setText(jsonObject.getJSONObject("user").getString("screen_name"));
            name.setTag(jsonObject.getJSONObject("user"));
            name.setOnClickListener(onTouXiangClickListener);

            touxiang.setTag(jsonObject.getJSONObject("user"));
            touxiang.setOnClickListener(onTouXiangClickListener);

            final int a = jsonObject.getJSONArray("pic_urls").length();
            if(a!=0) {

                retweet.setVisibility(View.GONE);


                int c = 8;
                while(c>-1){
                    if (c>=a){
                        viewHolders[c].setVisibility(View.GONE);
                    } else {
                        viewHolders[c].setVisibility(View.VISIBLE);
                    }
                    c--;
                }
                //有配图,无转发
            } else if (jsonObject.has("retweeted_status")){
                JSONObject retweet1 = jsonObject.getJSONObject("retweeted_status");
                retweet.setText(retweet1.getJSONObject("user").getString("screen_name") + ":" + retweet1.getString("text"));
                final int b = retweet1.getJSONArray("pic_urls").length();
//                bs[position]= b;
                if(b!=0) {

                    retweet.setVisibility(View.VISIBLE);


                    int c = 8;
                    while(c>-1){
                        if (c>=b){
                            viewHolders[c].setVisibility(View.GONE);
                        } else {
                            viewHolders[c].setVisibility(View.VISIBLE);
                        }
                        c--;
                    }

                } else {

                    g33.setVisibility(View.GONE);
                    g32.setVisibility(View.GONE);
                    g31.setVisibility(View.GONE);
                    g23.setVisibility(View.GONE);
                    g22.setVisibility(View.GONE);
                    g21.setVisibility(View.GONE);
                    g13.setVisibility(View.GONE);
                    g12.setVisibility(View.GONE);
                    g11.setVisibility(View.GONE);
                    retweet.setVisibility(View.VISIBLE); //有转发,无转发配图

                }
            }  else {

                g33.setVisibility(View.GONE);
                g32.setVisibility(View.GONE);
                g31.setVisibility(View.GONE);
                g23.setVisibility(View.GONE);
                g22.setVisibility(View.GONE);
                g21.setVisibility(View.GONE);
                g13.setVisibility(View.GONE);
                g12.setVisibility(View.GONE);
                g11.setVisibility(View.GONE);
                retweet.setVisibility(View.GONE); //无转发,无配图
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    initBitmaps();
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Long id = jsonObject.getLong("id");
                        String string = mCommentsAPI.showSync(id,0L,0L,20,1,0);
                        JSONObject pinglundejoj = new JSONObject(string);
                        pinglundejarei = pinglundejoj.getJSONArray("comments");
                        int bcd=0;
                        while (bcd<pinglundejarei.length()){
                            JSONObject dantiao = pinglundejarei.getJSONObject(bcd);
                            Log.i("单条评论string",dantiao+"");
                            Bitmap bitmap = urlstringToBitmap(dantiao.getJSONObject("user").getString("profile_image_url"));

                            pinglunbitmaps[bcd] = bitmap;
                            bcd++;
                        }
                        Log.i("这是一个小测试",bcd+"");//评论不足10条去不到这里,过10条crush
                        int count = pinglundejarei.length();
                        pinglunAdapter = new PinglunAdapter(weiboxiangqing.this,pinglundejarei,pinglunbitmaps,count);
                        handler.sendEmptyMessage(1024);

                    } catch (JSONException e){
                        e.getStackTrace();
                    }

                }
            }).start();


        } catch (JSONException e){
            e.getStackTrace();
        }



    }

    View.OnClickListener onTouXiangClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            JSONObject jsonObject = (JSONObject)v.getTag();
            String string = jsonObject.toString();
            Intent intent = new Intent(weiboxiangqing.this,user.class);
            intent.putExtra("yonghuxinxi",string);
            startActivity(intent);
        }
    };

    protected void initBitmaps(){
        new Thread(new Runnable() {//获取最新10条微博的配图以及用户头像存放到bitmaps中
            @Override
            public void run() {
                try{

                    Bitmap touxiang = urlstringToBitmap(jsonObject.getJSONObject("user").getString("profile_image_url"));
                    bitmaps[9] = touxiang;
                        Log.i("显示头像",touxiang+"");
//                        handler.sendEmptyMessage(13800);
                    int shumu = jsonObject.getJSONArray("pic_urls").length();//微博图片数目
                        Log.i("shumu",shumu+"");
                    JSONArray weibotupian = jsonObject.getJSONArray("pic_urls");//微博图片数组
                        Log.i("微博图片数组",weibotupian+"");
                    if (shumu!=0) {
                        int j = 0;
                        while (j < shumu) {
//                                Log.i("显示微博图片中的j",j+"");
                            Bitmap bitmap = urlstringToBitmap(weibotupian.getJSONObject(j).getString("thumbnail_pic"));
//                                Log.i("显示bitmap",bitmap.toString());
                            bitmaps[j] = bitmap;
//                                handler.sendEmptyMessage(13800);
                            j++;
                        }
                    } else if (jsonObject.has("retweeted_status")) {
                        int b = jsonObject.getJSONObject("retweeted_status").getJSONArray("pic_urls").length();//被转发微博图片数目
                        JSONArray banana = jsonObject.getJSONObject("retweeted_status").getJSONArray("pic_urls");//被转发微博图片数组
//                            Log.i("转发图片数组",banana+"");
                        if (b!=0){
                            int j = 0;
                            while (j < b) {
//                                    Log.i("显示转发图片中的j",j+"");
                                Bitmap bitmap = urlstringToBitmap(banana.getJSONObject(j).getString("thumbnail_pic"));
                                bitmaps[j] = bitmap;
//                                    handler.sendEmptyMessage(13800);
                                j++;
                            }
                        }
                    }
                }catch (JSONException e){
                    e.getStackTrace();
                }
                handler.sendEmptyMessage(10086);
            }
        }).start(); //为什么join不行
//        return bitmaps;
// 多此一举,将return的数组赋geibitmaps,但在构造数组的过程中已经建立起bitmaps了,有空思考一下返回类型,但一个方法是异步的时候-开启线程网络访问,他的返回类型貌似可以是空-比如上面,但是在方法执行过程中已经将一个数组填满了,这时需要设置一个成员变量去存放数据
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

    public String jiexiriqi(String string){
        String[] strings= string.split(" ");
        return strings[3];
    }

    public String jiexilaiyuan(String string){
        return("   来自:"+string.split(">")[1].split("<")[0]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weiboxiangqing, menu);
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

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            LogUtil.i(TAG, response);
            if (!TextUtils.isEmpty(response)) {
                CommentList comments = CommentList.parse(response);
                if(comments != null && comments.total_number > 0){
                    Toast.makeText(weiboxiangqing.this,
                            "获取评论成功, 条数: " + comments.commentList.size(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("回复");
        menu.add("删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getOrder()){
            case 0:
                return true;
            case 1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mCommentsAPI.destroySync(thechosenone);
                    }
                }).start();
        }
        return true;

    }
}
