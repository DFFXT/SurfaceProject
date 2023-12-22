package com.example.surfaceproject.gl.graph.texture;


import android.content.Context;

import com.example.surfaceproject.R;

public class LuminanceFilter extends BaseFilter {

    public LuminanceFilter(Context context) {
        super(context, R.raw.luminance_frg);
    }

}
