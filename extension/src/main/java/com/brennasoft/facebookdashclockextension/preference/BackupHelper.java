/*
 * Copyright 2014, Robby Pond
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
