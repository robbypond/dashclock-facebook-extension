package com.brennasoft.facebookdashclockextension.ui;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.brennasoft.facebookdashclockextension.R;
import com.brennasoft.facebookdashclockextension.util.AppUtils;
import com.brennasoft.facebookdashclockextension.util.HelpUtils;
import com.facebook.Session;

import java.util.HashSet;
import java.util.Iterator;

public class SettingsActivity extends Activity {

    private static final int FACEBOOK_LOGIN = 3001;
    static final int FACEBOOK_LOGIN_COMPLETE = 3008;
    public static final int FACEBOOK_LOGIN_FAILED = 3009;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            PrefsFragment mPrefsFragment = new PrefsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPrefsFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_about:
                HelpUtils.showAboutDialog(this);
                return true;
            case R.id.action_changelog:
                HelpUtils.showChangelog(this);
                return true;
            case R.id.action_donate:
                HelpUtils.showDonateActivity(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

        Preference mNamePreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ((PreferenceCategory) getPreferenceScreen().findPreference(
                    getString(R.string.pref_category_app))).removePreference(findPreference("pref_key_app_component_name"));

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
            // their values. When their values change, their summaries are updated
            // to reflect the new value, per the Android Design guidelines.
            Resources res = getResources();

            mNamePreference = findPreference("pref_key_name");
            mNamePreference.setOnPreferenceClickListener(this);

            findPreference(res.getString(R.string.pref_key_launch_messenger_on_message)).setEnabled(AppUtils.hasMessenger(getActivity()));

            CheckBoxPreference filterNotifications = (CheckBoxPreference) findPreference("pref_key_filter_notifications");

            final Preference notificationApplications = findPreference("pref_key_notification_applications");

            notificationApplications.setEnabled(filterNotifications.isChecked());

            bindPreferenceSummaryToValue(mNamePreference);
            bindPreferenceSummaryToValue(findPreference("pref_key_notification_types"));
            bindPreferenceSummaryToValue(findPreference("pref_key_display_style"));
            bindPreferenceSummaryToValue(notificationApplications);

            filterNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();
                    notificationApplications.setEnabled(stringValue.equalsIgnoreCase("true"));
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference == mNamePreference) {
                this.startActivityForResult(new Intent(getActivity(), LoginActivity.class), FACEBOOK_LOGIN);
                return true;
            }
            return false;
        }

        public void updateName(String name) {
            findPreference("pref_key_name").setSummary(name);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            new BackupManager(getActivity()).dataChanged();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == FACEBOOK_LOGIN_COMPLETE) {
                Session activeSession = Session.getActiveSession();
                if(activeSession != null && activeSession.isOpened()) {
                    activeSession.onActivityResult(getActivity(), requestCode,
                            resultCode, data);
                }
            } else if(resultCode == FACEBOOK_LOGIN_FAILED) {
                Toast.makeText(getActivity(), R.string.login_failure, Toast.LENGTH_LONG).show();
            }
            String name = getPreferenceManager().getSharedPreferences().getString("pref_key_name", getString(R.string.not_logged_in));
            updateName(name);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Resources resources = preference.getContext().getResources();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index]
                        : null);

            } else if(preference instanceof MultiSelectListPreference) {
                MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                Iterator<String> it = ((HashSet<String>) value).iterator();
                StringBuilder summary = new StringBuilder();
                boolean notificationTypes = preference.getKey().equalsIgnoreCase("pref_key_notification_types");
                String[] types = resources.getStringArray(notificationTypes ? R.array.notification_types : R.array.application_names);
                String[] values = resources.getStringArray(notificationTypes ? R.array.notification_type_values : R.array.application_ids);
                while(it.hasNext()) {
                    summary.append(getListPreferenceNameForType(types, values, Long.valueOf(it.next())));
                    if(it.hasNext()) {
                        summary.append(", ");
                    }
                }
                listPreference.setSummary(summary);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
                if(preference.getKey().equals("pref_key_name") && TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(preference.getContext().getString(R.string.not_logged_in));
                }
            }
            return true;
        }
    };

    private static String getListPreferenceNameForType(String[] types, String[] values, Long value) {
        int index = 0;
        for(String valueString : values) {
            Long testValue = Long.valueOf(valueString);
            if(testValue.longValue() == value.longValue()) {
                break;
            }
            index++;
        }
        return types[index];
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        Object value;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        if(preference instanceof MultiSelectListPreference) {
            value = prefs.getStringSet(preference.getKey(), new HashSet<String>());
        } else {
            value = prefs.getString(preference.getKey(), "");
        }
        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
    }
}
