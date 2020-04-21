package com.xia.xglide;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.xia.xglide.glide.XGlide;
import com.xia.xglide.glide.cache.ActiveResource;
import com.xia.xglide.glide.interf.BitmapPool;
import com.xia.xglide.glide.interf.Key;
import com.xia.xglide.glide.interf.LruBitmapPool;
import com.xia.xglide.glide.cache.LruMemoryCache;
import com.xia.xglide.glide.interf.MemoryCache;
import com.xia.xglide.glide.cache.Resource;

/**
 * @ author xia chen hui
 * @ date 2020/4/19 22:24
 * @ desc : 主页面
 */
public class MainActivity extends AppCompatActivity implements Resource.ResourceListener, MemoryCache.ResourceRemoveListener {
    Key key;
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
        XGlide.with(this).asBitmap().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1587491834483&di=a532d82db7032c1a9877e1d58bf76a4b&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg").into(imageView);
    }




    public Resource test(Key key) {
        //1 先从活动中拿
        activeResource = new ActiveResource(this);
        bitmapPool = new LruBitmapPool(10);
        lruMemoryCache = new LruMemoryCache(10);
        lruMemoryCache.setResourceRemovedListener(this);
        Resource resource = activeResource.getResource(key);
        if (resource != null) {
            resource.acquired();
            return resource;
        }

        //2 从内存中拿

        resource = lruMemoryCache.get(key);
        if (resource != null) {
            resource.acquired();
            /**
             *  要从内存中移除
             *  Lru可能会移除这个resource  或者也可能recycle掉这个resource
             *  如果不移除，下次使用这个resource的时候可以从活动资源中找到，但是可能被recycle掉了
             **/
            lruMemoryCache.removeResource(key);
            activeResource.active(key, resource);
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
     * @param key
     * @param resource
     */
    @Override
    public void onResourceAcquired(Key key, Resource resource) {
        //活动缓存中移除
        activeResource.deActive(key);
        //加入内存缓存
        lruMemoryCache.put(key, resource);

    }
}
