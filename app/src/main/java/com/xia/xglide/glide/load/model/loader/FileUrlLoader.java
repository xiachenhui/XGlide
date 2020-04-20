package com.xia.xglide.glide.load.model.loader;

import android.content.ContentResolver;
import android.net.Uri;

import com.xia.xglide.glide.load.ObjetKey;
import com.xia.xglide.glide.load.model.ModelLoaderRegister;
import com.xia.xglide.glide.load.model.data.FileUrlFetcher;

import java.io.InputStream;

public class FileUrlLoader implements ModelLoader<Uri, InputStream> {
    private final ContentResolver contentResolver;

    public FileUrlLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean handles(Uri uri) {

        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjetKey(uri), new FileUrlFetcher(uri, contentResolver));
    }
    public static class Factory implements ModelLoaderFactory<Uri,InputStream>{
        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegister register) {
            return new FileUrlLoader(contentResolver);
        }
    }
}
