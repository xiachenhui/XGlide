package com.xia.xglide.glide.cache;

import android.graphics.Bitmap;

import com.xia.xglide.glide.interf.K;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/16/016 21:30
 * desc : Resource 资源，
 **/
public class Resource {

    private Bitmap mBitmap;
    //用于计数
    private int mAcquired;
    //计数为0的时候调用回收
    private ResourceListener mResourceListener;

    private K k;

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public interface ResourceListener {
        void onResourceAcquired(K k, Resource resource);
    }

    public void setResourceListener(ResourceListener resourceListener) {
        this.k = k;
        this.mResourceListener = resourceListener;
    }

    /**
     * 计数+1
     */
    public void acquired() {
        //如果图片没有被回收
        if (mBitmap.isRecycled()) {
            throw new IllegalStateException("Acquire a recycled resource");
        }
        ++mAcquired;
    }

    /**
     * 计数-1
     */
    public void release() {
        if (--mAcquired == 0) {
            mResourceListener.onResourceAcquired(k, this);
        }
    }

    /**
     * 回收释放
     */
    public void recycle() {
        if (mAcquired > 0) {
            return;
        }
        if (!mBitmap.isRecycled()) {
            mBitmap.recycle();
            ;
        }

    }
}
