package com.xia.xglide.glide.load.model.data;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 22:43
 * desc : 数据加载的实现类
 **/
public class HttpUrlFetcher implements DataFetcher<InputStream> {
    private final Uri uri;
    private boolean isCancel;

    public HttpUrlFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void loadData(DataFetcherCallBack<InputStream> callBack) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if (isCancel) {
                //取消的直接返回
                return;
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                callBack.onFetcherReady(inputStream);
            } else {
                callBack.onLoadFail(new RuntimeException(connection.getResponseMessage()));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void cancel() {
        isCancel = true;
    }
}
