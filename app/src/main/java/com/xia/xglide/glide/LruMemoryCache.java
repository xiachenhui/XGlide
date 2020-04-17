package com.xia.xglide.glide;


import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.xia.xglide.glide.interf.K;
import com.xia.xglide.glide.interf.MemoryCache;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/16/016 21:36
 * desc : 内存缓存的实现类
 **/
public class LruMemoryCache extends LruCache<K, Resource> implements MemoryCache {
    private ResourceRemoveListener mRemovedListener;

    private  boolean  isRemoved;//是否主动移除

    public LruMemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public void setResourceRemovedListener(ResourceRemoveListener resourceRemovedListener) {
        this.mRemovedListener = resourceRemovedListener;
    }

    /**
     * 内存中移除图片
     * @param k
     * @return
     */
    @Override
    public Resource removeResource(K k) {
        isRemoved =true;
        Resource resource = remove(k);
        isRemoved =false;
        return resource;
    }

    @Override
    protected int sizeOf(@NonNull K key, @NonNull Resource value) {
        //当前Resource所占的内存大小，需要区分版本
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //4.4以上 老图片大小
            return value.getBitmap().getAllocationByteCount();
        }

        //新图片大小
        return value.getBitmap().getByteCount();
    }

    /**
     * 移除老的图片
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull K key, @NonNull Resource oldValue, @Nullable Resource newValue) {
        if (mRemovedListener != null && oldValue != null && !isRemoved) {
            //没有移除就保存到复用池
            mRemovedListener.onResourceRemoved(oldValue);
        }
    }
}
