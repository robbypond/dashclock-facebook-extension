package com.brennasoft.facebookdashclockextension.preference;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class BackupHelper extends BackupAgentHelper {

	private static final String PREFS_BACKUP_KEY = "prefs";
	
	@Override
	public void onCreate() {
        String defaultPrefsFilename = getPackageName() + "_preferences";
        addHelper(PREFS_BACKUP_KEY,
                new SharedPreferencesBackupHelper(this, defaultPrefsFilename));
	}

}
