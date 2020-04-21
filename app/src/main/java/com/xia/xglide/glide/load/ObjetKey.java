package com.xia.xglide.glide.load;

import com.xia.xglide.glide.interf.Key;

import java.security.MessageDigest;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 23:01
 * desc : K的实现类
 **/
public class ObjetKey implements Key {
    private Object object;

    public ObjetKey(Object object) {
        this.object = object;
    }

    /**
     * 对数据进行加密，MD5 或者SHA
     *
     * @param md
     */
    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return object.toString().getBytes();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ObjetKey objetKey = (ObjetKey) obj;
        return object != null ? object.equals(objetKey) : objetKey.object == null;

    }

    @Override
    public int hashCode() {
        return object != null ? object.hashCode() : 0;
    }
}
