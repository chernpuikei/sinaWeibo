package chok.chok.test4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Created by Kei on 2014/12/31.
*/
public class WeiboAdapter extends BaseAdapter {

    JSONArray jsonArray;
    Context context;
    JSONObject jsonObject;
    View[] viewHolders = new View[9];
    int tiaoshu;
    HashMap<String,Bitmap> bitmaps;
    Boolean toclickornottoclick = true;
    Boolean anothertoclickornottoclick = true;
    int start;
    int end;


    public  WeiboAdapter(Context context,JSONArray jsonArray,HashMap<String,Bitmap> bitmaps,int tiaoshu){
        this.jsonArray = jsonArray;
        this.context = context;
        this.bitmaps = bitmaps;
        this.tiaoshu = tiaoshu;
    }

    View.OnClickListener onTouXiangClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            JSONObject jsonObject = (JSONObject)v.getTag();
            String string = jsonObject.toString();
            Intent intent = new Intent(context,user.class);
            intent.putExtra("yonghuxinxi",string);
            context.startActivity(intent);
        }
    };

    View.OnClickListener onTextClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (toclickornottoclick) {
                JSONObject jsonObject = (JSONObject) v.getTag();
                String string = jsonObject.toString();
                Intent intent = new Intent(context,weiboxiangqing.class);
                intent.putExtra("weiboobject",string);
                context.startActivity(intent);
            }
        }
    };

    View.OnClickListener onImageClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String string = (String) v.getTag();
            Intent intent = new Intent(context,Image.class);
            intent.putExtra("weibopic",string);
            context.startActivity(intent);
        }
    };



    View.OnClickListener onPinglunListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String aLong = v.getTag()+"";
            Intent intent = new Intent(context,xieweibo.class);
            intent.putExtra("id",aLong);
            context.startActivity(intent);
        }
    };

    View.OnClickListener onZhuanfaListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String joj = v.getTag()+"";
            Intent intent = new Intent(context,xieweibo.class);
            intent.putExtra("joj",joj);
            intent.putExtra("id","zhuanfa");
            context.startActivity(intent);
        }
    };

    class ViewHolder{
        TextView name;
        TextView date;
        TextView source;
        TextView text;
        TextView zhuanfa;
        TextView pinglun;
        TextView zan;
        ImageView touxiang;
        ImageView g11;
        ImageView g12;
        ImageView g13;
        ImageView g21;
        ImageView g22;
        ImageView g23;
        ImageView g31;
        ImageView g32;
        ImageView g33;
        TextView retweet;
    }





    @Override
    public int getCount() {
        return tiaoshu;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        if (convertView==null){
            convertView = inflater.inflate(R.layout.weibo_item,null);
            viewHolder.name = (TextView)convertView.findViewById(R.id.name);
            viewHolder.date = (TextView)convertView.findViewById(R.id.date);
            viewHolder.source = (TextView)convertView.findViewById(R.id.source);
            viewHolder.text = (TextView)convertView.findViewById(R.id.text);
            viewHolder.zhuanfa = (TextView)convertView.findViewById(R.id.zhuanfa);
            viewHolder.pinglun = (TextView)convertView.findViewById(R.id.pinglun);
            viewHolder.zan = (TextView)convertView.findViewById(R.id.zan);
            viewHolder.touxiang = (ImageView)convertView.findViewById(R.id.touxiang);
            viewHolder.g11 = (ImageView)convertView.findViewById(R.id.imageView11);
            viewHolder.g12 = (ImageView)convertView.findViewById(R.id.imageView12);
            viewHolder.g13 = (ImageView)convertView.findViewById(R.id.imageView13);
            viewHolder.g21 = (ImageView)convertView.findViewById(R.id.imageView21);
            viewHolder.g22 = (ImageView)convertView.findViewById(R.id.imageView22);
            viewHolder.g23 = (ImageView)convertView.findViewById(R.id.imageView23);
            viewHolder.g31 = (ImageView)convertView.findViewById(R.id.imageView31);
            viewHolder.g32 = (ImageView)convertView.findViewById(R.id.imageView32);
            viewHolder.g33 = (ImageView)convertView.findViewById(R.id.imageView33);
            viewHolder.retweet = (TextView)convertView.findViewById(R.id.retweet);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolders[0]=viewHolder.g11;
        viewHolders[1]=viewHolder.g12;
        viewHolders[2]=viewHolder.g13;
        viewHolders[3]=viewHolder.g21;
        viewHolders[4]=viewHolder.g22;
        viewHolders[5]=viewHolder.g23;
        viewHolders[6]=viewHolder.g31;
        viewHolders[7]=viewHolder.g32;
        viewHolders[8]=viewHolder.g33;              //因为这块东西



        try{
            jsonObject = jsonArray.getJSONObject(position);

            viewHolder.date.setText(jiexiriqi(jsonObject.getString("created_at")));
            viewHolder.source.setText(jiexilaiyuan(jsonObject.getString("source")));

            final String text = jsonObject.getString("text");
            final SpannableString weiboneirong = new SpannableString(text);
            final Matcher matcher = Pattern.compile("http.*?(\\s|$)").matcher(text);
            while (matcher.find()){
                final String substring = text.substring(matcher.start(),matcher.end());
                weiboneirong.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        toclickornottoclick = false;
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(substring);
                        intent.setData(uri);
                        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                        context.startActivity(intent);
                    }
                },matcher.start(),matcher.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            Matcher yonghumatcher = Pattern.compile("@\\w+(\\s|$)").matcher(text);
            while (yonghumatcher.find()){
                weiboneirong.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        try {
                            JSONObject nidongde = jsonObject.getJSONObject("user");
                            Intent intent = new Intent(context,user.class);
                            intent.putExtra("yonghuxinxi",nidongde.toString());
                            context.startActivity(intent);
                            toclickornottoclick = false;
                        } catch (JSONException e) {
                            e.getStackTrace();
                        }
                    }
                },yonghumatcher.start(),yonghumatcher.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }



            viewHolder.text.setText(weiboneirong);
            viewHolder.text.setTag(jsonObject);
            viewHolder.text.setMovementMethod(LinkMovementMethod.getInstance());

            viewHolder.text.setOnClickListener(onTextClickListener);

            viewHolder.zhuanfa.setText("转发("+jsonObject.getString("reposts_count")+")");
            viewHolder.zhuanfa.setTag(jsonObject);
            viewHolder.zhuanfa.setOnClickListener(onZhuanfaListener);


            viewHolder.pinglun.setText("评论("+jsonObject.getString("comments_count")+")");
            viewHolder.pinglun.setTag(jsonObject.getLong("id"));
            viewHolder.pinglun.setOnClickListener(onPinglunListener);

            viewHolder.zan.setText("赞(" + jsonObject.getString("attitudes_count") + ")");

            viewHolder.name.setText(jsonObject.getJSONObject("user").getString("screen_name"));
            viewHolder.name.setTag(jsonObject.getJSONObject("user"));
            viewHolder.name.setOnClickListener(onTouXiangClickListener);

            String touxiangtupian = jsonArray.getJSONObject(position).getJSONObject("user").getString("profile_image_url");
            viewHolder.touxiang.setImageBitmap(bitmaps.get(touxiangtupian));
            viewHolder.touxiang.setTag(jsonObject.getJSONObject("user"));
            viewHolder.touxiang.setOnClickListener(onTouXiangClickListener);





            final int a = jsonObject.getJSONArray("pic_urls").length();
            if(a!=0) {

                viewHolder.retweet.setVisibility(View.GONE);


                int c = 8;
                while(c>-1){
                    if (c>=a){
                        viewHolders[c].setVisibility(View.GONE);
                    } else {
                        viewHolders[c].setVisibility(View.VISIBLE);
                        viewHolders[c].setTag(jsonObject.getJSONArray("pic_urls").getJSONObject(c).getString("thumbnail_pic"));
                        viewHolders[c].setOnClickListener(onImageClickListener);
                    }
                    c--;
                }
                JSONArray tupianmen = jsonArray.getJSONObject(position).getJSONArray("pic_urls");
                viewHolder.g11.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(0).getString("thumbnail_pic")));
                viewHolder.g12.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(1).getString("thumbnail_pic")));
                viewHolder.g13.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(2).getString("thumbnail_pic")));
                viewHolder.g21.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(3).getString("thumbnail_pic")));
                viewHolder.g22.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(4).getString("thumbnail_pic")));
                viewHolder.g23.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(5).getString("thumbnail_pic")));
                viewHolder.g31.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(6).getString("thumbnail_pic")));
                viewHolder.g32.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(7).getString("thumbnail_pic")));
                viewHolder.g33.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(8).getString("thumbnail_pic")));
                                                                             //有配图,无转发
            } else if (jsonObject.has("retweeted_status")){
                JSONObject retweet = jsonObject.getJSONObject("retweeted_status");

                SpannableString beizhuanfayonghu = new SpannableString(retweet.getJSONObject("user").getString("screen_name")+":" + retweet.getString("text"));
                String huanchong = retweet.getJSONObject("user").getString("screen_name")+":" + retweet.getString("text");
                final JSONObject nidongde = retweet.getJSONObject("user");
                beizhuanfayonghu.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(context,user.class);
                        intent.putExtra("yonghuxinxi",nidongde.toString());
                        context.startActivity(intent);
                        toclickornottoclick = false;
                    }
                },0,retweet.getJSONObject("user").getString("screen_name").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                viewHolder.retweet.setText(beizhuanfayonghu);//删到剩下spannablestring的时候可用
                viewHolder.retweet.setMovementMethod(LinkMovementMethod.getInstance());

                viewHolder.retweet.setTag(retweet);
                viewHolder.retweet.setOnClickListener(onTextClickListener);
                final int b = retweet.getJSONArray("pic_urls").length();
                if(b!=0) {

                    viewHolder.retweet.setVisibility(View.VISIBLE);

                    int c = 8;
                    while(c>-1){
                        if (c>=b){
                            viewHolders[c].setVisibility(View.GONE);
                        } else {
                            viewHolders[c].setVisibility(View.VISIBLE);
                            viewHolders[c].setTag(jsonObject.getJSONObject("retweeted_status").getJSONArray("pic_urls").getJSONObject(c).getString("thumbnail_pic"));
                            viewHolders[c].setOnClickListener(onImageClickListener);

                        }
                        c--;
                    }

                    JSONArray tupianmen = jsonArray.getJSONObject(position).getJSONObject("retweeted_status").getJSONArray("pic_urls");
                    viewHolder.g11.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(0).getString("thumbnail_pic")));
                    viewHolder.g12.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(1).getString("thumbnail_pic")));
                    viewHolder.g13.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(2).getString("thumbnail_pic")));
                    viewHolder.g21.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(3).getString("thumbnail_pic")));
                    viewHolder.g22.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(4).getString("thumbnail_pic")));
                    viewHolder.g23.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(5).getString("thumbnail_pic")));
                    viewHolder.g31.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(6).getString("thumbnail_pic")));
                    viewHolder.g32.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(7).getString("thumbnail_pic")));
                    viewHolder.g33.setImageBitmap(bitmaps.get(tupianmen.getJSONObject(8).getString("thumbnail_pic")));

                } else {

                        viewHolder.g33.setVisibility(View.GONE);
                        viewHolder.g32.setVisibility(View.GONE);
                        viewHolder.g31.setVisibility(View.GONE);
                        viewHolder.g23.setVisibility(View.GONE);
                        viewHolder.g22.setVisibility(View.GONE);
                        viewHolder.g21.setVisibility(View.GONE);
                        viewHolder.g13.setVisibility(View.GONE);
                        viewHolder.g12.setVisibility(View.GONE);
                        viewHolder.g11.setVisibility(View.GONE);
                        viewHolder.retweet.setVisibility(View.VISIBLE); //有转发,无转发配图

                }
            }  else {

                viewHolder.g33.setVisibility(View.GONE);
                viewHolder.g32.setVisibility(View.GONE);
                viewHolder.g31.setVisibility(View.GONE);
                viewHolder.g23.setVisibility(View.GONE);
                viewHolder.g22.setVisibility(View.GONE);
                viewHolder.g21.setVisibility(View.GONE);
                viewHolder.g13.setVisibility(View.GONE);
                viewHolder.g12.setVisibility(View.GONE);
                viewHolder.g11.setVisibility(View.GONE);
                viewHolder.retweet.setVisibility(View.GONE); //无转发,无配图
            }




        } catch (JSONException e){
            e.getStackTrace();
        }
        return convertView;
    }

    public String jiexiriqi(String string){
        String[] strings= string.split(" ");
        return strings[3];
    }

    public String jiexilaiyuan(String string){
        return("   来自:"+string.split(">")[1].split("<")[0]);
    }

//    protected Bitmap urlstringToBitmap(String string){
//
//        URL url = null;
//        Bitmap bitmap = null;
//
////        if (map.containsKey(string)){
////            Log.i("map","123");
////            return (map.get(string));
////        } else {
//            try{
//                url = new URL(string);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//            try{
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoInput(true);
//                urlConnection.connect();
//                InputStream inputStream = urlConnection.getInputStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
////                map.put(string,bitmap);
//                inputStream.close();
//            } catch (IOException e) {
//                e.getStackTrace();
//            }
//            return bitmap;
//
//
//    }

//    protected Bitmap getBitmap(String string){
//        return (map.get(string));
//    }
//
//    protected void putBitmap (String string){
//        URL url = null;
//        Bitmap bitmap = null;
//        try{
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setDoInput(true);
//            urlConnection.connect();
//            InputStream inputStream = urlConnection.getInputStream();
//            bitmap = BitmapFactory.decodeStream(inputStream);
//            map.put(string,bitmap);
//            inputStream.close();
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//    }

}




