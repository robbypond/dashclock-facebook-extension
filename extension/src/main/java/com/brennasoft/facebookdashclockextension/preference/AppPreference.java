/*
 * Copyright 2014 Robby Pond
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brennasoft.facebookdashclockextension.preference;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brennasoft.facebookdashclockextension.R;
import com.brennasoft.facebookdashclockextension.ui.BindableAdapter;
import com.brennasoft.facebookdashclockextension.util.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AppPreference extends DialogPreference implements OnItemClickListener {

	private AppAdapter mAdapter;
	private final SharedPreferenceSaver mSharedPreferenceSaver;

    public AppPreference(Context context, AttributeSet attrs) {
        super(context, attrs);setLayoutResource(R.layout.preference);
        mSharedPreferenceSaver = new SharedPreferenceSaver(context);
    }

    @Override
    protected View onCreateDialogView() {
		ListView view = new ListView(getContext());
        view.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        view.setFastScrollEnabled(true);
        view.setAdapter(adapter());
        view.setOnItemClickListener(this);
        return view;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        loadApps();
    }

    private void loadApps() {
        PackageManager packageManager = getContext().getPackageManager();
        Set<ResolveInfo> apps = new HashSet<>();
        for(String key : AppUtils.IntentMap.keySet()) {
            List<ResolveInfo> intentApps = packageManager.queryIntentActivities(AppUtils.IntentMap.get(key), 0);
            apps.addAll(intentApps);
        }
        mAdapter.replaceWith(new ArrayList<>(apps));
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        SharedPreferences prefs = getSharedPreferences();
        final Context context = getContext();
        String componentName = prefs.getString("pref_key_app_component_name", "");
        String appName = prefs.getString("pref_key_app", "");
        PackageManager pm = context.getPackageManager();
        if (imageView != null && !TextUtils.isEmpty(componentName)) {
        	Drawable icon;
			try {
				icon = pm.getActivityIcon(ComponentName.unflattenFromString(componentName));
	            imageView.setImageDrawable(icon);
	            imageView.setVisibility(View.VISIBLE);
			} catch (NameNotFoundException exc) {
				appName = "";
				Editor e = prefs.edit();
				e.putString("pref_key_app", "");
				e.putString("pref_key_app_component_name", "");
				mSharedPreferenceSaver.savePreferences(e, true);
			}       
        }
        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(getContext().getString(R.string.title_app));
        if(!TextUtils.isEmpty(appName)) {
        	textView = (TextView) view.findViewById(R.id.summary);
        	textView.setText(appName);
        	textView.setVisibility(View.VISIBLE);
        }
        notifyChanged();
    }
	
	private ListAdapter adapter() {
        mAdapter = new AppAdapter(getContext());
        return mAdapter;
	}

    private class AppAdapter extends BindableAdapter<ResolveInfo> {

        private List<ResolveInfo> resolveInfos = Collections.EMPTY_LIST;
        private final PackageManager packageManager;

        public AppAdapter(Context context) {
            super(context);
            packageManager = context.getPackageManager();
        }

        public void replaceWith(List<ResolveInfo> resolveInfos) {
            this.resolveInfos = resolveInfos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return resolveInfos.size();
        }

        @Override
        public ResolveInfo getItem(int position) {
            return resolveInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            return inflater.inflate(R.layout.activity_list_item, container, false);
        }

        @Override
        public void bindView(ResolveInfo item, int position, View view) {
            ImageView image = (ImageView) view.findViewById(R.id.app_icon);
            TextView text = (TextView) view.findViewById(R.id.app_name);
            text.setText(item.activityInfo.applicationInfo.loadLabel(packageManager));
            image.setImageDrawable(item.activityInfo.applicationInfo.loadIcon(packageManager));
        }
    }
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
		ActivityInfo ai = mAdapter.getItem(index).activityInfo;
		Editor e = getSharedPreferences().edit();
		e.putString("pref_key_app", ai.applicationInfo.loadLabel(getContext().getPackageManager()).toString());
		e.putString("pref_key_app_component_name", new ComponentName(ai.packageName, ai.name).flattenToString());
		mSharedPreferenceSaver.savePreferences(e, true);
		getDialog().dismiss();
		notifyChanged();
	}
}
