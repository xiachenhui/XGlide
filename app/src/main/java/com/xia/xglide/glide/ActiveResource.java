package com.xia.xglide.glide;

import com.xia.xglide.glide.interf.K;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/16/016 21:55
 * desc : 活动资源， 正在使用中的图片资源
 **/
public class ActiveResource {

    private Map<K, ResourceWeakReference> mWeakMap = new HashMap<>();
    //引用队列
    private ReferenceQueue<Resource> mQueue;

    private Thread cleanReference;

    private Resource.ResourceListener mResourceListener;

    private boolean isShutDown;

    public ActiveResource(Resource.ResourceListener resourceListener) {
        this.mResourceListener = resourceListener;
    }
    //弱引用的Resource
    static final class ResourceWeakReference extends WeakReference<Resource> {
        private K k;

        public ResourceWeakReference(Resource referent) {
            super(referent);
        }

        public ResourceWeakReference(K k, Resource referent, ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.k = k;
        }
    }

    /**
     * 加入活动缓存
     *
     * @param k
     * @param resource
     */
    public void active(K k, Resource resource) {
        mResourceListener.onResourceAcquired(k, resource);
        mWeakMap.put(k, new ResourceWeakReference(k, resource, getResourceQueue()));
    }

    /**
     * 移除活动缓存
     *
     * @return
     */
    public Resource deActive(K k) {
        ResourceWeakReference reference = mWeakMap.remove(k);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    /**
     * 关闭线程
     */
    void shutDown() {
        isShutDown = true;
        if (cleanReference != null) {

            try {
                //强制关闭线程
                cleanReference.interrupt();
                cleanReference.join(TimeUnit.SECONDS.toMillis(5));
                if (cleanReference.isAlive()) {
                    throw new RuntimeException("Failed to join in time");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ReferenceQueue<? super Resource> getResourceQueue() {
        if (mQueue == null) {
            mQueue = new ReferenceQueue<>();

            cleanReference = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!isShutDown) {
                        try {
                            //被回收的引用，每次回收都会调用此方法
                            ResourceWeakReference ref = (ResourceWeakReference) mQueue.remove();
                            mWeakMap.remove(ref.k);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            cleanReference.start();
        }
        return mQueue;
    }

    /**
     * 获取资源
     * @param k
     * @return
     */
    public Resource getResource(K k) {
        ResourceWeakReference resourceWeakReference = mWeakMap.get(k);
        if (resourceWeakReference != null) {
            return resourceWeakReference.get();
        }

        return null;
    }

}
