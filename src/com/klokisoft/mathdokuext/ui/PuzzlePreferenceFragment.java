package com.klokisoft.mathdokuext.ui;

import com.klokisoft.mathdokuext.Preferences;

import com.klokisoft.mathdokuext.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PuzzlePreferenceFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	Preferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.puzzle_preferences);

		mPreferences = Preferences.getInstance();

		setThemeSummary();

	}

	@Override
	public void onStart() {
		mPreferences.mSharedPreferences
				.registerOnSharedPreferenceChangeListener(this);
		super.onStart();
	}

	@Override
	public void onStop() {
		mPreferences.mSharedPreferences
				.unregisterOnSharedPreferenceChangeListener(this);

		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Preferences.PUZZLE_SETTING_THEME)) {
			setThemeSummary();
		}
	}

	/**
	 * Set summary for option "theme" to the current value of the option.
	 */
	private void setThemeSummary() {
		switch (mPreferences.getTheme()) {
		case EXTENDED:
			findPreference(Preferences.PUZZLE_SETTING_THEME).setSummary(
					getResources().getString(R.string.theme_extended));
			break;
		case LIGHT:
			findPreference(Preferences.PUZZLE_SETTING_THEME).setSummary(
					getResources().getString(R.string.theme_light));
			break;
		case DARK:
			findPreference(Preferences.PUZZLE_SETTING_THEME).setSummary(
					getResources().getString(R.string.theme_dark));
			break;
		}
	}

}