package com.xia.xglide.glide.interf;

import java.io.File;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 22:18
 * desc : 磁盘缓存
 **/
public interface DiskCache {

    interface Writer {
        boolean write(File f);
    }

    File get(Key key);

    void put(Key key, Writer writer);

    void delete(Key key);

    void clear();
}
