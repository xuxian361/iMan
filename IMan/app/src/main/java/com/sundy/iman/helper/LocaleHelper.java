package com.sundy.iman.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.orhanobut.logger.Logger;
import com.sundy.iman.config.Constants;

import java.util.Locale;

/**
 * 多语言Helper
 * Created by sundy on 17/10/11.
 */

public class LocaleHelper {

    public static Context setLocale(Context context, String language) {
        Logger.e("----->language= " + language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Configuration configuration = context.getResources().getConfiguration();
        if (language.equals(Constants.LANG_EN)) {
            configuration.setLocale(Locale.ENGLISH);
        } else if (language.equals(Constants.LANG_SC)) {
            configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
        } else if (language.equals(Constants.LANG_TC)) {
            configuration.setLocale(Locale.TRADITIONAL_CHINESE);
        } else {
            configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
        }

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        if (language.equals(Constants.LANG_EN)) {
            configuration.locale = Locale.ENGLISH;
        } else if (language.equals(Constants.LANG_SC)) {
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(Constants.LANG_TC)) {
            configuration.locale = Locale.TRADITIONAL_CHINESE;
        } else {
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public static void setConfiguration(Context context, String language) {
        Locale targetLocale = getLanguageLocale(language);
        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);//语言更换生效的代码!
    }

    private static Locale getLanguageLocale(String language) {
        if (language.equals(Constants.LANG_EN)) {
            return Locale.ENGLISH;
        } else if (language.equals(Constants.LANG_SC)) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(Constants.LANG_TC)) {
            return Locale.TRADITIONAL_CHINESE;
        } else {
            return Locale.ENGLISH;
        }
    }

}
