package com.xia.xglide.glide;

import java.io.File;

interface ModelTypes<T> {
    T load(String string);

    T load(File file);

}
