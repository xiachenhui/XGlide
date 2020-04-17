package com.xia.xglide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.xia.xglide.glide.ActiveResource;
import com.xia.xglide.glide.interf.BitmapPool;
import com.xia.xglide.glide.interf.K;
import com.xia.xglide.glide.interf.LruBitmapPool;
import com.xia.xglide.glide.LruMemoryCache;
import com.xia.xglide.glide.interf.MemoryCache;
import com.xia.xglide.glide.Resource;
import com.xia.xglide.glide.load.model.HttpUrlLoader;
import com.xia.xglide.glide.load.model.ModelLoader;
import com.xia.xglide.glide.load.model.data.DataFetcher;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements Resource.ResourceListener, MemoryCache.ResourceRemoveListener {
    K k;
    private ActiveResource activeResource;
    private LruMemoryCache lruMemoryCache;
    BitmapPool bitmapPool;
    private ImageView imageView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        new Thread() {
            @Override
            public void run() {
                super.run();
                loadTest();
            }
        }.start();

    }

    private void loadTest() {

        Uri uri = Uri.parse("https://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=%E5%9B%BE%E7%89%87&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&hd=undefined&latest=undefined&copyright=undefined&cs=3173584241,3533290860&os=1133571898,402444249&simid=3493630544,246115770&pn=2&rn=1&di=183590&ln=1664&fr=&fmq=1587137314154_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&hs=2&objurl=http%3A%2F%2Fa0.att.hudong.com%2F78%2F52%2F01200000123847134434529793168.jpg&rpstart=0&rpnum=0&adpicid=0&force=undefined");
        HttpUrlLoader httpUrlLoader = new HttpUrlLoader();
        ModelLoader.LoadData<InputStream> inputStreamLoadData = httpUrlLoader.buildData(uri);
        inputStreamLoadData.fetcher.loadData(new DataFetcher.DataFetcherCallBack<InputStream>() {
            @Override
            public void onFetcherReady(InputStream inputStream) {
                //解析输入流 获取图片
                bitmap = BitmapFactory.decodeStream(inputStream);
                handler.sendEmptyMessage(0);

            }

            @Override
            public void onLoadFail(Exception e) {

            }
        });

    }


    public Resource test(K k) {
        //1 先从活动中拿
        activeResource = new ActiveResource(this);
        bitmapPool = new LruBitmapPool(10);
        lruMemoryCache = new LruMemoryCache(10);
        lruMemoryCache.setResourceRemovedListener(this);
        Resource resource = activeResource.getResource(k);
        if (resource != null) {
            resource.acquired();
            return resource;
        }

        //2 从内存中拿

        resource = lruMemoryCache.get(k);
        if (resource != null) {
            resource.acquired();
            /**
             *  要从内存中移除
             *  Lru可能会移除这个resource  或者也可能recycle掉这个resource
             *  如果不移除，下次使用这个resource的时候可以从活动资源中找到，但是可能被recycle掉了
             **/
            lruMemoryCache.removeResource(k);
            activeResource.active(k, resource);
            return resource;
        }
        return null;
    }

    /**
     * 内存缓存被动移除
     * 需要放入复用池
     *
     * @param resource
     */
    @Override
    public void onResourceRemoved(Resource resource) {
        bitmapPool.put(resource.getBitmap());
    }

    /**
     * 活动缓存
     * 当资源没有正在使用的时候，从活动资源移除到内存缓存
     *
     * @param k
     * @param resource
     */
    @Override
    public void onResourceAcquired(K k, Resource resource) {
        //活动缓存中移除
        activeResource.deActive(k);
        //加入内存缓存
        lruMemoryCache.put(k, resource);

    }
}
