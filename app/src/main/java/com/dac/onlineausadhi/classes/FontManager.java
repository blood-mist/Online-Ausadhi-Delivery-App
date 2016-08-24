package com.dac.onlineausadhi.classes;

/**
 * Created by blood-mist on 5/30/16.
 */




    import android.content.Context;
import android.graphics.Typeface;

    /**
     * Created by udit on 13/02/16.
     */
    public class FontManager {

        public static final String ROOT = "fonts/";
        public static final String FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

        public static Typeface getTypeface(Context context, String font) {
            return Typeface.createFromAsset(context.getAssets(), font);
        }

    }




