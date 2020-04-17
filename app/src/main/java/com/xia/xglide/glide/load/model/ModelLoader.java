package com.xia.xglide.glide.load.model;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2020/4/17/017 22:33
 * desc : XGlide调用
 **/

import com.xia.xglide.glide.interf.K;
import com.xia.xglide.glide.load.model.data.DataFetcher;

/**
 * @param <Model> 数据的来源
 * @param <Data>  加载成功后的数据类型
 */
public interface ModelLoader<Model, Data> {
    /**
     * 加载图片
     *
     * @param <Data>
     */
    class LoadData<Data> {
        //缓存的Key
        final K k;

        //加载数据的方式
        public DataFetcher<Data> fetcher;


        public LoadData(K k, DataFetcher<Data> fetcher) {
            this.k = k;
            this.fetcher = fetcher;
        }
    }

    /**
     * 判断加载图片来源是哪一种数据格式
     * 判断处理对应model的数据
     *
     * @return
     */
    boolean handles(Model model);

    /**
     * 创建加载数据的方式
     *
     * @param model
     */
    LoadData<Data> buildData(Model model);

}
