package com.xia.xglide.glide.load.model.data;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ author xia chen hui
 * @ date 2020/4/20 20:58
 * @ desc : 处理文件类型的数据
 */
public class FileUrlFetcher implements DataFetcher<InputStream> {
    private final Uri uri;
    private final ContentResolver contentResolver;

    public FileUrlFetcher(Uri uri, ContentResolver contentResolver) {
        this.uri = uri;
        this.contentResolver = contentResolver;
    }

    @Override
    public void loadData(DataFetcherCallBack<InputStream> callBack) {
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            callBack.onFetcherReady(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callBack.onLoadFail(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void cancel() {

    }
}
