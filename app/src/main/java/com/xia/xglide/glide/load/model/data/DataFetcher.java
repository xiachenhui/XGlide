package com.xia.xglide.glide.load.model.data;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 22:38
 * desc : 加载数据的接口
 **/
public interface DataFetcher<Data> {
    //加载结果回调
    interface DataFetcherCallBack<Data> {
        /**
         * 数据加载成功
         *
         * @param data
         */
        void onFetcherReady(Data data);

        /**
         * 数据加载失败
         *
         * @param e
         */
        void onLoadFail(Exception e);

    }

    //加载数据
    void loadData(DataFetcherCallBack<Data> dataDataFetcherCallBack);

    //取消加载
    void cancel();
}
