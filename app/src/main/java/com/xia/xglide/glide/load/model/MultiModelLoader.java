package com.xia.xglide.glide.load.model;

import com.xia.xglide.glide.load.model.loader.ModelLoader;

import java.util.List;

/**
 * @ author : xia chen hui
 * @ date : 2020/4/20 22:00
 * @ desc : 处理有多个匹配的情况
 */
public class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {
    private final List<ModelLoader<Model, Data>> loaderList;

    public MultiModelLoader(List<ModelLoader<Model, Data>> loaderList) {
        this.loaderList = loaderList;
    }

    @Override
    public boolean handles(Model model) {
        for (ModelLoader<Model, Data> modelLoader : loaderList) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LoadData<Data> buildData(Model model) {
        for (int i = 0; i < loaderList.size(); i++) {
            ModelLoader<Model, Data> modelLoader = loaderList.get(i);
            if (modelLoader.handles(model)) {
                return modelLoader.buildData(model);
            }
        }
        return null;
    }
}
