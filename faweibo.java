package chok.chok.test4;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import chok.chok.test4.AccessTokenKeeper;
import chok.chok.test4.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import company.sina.weibo.sdk.openapi.StatusesAPI;
import company.sina.weibo.sdk.openapi.models.ErrorInfo;
import company.sina.weibo.sdk.openapi.models.Status;
import company.sina.weibo.sdk.openapi.models.StatusList;


public class faweibo extends Activity {
    private static final String TAG = faweibo.class.getName();
    Bitmap bitmap;
    Uri uri;
    /** UI 元素：ListView */
    private ListView mFuncListView;
    /** 功能列表 */
    private String[] mFuncList;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faweibo);
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);

        final EditText editText = (EditText) findViewById(R.id.editText);

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mStatusesAPI.updateSync(editText.getText().toString(), "+40.0", "+127.0");
                }
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10086);
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(faweibo.this,uri.toString(),Toast.LENGTH_LONG).show();
                mStatusesAPI.uploadSync(editText.getText().toString(),bitmap, null, null);
            }
        });

        final ImageView imageView = (ImageView)findViewById(R.id.imageView);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,data.getData().toString(),Toast.LENGTH_LONG).show();//note:这里是用getData而不是getExtra
        try{
            uri = data.getData();
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            bitmap = compressImage(bitmap);


            //ImageView imageView=(ImageView)findViewById(R.id.imageView);
            //imageView.setImageBitmap(bitmap);
        }
        catch (IOException ioe){
            Toast.makeText(this,"找不到图片",Toast.LENGTH_LONG).show();
        }


    }

//    private RequestListener mListener = new RequestListener() {
//        @Override
//        public void onComplete(String response) {
//            if (!TextUtils.isEmpty(response)) {
//                LogUtil.i(TAG, response);
//                if (response.startsWith("{\"statuses\"")) {
//                    // 调用 StatusList#parse 解析字符串成微博列表对象
//                    StatusList statuses = StatusList.parse(response);
//                    if (statuses != null && statuses.total_number > 0) {
//                        Toast.makeText(faweibo.this,
//                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                } else if (response.startsWith("{\"created_at\"")) {
//                    // 调用 Status#parse 解析字符串成微博对象
//                    Status status = Status.parse(response);
//                    Toast.makeText(faweibo.this,
//                            "发送一送微博成功, id = " + status.id,
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(faweibo.this, response, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
//            LogUtil.e(TAG, e.getMessage());
//            ErrorInfo info = ErrorInfo.parse(e.getMessage());
//            Toast.makeText(faweibo.this, info.toString(), Toast.LENGTH_LONG).show();
//        }
//    };

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,20, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>200) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


}
