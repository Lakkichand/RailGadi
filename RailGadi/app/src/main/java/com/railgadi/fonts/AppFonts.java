package com.railgadi.fonts;

import android.content.Context;
import android.graphics.Typeface;

public class AppFonts {

    public static Typeface getRobotoLight(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf");
    }

    public static Typeface getRobotoMedium(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "roboto_medium.ttf");
    }

    public static Typeface getRobotoThin(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "roboto_thin.ttf");
    }

    public static Typeface getRobotoReguler(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "roboto_raguler.ttf");
    }
}
