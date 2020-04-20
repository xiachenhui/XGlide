package com.xia.xglide.glide.load.model;

import com.xia.xglide.glide.load.model.loader.ModelLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @ author : xia chen hui
 * @ date : 2020/4/20 21:24
 * @ desc : 管理Model类
 */
public class ModelLoaderRegister {

    private List<Entry<?, ?>> entries = new ArrayList<>();

    //保存数据来源
    public synchronized <Model, Data> void add(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
        entries.add(new Entry<>(modelClass, dataClass, factory));

    }

    /**
     * 获取对对应的model和data类型的modelLoader
     *
     * @param modelClass
     * @param dataClass
     * @param <Model>
     * @param <Data>
     * @return
     */
    public <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass, Class<Data> dataClass) {
        List<ModelLoader<Model, Data>> loaderList = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            //是需要的Model 和Data 类型
            if (entry.handles(modelClass, dataClass)) {
                loaderList.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }

        }
        //有多个匹配的model
        if (loaderList.size() > 1) {
            return new MultiModelLoader<>(loaderList);
        } else {
            return loaderList.get(0);
        }

    }

    /**
     * 获取所有的modelLoader
     *
     * @param modelClass
     * @param <Model>
     * @return
     */
    public <Model> List<ModelLoader<Model, ?>> getModelLoader(Class<Model> modelClass) {
        List<ModelLoader<Model, ?>> loaderList = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            if (entry.handles(modelClass)) {
                loaderList.add((ModelLoader<Model, ?>) entry.factory.build(this));
            }
        }

        return loaderList;
    }

    private static class Entry<Model, Data> {
        Class<Model> modelClass;
        Class<Data> dataClass;
        ModelLoader.ModelLoaderFactory<Model, Data> factory;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {

            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        boolean handles(Class<?> modelClass, Class<?> dataClass) {
            //A.isAssignableFrom(B)  判断A和B是否是同一个类型，或者A是否是B的子类
            return this.modelClass.isAssignableFrom(modelClass)
                    && this.dataClass.isAssignableFrom(dataClass);
        }

        boolean handles(Class<?> modelClass) {
            //A.isAssignableFrom(B)  判断A和B是否是同一个类型，或者A是否是B的子类
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }
}
