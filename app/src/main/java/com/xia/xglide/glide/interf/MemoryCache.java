package com.xia.xglide.glide.interf;

import com.xia.xglide.glide.cache.Resource;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/16/016 21:30
 * desc :内存缓存
 **/
public interface MemoryCache {
    /**
     * 移除内存图片
     */
    interface ResourceRemoveListener {
        void onResourceRemoved(Resource resource);
    }

    void setResourceRemovedListener(ResourceRemoveListener resourceRemovedListener);

    //添加Resource 到内存
    Resource put(K k, Resource resource);

    //移除内存中的Resource
    Resource removeResource(K k);
}
