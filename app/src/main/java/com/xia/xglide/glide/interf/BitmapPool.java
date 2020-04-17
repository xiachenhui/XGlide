package com.xia.xglide.glide.interf;

import android.graphics.Bitmap;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 20:52
 * desc : 复用池
 **/
public interface BitmapPool {
    /**
     * 存入图片
     * @param bitmap
     */
    void put(Bitmap bitmap);

    /**
     *
     * @param width 宽
     * @param height 高
     * @param config 图片格式
     * @return 获取图片
     */
    Bitmap get(int width,int height,Bitmap.Config config);

}
