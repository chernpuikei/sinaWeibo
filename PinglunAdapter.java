package chok.chok.test4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
* Created by Kei on 2015/1/6.
*/
public class PinglunAdapter extends BaseAdapter{

    Context context;
    JSONArray jsonArray;
    Bitmap[] bitmaps = new Bitmap[20];
    JSONObject jsonObject;
    int count;

    public  PinglunAdapter(Context context,JSONArray jsonArray,Bitmap[] bitmaps,int count){
        this.context = context;
        this.jsonArray = jsonArray;
        this.bitmaps = bitmaps;
        this.count = count;
        Log.i("完成创建","rt");
    }

    View.OnClickListener onTouXiangClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            JSONObject jsonObject = (JSONObject)v.getTag();
            String string = jsonObject.toString();
            Log.i("weiboadapter", string);
            Intent intent = new Intent(context,user.class);
            intent.putExtra("yonghuxinxi",string);
            context.startActivity(intent);
        }
    };

    class ViewHolder{
        ImageView pinglunyonghudetouxiang;
        TextView pinglunyonghu;
        TextView pinglunshijian;
        TextView pinglunneirong;
    }

    @Override
    public int getCount() {
        return count>20?20:count;
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
            convertView = inflater.inflate(R.layout.pinglun_item,null);//这里曾经inflate成weibo_item
            viewHolder.pinglunyonghudetouxiang = (ImageView) convertView.findViewById(R.id.pinglunyonghudetouxiang);
            viewHolder.pinglunyonghu = (TextView) convertView.findViewById((R.id.pinglunyonghu));
//            viewHolder.pinglunshijian = (TextView) convertView.findViewById(R.id.pinglunshijian);
            viewHolder.pinglunneirong = (TextView) convertView.findViewById(R.id.pinglunneirong);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try{
            if (jsonArray.getJSONObject(position)!=null){
                jsonObject = jsonArray.getJSONObject(position);
                viewHolder.pinglunyonghudetouxiang.setImageBitmap(bitmaps[position]);//然后这里就一直报nullpointer
                viewHolder.pinglunyonghu.setText(jsonObject.getJSONObject("user").getString("name"));
                Log.i("pinglunyonghu",jsonObject.getJSONObject("user").getString("screen_name")+"");
//                viewHolder.pinglunshijian.setText(jiexiriqi(jsonObject.getString("created_at")));
                viewHolder.pinglunneirong.setText(jsonObject.getString("text"));
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





}
