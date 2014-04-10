package com.brennasoft.facebookdashclockextension.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.HashMap;
import java.util.List;

public class AppUtils {

	public static final HashMap<String, Intent> IntentMap = new HashMap<String, Intent>() {{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("fplusfree://notifications"));
		intent.setPackage("uk.co.senab.blueNotifyFree");
		put("uk.co.senab.blueNotifyFree", intent);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("fplus://notifications"));
		intent.setPackage("uk.co.senab.blueNotify");
		put("uk.co.senab.blueNotify", intent);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.facebook.com/notifications"));
		intent.setPackage("com.seesmic");
		put("com.seesmic", intent);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.facebook.com/notifications"));
		intent.setPackage("com.seesmic.pro");
		put("com.seesmic.pro", intent);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("facebook://notifications"));
		intent.setPackage("com.facebook.katana");
		put("com.facebook.katana", intent);
		intent = new Intent();
		intent.setPackage("com.flipster");
		put("com.flipster", intent);
		intent = new Intent();
		intent.setPackage("com.flipster.pro");
		put("com.flipster.pro", intent);
		intent = new Intent(Intent.ACTION_MAIN);
		intent.setPackage("app.fastfacebook.com");
		put("app.fastfacebook.com", intent);
		intent = new Intent(Intent.ACTION_MAIN);
		intent.setPackage("app.fastpro.com");
		put("app.fastpro.com", intent);
        intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage("com.abewy.klyph_beta");
        put("com.abewy.klyph_beta", intent);
        intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage("com.abewy.klyph_beta.old");
        put("com.abewy.klyph_beta.old", intent);
        intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage("com.abewy.klyph.pro");
        put("com.abewy.klyph.pro", intent);
        intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage("com.radiantbits.atrium");
        put("com.radiantbits.atrium", intent);
		intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.facebook.com"));
		put("browsers", intent);
	}};

	public static Intent getIntent(String packageName) {
		return IntentMap.containsKey(packageName) ? IntentMap.get(packageName) : IntentMap.get("default");
	}

    public static boolean isIntentAvailable(Context context, Intent intent, boolean defaultOnly) {
        List<ResolveInfo> list =
                context.getPackageManager().queryIntentActivities(intent, defaultOnly ? PackageManager.MATCH_DEFAULT_ONLY : 0);
        return list.size() > 0;
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        return isIntentAvailable(context, intent, true);
    }

    public static boolean hasMessenger(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setPackage("com.facebook.orca");
        return AppUtils.isIntentAvailable(context, intent, false);
    }
}
