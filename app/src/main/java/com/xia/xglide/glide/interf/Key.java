package com.xia.xglide.glide.interf;

import java.security.MessageDigest;

public interface Key {
    //对数据进行加密
    void updateDiskCacheKey(MessageDigest md);

    //获取数据的byte数组
    byte[] getKeyBytes();
}
