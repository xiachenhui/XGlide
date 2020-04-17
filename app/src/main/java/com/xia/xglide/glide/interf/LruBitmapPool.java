package com.xia.xglide.glide.interf;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.xia.xglide.glide.interf.BitmapPool;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 20:54
 * desc : 复用池实现类
 **/
public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {
    //复用池中的集合用于筛选大小符合条件的bitmap
    private NavigableMap<Integer, Integer> map = new TreeMap<>();
    //超过图片大小的一个倍数
    private int MAX_OVER_SIZE = 2;

    private boolean isRemoved;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    /**
     * 图片放入复用池
     *
     * @param bitmap
     */
    @Override
    public void put(Bitmap bitmap) {
        //判断图片是否可以复用
        if (!bitmap.isMutable()) {
            //不能复用的直接回收释放
            bitmap.recycle();
            return;
        }
        //获取图片大小
        int size;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            size = bitmap.getAllocationByteCount();
        } else {
            size = bitmap.getByteCount();
        }
        if (size > maxSize()) {
            bitmap.recycle();
            return;
        }
        //大小没有超过复用池的最大值，
        put(size, bitmap);
        //放到筛选集合中，只需要key，值随意填写
        map.put(size, 0);

    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        //暂时只处理2种图片格式
        int size = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        //从筛选集合中获取大小等于size  或者大于size的key
        Integer key = map.ceilingKey(size);
        //筛选出的图片不能超过当前图片的2倍
        if (key != null && key <= size * MAX_OVER_SIZE) {
            //主动移除
            isRemoved = true;
            Bitmap remove = remove(key);
            isRemoved = false;
            return remove;
        }
        return null;
    }

    @Override
    protected int sizeOf(@NonNull Integer key, @NonNull Bitmap value) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return value.getAllocationByteCount();
        } else {
            return value.getByteCount();
        }

    }

    @Override
    protected void entryRemoved(boolean evicted, @NonNull Integer key, @NonNull Bitmap oldValue, @Nullable Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        map.remove(key);
        //如果是被动移除，就回收
        if (!isRemoved) {
            oldValue.recycle();
        }
    }
}
