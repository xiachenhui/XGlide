package com.xia.xglide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xia.xglide.glide.ActiveResource;
import com.xia.xglide.glide.K;
import com.xia.xglide.glide.LruMemoryCache;
import com.xia.xglide.glide.MemoryCache;
import com.xia.xglide.glide.Resource;

public class MainActivity extends AppCompatActivity implements Resource.ResourceListener, MemoryCache.ResourceRemoveListener {
    K k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public void onResourceAcquired(K k, Resource resource) {

    }

    public Resource test(K k) {
        //1 先从活动中拿

        ActiveResource activeResource = new ActiveResource(this);
        LruMemoryCache lruMemoryCache = new LruMemoryCache(10);
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
            //要从内存中移除
            //Lru可能会移除这个resource  或者也可能recycle掉这个resource
            //如果不移除，下次使用这个resource的时候可以从活动资源中找到，但是可能被recycle掉了
            lruMemoryCache.removeResource(k);
            activeResource.active(k, resource);
            return resource;
        }
        return null;
    }

    @Override
    public void onResourceRemoved(Resource resource) {

    }
}
