package com.xia.xglide.glide;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.xia.xglide.glide.cache.ArrayPool;
import com.xia.xglide.glide.interf.BitmapPool;
import com.xia.xglide.glide.interf.DiskCache;
import com.xia.xglide.glide.interf.MemoryCache;
import com.xia.xglide.glide.load.Engine;
import com.xia.xglide.glide.load.codec.StreamBitmapDecoder;
import com.xia.xglide.glide.load.model.loader.FileLoader;
import com.xia.xglide.glide.load.model.loader.FileUrlLoader;
import com.xia.xglide.glide.load.model.loader.HttpUrlLoader;
import com.xia.xglide.glide.load.model.loader.StringModelLoader;
import com.xia.xglide.glide.manager.RequestManagerRetriever;
import com.xia.xglide.glide.request.RequestOptions;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

public class XGlide implements ComponentCallbacks2 {
    private static volatile XGlide XGlide;

    private final Engine engine;
    private final BitmapPool bitmapPool;
    private final MemoryCache memoryCache;
    private final DiskCache diskCache;
    private final RequestManagerRetriever requestManagerRetriever;
    private final Context context;
    private final RequestOptions defaultRequestOptions;
    private final ThreadPoolExecutor executor;
    private final Registry registry;
    private final ArrayPool arrayPool;

    XGlide(Context context, RequestManagerRetriever requestManagerRetriever, GlideBuilder
            glideBuilder) {
        this.context = context.getApplicationContext();
        this.requestManagerRetriever = requestManagerRetriever;
        this.engine = glideBuilder.engine;
        this.bitmapPool = glideBuilder.bitmapPool;
        this.memoryCache = glideBuilder.memoryCache;
        this.diskCache = glideBuilder.diskCache;
        this.defaultRequestOptions = glideBuilder.defaultRequestOptions;
        this.executor = glideBuilder.executor;
        this.arrayPool = glideBuilder.arrayPool;

        //注册机
        registry = new Registry();
        ContentResolver contentResolver = context.getContentResolver();
        registry.add(String.class, InputStream.class, new StringModelLoader.Factory())
                .add(Uri.class, InputStream.class, new HttpUrlLoader.Factory())
                .add(Uri.class, InputStream.class, new FileUrlLoader.Factory(contentResolver))
                .add(File.class, InputStream.class, new FileLoader.Factory())
                .register(InputStream.class, new StreamBitmapDecoder(bitmapPool, arrayPool));
    }

    public Registry getRegistry() {
        return registry;
    }

    public static XGlide get(Context context) {
        if (XGlide == null) {
            synchronized (XGlide.class) {
                if (XGlide == null) {
                    checkAndInitializeGlide(context);
                }
            }
        }

        return XGlide;
    }

    private static void checkAndInitializeGlide(Context context) {
        initializeGlide(context, new GlideBuilder());
    }


    private static void initializeGlide(Context context, GlideBuilder builder) {
        Context applicationContext = context.getApplicationContext();
        XGlide glide = builder.build(applicationContext);
        applicationContext.registerComponentCallbacks(glide);
        XGlide.XGlide = glide;
    }


    public Engine getEngine() {
        return engine;
    }

    public RequestOptions getDefaultRequestOptions() {
        return defaultRequestOptions;
    }

    public BitmapPool getBitmapPool() {
        return bitmapPool;
    }

    public ArrayPool getArrayPool() {
        return arrayPool;
    }

    public Context getContext() {
        return context;
    }


    public MemoryCache getMemoryCache() {
        return memoryCache;
    }


    public DiskCache getDiskCache() {
        return diskCache;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }


    public RequestManagerRetriever getRequestManagerRetriever() {
        return requestManagerRetriever;
    }


    /**
     * 获得请求管理生成器
     *
     * @param context
     * @return
     */
    private static RequestManagerRetriever getRetriever(Context context) {
        return XGlide.get(context).getRequestManagerRetriever();
    }

    /**
     * 获得请求管理器
     *
     * @param context 同步生命周期
     * @return
     */
    public static RequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * 获得请求管理器
     *
     * @param activity 同步生命周期
     * @return
     */
    public static RequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(View view) {
        return getRetriever(view.getContext()).get(view);
    }


    public static RequestManager with(FragmentActivity activity) {
        return getRetriever(activity).get(activity);
    }


    public static RequestManager with(android.app.Fragment fragment) {
        return getRetriever(fragment.getActivity()).get(fragment);
    }


    public static RequestManager with(Fragment fragment) {
        return getRetriever(fragment.getActivity()).get(fragment);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Do nothing.
    }

    /**
     * 内存紧张
     */
    @Override
    public void onLowMemory() {
        //memory和bitmappool顺序不能变
        //因为memory移除后会加入复用池
        memoryCache.clearMemory();
        bitmapPool.clearMemory();
        arrayPool.clearMemory();
    }

    /**
     * 需要释放内存
     *
     * @param level 内存状态
     */
    @Override
    public void onTrimMemory(int level) {
        memoryCache.trimMemory(level);
        bitmapPool.trimMemory(level);
        arrayPool.trimMemory(level);
    }
}
