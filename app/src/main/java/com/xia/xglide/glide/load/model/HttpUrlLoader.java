package com.xia.xglide.glide.load.model;

import android.net.Uri;

import com.xia.xglide.glide.interf.K;
import com.xia.xglide.glide.load.ObjetKey;
import com.xia.xglide.glide.load.model.data.HttpUrlFetcher;

import java.io.InputStream;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 22:51
 * desc : model的实现
 **/
public class HttpUrlLoader implements ModelLoader<Uri, InputStream> {
    @Override
    public boolean handles(Uri uri) {
        String scheme = uri.getScheme();
        //判断是网络还是文件请求
        return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https");
    }


    //创建加载数据的方式
    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjetKey(uri), new HttpUrlFetcher(uri));
    }
}
