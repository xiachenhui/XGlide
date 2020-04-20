package com.xia.xglide.glide.load.model.loader;

import android.net.Uri;

import com.xia.xglide.glide.load.model.ModelLoaderRegister;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class StringModelLoader implements ModelLoader<String, InputStream> {
    private final ModelLoader<Uri, InputStream> loader;

    public StringModelLoader(ModelLoader<Uri, InputStream> loader) {
        this.loader = loader;
    }

    @Override
    public boolean handles(String s) {
        return true;
    }


    @Override
    public LoadData<InputStream> buildData(String model) {
        Uri uri = null;
        if (model.startsWith("/")) {
            uri = Uri.fromFile(new File(model));
        } else {
            uri = Uri.parse(model);
        }

        return this.loader.buildData(uri);
    }


    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(ModelLoaderRegister register) {
            return new StringModelLoader(register.build(Uri.class, InputStream.class));
        }
    }
}
