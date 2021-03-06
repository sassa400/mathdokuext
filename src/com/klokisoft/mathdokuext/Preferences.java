package com.klokisoft.mathdokuext;

import java.util.Map;

import com.klokisoft.mathdokuext.gridGenerating.GridGenerator.PuzzleComplexity;
import com.klokisoft.mathdokuext.painter.Painter;
import com.klokisoft.mathdokuext.painter.Painter.GridTheme;
import com.klokisoft.mathdokuext.storage.database.GridDatabaseAdapter.SizeFilter;
import com.klokisoft.mathdokuext.storage.database.GridDatabaseAdapter.StatusFilter;
import com.klokisoft.mathdokuext.tip.TipDialog;
import com.klokisoft.mathdokuext.util.SingletonInstanceNotInstantiated;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {
	@SuppressWarnings("unused")
	private static final String TAG = "MathDokuExt.Preferences";

	// Singleton reference to the preferences object
	private static Preferences mPreferencesSingletonInstance = null;

	// Actual preferences
	public SharedPreferences mSharedPreferences;

	// Global APP preferences
	public final static String APP_CURRENT_VERSION = "app_current_version";
	public final static int APP_CURRENT_VERSION_DEFAULT = -1;

	// Archive preferences
	public final static String ARCHIVE_AVAILABLE = "archive_available";
	public final static boolean ARCHIVE_AVAILABLE_DEFAULT = false;

	public final static String ARCHIVE_GRID_LAST_SHOWED = "archive_grid_last_showed";
	public final static int ARCHIVE_GRID_LAST_SHOWED_DEFAULT = -1;

	public final static String ARCHIVE_SIZE_FILTER_LAST_VALUE = "archive_size_filter_last_value";
	public final static String ARCHIVE_SIZE_FILTER_LAST_VALUE_DEFAULT = SizeFilter.ALL
			.toString();

	public final static String ARCHIVE_STATUS_FILTER_LAST_VALUE = "archive_status_filter_last_value";
	public final static String ARCHIVE_STATUS_FILTER_LAST_VALUE_DEFAULT = StatusFilter.ALL
			.toString();

	public final static String ARCHIVE_SETTING_SIZE_FILTER_VISIBLE = "archive_setting_size_filter_size_visible";
	public final static boolean ARCHIVE_SETTING_SIZE_FILTER_VISIBLE_DEFAULT = false;

	public final static String ARCHIVE_SETTING_STATUS_FILTER_VISIBLE = "archive_setting_status_filter_visible";
	public final static boolean ARCHIVE_SETTING_STATUS_FILTER_VISIBLE_DEFAULT = true;

	public final static String ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE = "archive_setting_chart_description_visible";
	public final static boolean ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT = true;

	public final static String PUZZLE_INPUT_MODE_CHANGED_COUNTER = "puzzle_input_mode_changed_counter";
	public final static int PUZZLE_INPUT_MODE_CHANGED_COUNTER_DEFAULT = 0;

	// Puzzle parameters settings to be set as default values for next game
	public final static String PUZZLE_PARAMETER_COMPLEXITY = "puzzle_parameter_complexity";
	public final static String PUZZLE_PARAMETER_COMPLEXITY_DEFAULT = PuzzleComplexity.VERY_EASY
			.toString();

	public final static String PUZZLE_PARAMETER_OPERATORS_VISIBLE = "puzzle_parameter_operators_visible";
	public final static boolean PUZZLE_PARAMETER_OPERATORS_VISIBLE_DEFAULT = true;

	public final static String PUZZLE_PARAMETER_SIZE = "puzzle_parameter_size";
	public final static int PUZZLE_PARAMETER_SIZE_DEFAULT = 4;

	// Puzzle setting preferences
	public final static String PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE = "puzzle_setting_bad_cage_math_visible";
	public final static boolean PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE_DEFAULT = true;

	public final static String PUZZLE_SETTING_CLEAR_MAYBES = "puzzle_setting_clear_maybes";
	public final static boolean PUZZLE_SETTING_CLEAR_MAYBES_DEFAULT = true;

	public final static String PUZZLE_SETTING_COLORED_DIGITS = "puzzle_setting_colored_digits";
	public final static boolean PUZZLE_SETTING_COLORED_DIGITS_DEFAULT = true;

	public final static String PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE = "puzzle_setting_duplicate_digits_visible";
	public final static boolean PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE_DEFAULT = true;

	public final static String PUZZLE_SETTING_FULL_SCREEN = "puzzle_setting_full_screen";
	public final static boolean PUZZLE_SETTING_FULL_SCREEN_DEFAULT = false;

	public final static String PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID = "puzzle_setting_maybes_displayed_in_grid";
	public final static boolean PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID_DEFAULT = true;

	public final static String PUZZLE_SETTING_PLAY_SOUND_EFFECTS = "puzzle_setting_sound_effects";
	public final static boolean PUZZLE_SETTING_PLAY_SOUND_EFFECTS_DEFAULT = true;

	public final static String PUZZLE_SETTING_THEME = "puzzle_setting_theme";
	public final static String PUZZLE_SETTING_THEME_DARK = "theme_dark";
	public final static String PUZZLE_SETTING_THEME_LIGHT = "theme_light";
	public final static String PUZZLE_SETTING_THEME_EXTENDED = "theme_extended";
	public final static String PUZZLE_SETTING_THEME_DEFAULT = PUZZLE_SETTING_THEME_EXTENDED;

	public final static String PUZZLE_SETTING_TIMER_VISIBLE = "puzzle_setting_timer_visible";
	public final static boolean PUZZLE_SETTING_TIMER_VISIBLE_DEFAULT = true;

	public final static String PUZZLE_SETTING_WAKE_LOCK = "puzzle_setting_wake_lock";
	public final static boolean PUZZLE_SETTING_WAKE_LOCK_DEFAULT = true;

	public final static String PUZZLE_SETTING_DIGITS_LONG_TOUCH = "puzzle_setting_digits_long_touch";
	public final static boolean PUZZLE_SETTING_DIGITS_LONG_TOUCH_DEFAULT = true;

	public final static String PUZZLE_SETTING_FILL_DIGITCALC = "puzzle_setting_fill_digitcalc";
	public final static boolean PUZZLE_SETTING_FILL_DIGITCALC_DEFAULT = false;


	// Statistics setting preferences
	public final static String STATISTICS_AVAILABLE = "statistics_available";
	public final static boolean STATISTICS_AVAILABLE_DEFAULT = false;

	public final static String STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE = "statistics_setting_chart_description_visible";
	public final static boolean STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT = true;

	public final static String STATISTICS_SETTING_ELAPSED_TIME_CHART_MAXIMUM_GAMES = "statistics_setting_elapsed_time_chart_maximum_games";
	public final static String STATISTICS_SETTING_ELAPSED_TIME_CHART_MAXIMUM_GAMES_DEFAULT = Integer
			.toString(100);

	public final static String STATISTICS_TAB_LAST_SHOWED = "statistics_tab_last_showed";
	public final static int STATISTICS_TAB_LAST_SHOWED_DEFAULT = -1;

	// Dependent on the speed of playing, the counter preferences are often
	// updated. For this reason they are kept in memory and only committed to
	// memory on call to commitCounters.
	private final static int PUZZLE_INPUT_MODE_CHANGED_COUNTER_ID = 0;
	private int[] counters = null;

	/**
	 * Creates a new instance of {@link Preferences}.
	 * 
	 * This object can not be instantiated directly. Use {@link #getInstance()}
	 * to get the singleton reference to the Preference object.
	 * 
	 * @param context
	 *            The context for which the preferences have to be determined.
	 * 
	 */
	private Preferences(Context context) {
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	/**
	 * Gets the singleton reference to the Preference object. If it does not yet
	 * exist then it will be created.
	 * 
	 * @param context
	 *            The context in which the GridPainter is created.
	 * 
	 * @return The context for which the preferences have to be determined.
	 */
	public static Preferences getInstance(Context context) {
		if (mPreferencesSingletonInstance == null) {
			// Only the first time this method is called, the object will be
			// created.
			mPreferencesSingletonInstance = new Preferences(context);
		}
		return mPreferencesSingletonInstance;
	}

	/**
	 * Gets the singleton reference to the Preferences object. If it does not
	 * yet exist an exception will be thrown.
	 * 
	 * @return The singleton reference to the Preferences object.
	 */
	public static Preferences getInstance() {
		if (mPreferencesSingletonInstance == null) {
			throw new SingletonInstanceNotInstantiated();
		}
		return mPreferencesSingletonInstance;
	}

	/**
	 * Upgrade preferences to another version.
	 * 
	 * @param previousInstalledVersion
	 *            The old version installed.
	 * @param currentVersion
	 *            The new version to which the preferences need to be upgraded.
	 */
	public void upgrade(int previousInstalledVersion, int currentVersion) {

		// Update preferences
		Editor prefeditor = mSharedPreferences.edit();

//		if (previousInstalledVersion < 493 && currentVersion >= 524) 
		if (previousInstalledVersion < 1) //SASSA first time install 
		{
			// On upgrade to version 2 of MathDoku the preferences are cleaned
			// up. Unnecessary preferences are deleted. Preferences which are
			// kept are renamed in order to improve future maintenance.

			// Collect values of preferences which have to converted. As
			// preference names will be converted, the get function for the
			// preferences cannot be used. So retrieve the values directly using
			// the old names.
//			boolean prefOptionClearMaybes = mSharedPreferences.getBoolean(
//					"redundantPossibles", PUZZLE_SETTING_CLEAR_MAYBES_DEFAULT);
//			boolean prefOptionPlaySoundOptions = mSharedPreferences.getBoolean(
//					"soundeffects", PUZZLE_SETTING_PLAY_SOUND_EFFECTS_DEFAULT);
//			boolean prefOptionShowBadCageMath = mSharedPreferences.getBoolean(
//					"badmaths", PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE_DEFAULT);
//			boolean prefOptionShowDupeDigits = mSharedPreferences.getBoolean(
//					"dupedigits",
//					PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE_DEFAULT);
//			boolean prefOptionShowMaybesAsGrid = mSharedPreferences
//					.getBoolean("maybe3x3",
//							PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID_DEFAULT);
//			boolean prefOptionShowTimer = mSharedPreferences.getBoolean(
//					"timer", PUZZLE_SETTING_TIMER_VISIBLE_DEFAULT);
//			String prefOptionTheme = mSharedPreferences.getString("theme",
//					PUZZLE_SETTING_THEME_DEFAULT);
//			boolean prefWakeLock = mSharedPreferences.getBoolean("wakelock",
//					PUZZLE_SETTING_WAKE_LOCK_DEFAULT);
//
//			// Delete all existing preferences to clean up historic values.
//			prefeditor.clear();
//
//			// Add all preferences which were converted from the old values.
//			prefeditor
//					.putBoolean(PUZZLE_SETTING_CLEAR_MAYBES,
//							prefOptionClearMaybes)
//					.putBoolean(PUZZLE_SETTING_PLAY_SOUND_EFFECTS,
//							prefOptionPlaySoundOptions)
//					.putBoolean(PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE,
//							prefOptionShowBadCageMath)
//					.putBoolean(PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE,
//							prefOptionShowDupeDigits)
//					.putBoolean(PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID,
//							prefOptionShowMaybesAsGrid)
//					.putBoolean(PUZZLE_SETTING_TIMER_VISIBLE,
//							prefOptionShowTimer)
//					.putString(PUZZLE_SETTING_THEME, prefOptionTheme)
//					.putBoolean(PUZZLE_SETTING_WAKE_LOCK, prefWakeLock);

			prefeditor
			.putBoolean(PUZZLE_SETTING_CLEAR_MAYBES, PUZZLE_SETTING_CLEAR_MAYBES_DEFAULT)
			.putBoolean(PUZZLE_SETTING_PLAY_SOUND_EFFECTS, PUZZLE_SETTING_PLAY_SOUND_EFFECTS_DEFAULT)
			.putBoolean(PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE, PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE_DEFAULT)
			.putBoolean(PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE, PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE_DEFAULT)
			.putBoolean(PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID, PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID_DEFAULT)
			.putBoolean(PUZZLE_SETTING_TIMER_VISIBLE, PUZZLE_SETTING_TIMER_VISIBLE_DEFAULT)
			.putString(PUZZLE_SETTING_THEME, PUZZLE_SETTING_THEME_DEFAULT)
			.putBoolean(PUZZLE_SETTING_WAKE_LOCK, PUZZLE_SETTING_WAKE_LOCK_DEFAULT);
			
			// Set value for new preferences
			prefeditor.putString(ARCHIVE_SIZE_FILTER_LAST_VALUE, ARCHIVE_SIZE_FILTER_LAST_VALUE_DEFAULT);
			prefeditor.putString(ARCHIVE_STATUS_FILTER_LAST_VALUE, ARCHIVE_STATUS_FILTER_LAST_VALUE_DEFAULT);
			prefeditor.putInt(ARCHIVE_GRID_LAST_SHOWED, ARCHIVE_GRID_LAST_SHOWED_DEFAULT);
			prefeditor.putBoolean(PUZZLE_SETTING_COLORED_DIGITS, PUZZLE_SETTING_COLORED_DIGITS_DEFAULT);
			prefeditor.putString(STATISTICS_SETTING_ELAPSED_TIME_CHART_MAXIMUM_GAMES, STATISTICS_SETTING_ELAPSED_TIME_CHART_MAXIMUM_GAMES_DEFAULT);
			prefeditor.putBoolean(PUZZLE_SETTING_FULL_SCREEN, PUZZLE_SETTING_FULL_SCREEN_DEFAULT);
			prefeditor.putInt(PUZZLE_INPUT_MODE_CHANGED_COUNTER, PUZZLE_INPUT_MODE_CHANGED_COUNTER_DEFAULT);
			prefeditor.putString(PUZZLE_PARAMETER_COMPLEXITY, PUZZLE_PARAMETER_COMPLEXITY_DEFAULT);
			prefeditor.putBoolean(PUZZLE_PARAMETER_OPERATORS_VISIBLE, PUZZLE_PARAMETER_OPERATORS_VISIBLE_DEFAULT);
			prefeditor.putInt(PUZZLE_PARAMETER_SIZE, PUZZLE_PARAMETER_SIZE_DEFAULT);
			prefeditor.putBoolean(ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE, ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT);
			prefeditor.putBoolean(ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE, ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT);
			prefeditor.putBoolean(STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE, STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT);
			prefeditor.putBoolean(STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE, STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT);
			prefeditor.putBoolean(ARCHIVE_SETTING_STATUS_FILTER_VISIBLE, ARCHIVE_SETTING_STATUS_FILTER_VISIBLE_DEFAULT);
			prefeditor.putBoolean(ARCHIVE_SETTING_SIZE_FILTER_VISIBLE, ARCHIVE_SETTING_SIZE_FILTER_VISIBLE_DEFAULT);
			prefeditor.putInt(STATISTICS_TAB_LAST_SHOWED, STATISTICS_TAB_LAST_SHOWED_DEFAULT);
			prefeditor.putBoolean(PUZZLE_SETTING_DIGITS_LONG_TOUCH, PUZZLE_SETTING_DIGITS_LONG_TOUCH_DEFAULT);
			prefeditor.putBoolean(PUZZLE_SETTING_FILL_DIGITCALC, PUZZLE_SETTING_FILL_DIGITCALC_DEFAULT);
		}

		// Save
		prefeditor.putInt(APP_CURRENT_VERSION, currentVersion);
		prefeditor.commit();
	}

	/**
	 * Checks whether the timer should be displayed.
	 * 
	 * @return True in case the timer should be displayed. False otherwise.
	 */
	public boolean isTimerVisible() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_TIMER_VISIBLE,
				PUZZLE_SETTING_TIMER_VISIBLE_DEFAULT);
	}

	/**
	 * Gets the current theme.
	 * 
	 * @return The current theme.
	 */
	public Painter.GridTheme getTheme() {
		String theme = mSharedPreferences.getString(PUZZLE_SETTING_THEME,
				PUZZLE_SETTING_THEME_DEFAULT);

		if (theme.equals(PUZZLE_SETTING_THEME_DARK)) {
			return GridTheme.DARK;
		} 
		else if (theme.equals(PUZZLE_SETTING_THEME_LIGHT)) 
		{
			return GridTheme.LIGHT;
		}
		else 
		{
			return GridTheme.EXTENDED;
		}
	}

	/**
	 * Checks whether redundant possible values in the same column or row should
	 * be removed automatically.
	 * 
	 * @return True in case redundant possible values in the same column or row
	 *         should be removed automatically. False otherwise.
	 */
	public boolean isPuzzleSettingClearMaybesEnabled() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_CLEAR_MAYBES,
				PUZZLE_SETTING_CLEAR_MAYBES_DEFAULT);
	}

	/**
	 * Gets the version number of the currently installed version of the app.
	 * 
	 * @return The version number of the currently installed version of the app.
	 */
	public int getCurrentInstalledVersion() {
		return mSharedPreferences.getInt(APP_CURRENT_VERSION,
				APP_CURRENT_VERSION_DEFAULT);
	}

	/**
	 * Checks whether it should be prevented that the screen get locked while
	 * playing.
	 * 
	 * @return True in case it should be prevented that the screen get locked
	 *         while playing.
	 */
	public boolean isWakeLockEnabled() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_WAKE_LOCK,
				PUZZLE_SETTING_WAKE_LOCK_DEFAULT);
	}
	
	/**
	 * Whether digit buttons are used.
	 */
	public boolean isDigitsLongTouchEnabled() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_DIGITS_LONG_TOUCH,
				PUZZLE_SETTING_DIGITS_LONG_TOUCH_DEFAULT);
	}

	/**
	 * Whether digit buttons are used.
	 */
	public boolean isFillKenkencalcEnabled() {
		//if preference doesn't exists, add it
		if (!mSharedPreferences.contains(PUZZLE_SETTING_FILL_DIGITCALC))
		{
			Editor prefeditor = mSharedPreferences.edit();
			prefeditor.putBoolean(PUZZLE_SETTING_FILL_DIGITCALC, PUZZLE_SETTING_FILL_DIGITCALC_DEFAULT);
			prefeditor.commit();
		}
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_FILL_DIGITCALC,
				PUZZLE_SETTING_FILL_DIGITCALC_DEFAULT);
	}

	/**
	 * Checks whether the sounds effects are enabled.
	 * 
	 * @return True in the sounds effects are enabled. False otherwise.
	 */
	public boolean isPlaySoundEffectEnabled() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_PLAY_SOUND_EFFECTS,
				PUZZLE_SETTING_PLAY_SOUND_EFFECTS_DEFAULT);
	}

	/**
	 * Mark a tip so it will not be displayed again.
	 * 
	 * @param tip
	 *            The name of the tip.
	 */
	public void setTipDoNotDisplayAgain(String tip) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putBoolean(
				TipDialog.getPreferenceStringDisplayTipAgain(tip), false);
		prefeditor.apply();
	}

	/**
	 * Check whether this tip will be shown. This method should only be called
	 * by the Tip-classes only. Use method toBeDisplayed of a specific Tip-class
	 * to determine whether a tip should be shown.
	 * 
	 * @param tip
	 *            The name of the tip.
	 * @return True in case the tip has to be shown. False otherwise.
	 */
	public boolean getTipDisplayAgain(String tip) {
		return mSharedPreferences.getBoolean(
				TipDialog.getPreferenceStringDisplayTipAgain(tip), true);
	}

	/**
	 * Get the time at which the given tip was last displayed.
	 * 
	 * @param tip
	 *            The name of the tip.
	 * @return The time at which the tip was last displayed.
	 */
	public long getTipLastDisplayTime(String tip) {
		return mSharedPreferences.getLong(
				TipDialog.getPreferenceStringLastDisplayTime(tip), 0L);
	}

	/**
	 * Set the time at which the given tip was last displayed.
	 * 
	 * @param tip
	 *            The name of the tip.
	 * @param time
	 *            The time at which the tip was last displayed.
	 */
	public void setTipLastDisplayTime(String tip, long time) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putLong(TipDialog.getPreferenceStringLastDisplayTime(tip),
				time);
		prefeditor.apply();
	}

	/**
	 * Gets all shared preferences. Should not be used if possible.
	 * 
	 * @return A mapset of all shared preferences.
	 */
	public Map<String, ?> getAllSharedPreferences() {
		return mSharedPreferences.getAll();
	}

	/**
	 * Checks whether duplicate digits should be shown in the grid.
	 * 
	 * @return True in case duplicate digits should be shown in the grid. False
	 *         otherwise.
	 */
	public boolean isDuplicateDigitHighlightVisible() {
		return mSharedPreferences.getBoolean(
				PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE,
				PUZZLE_SETTING_DUPLICATE_DIGITS_VISIBLE_DEFAULT);
	}

	/**
	 * Checks whether maybes should be shown in the same pattern as the digit
	 * buttons.
	 * 
	 * @return True in case maybes should be shown in the same pattern as the
	 *         digit buttons. False otherwise.
	 */
	public boolean isMaybesDisplayedInGrid() {
		return mSharedPreferences.getBoolean(
				PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID,
				PUZZLE_SETTING_MAYBES_DISPLAYED_IN_GRID_DEFAULT);
	}

	/**
	 * Checks whether cages having bad math should be highlighted.
	 * 
	 * @return True in case cages having bad math should be highlighted. False
	 *         otherwise.
	 */
	public boolean isBadCageMathHighlightVisible() {
		return mSharedPreferences.getBoolean(
				PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE,
				PUZZLE_SETTING_BAD_CAGE_MATHS_VISIBLE_DEFAULT);
	}

	/**
	 * Checks whether a description has to be shown below a chart in the
	 * statistics activity.
	 * 
	 * @return True in case the charts descriptions have to be shown. False
	 *         otherwise.
	 */
	public boolean isStatisticsChartDescriptionVisible() {
		return mSharedPreferences.getBoolean(
				STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE,
				STATISTICS_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT);
	}

	/**
	 * Checks whether a description has to be shown below a chart in the archive
	 * activity.
	 * 
	 * @return True in case the charts descriptions have to be shown. False
	 *         otherwise.
	 */
	public boolean isArchiveChartDescriptionVisible() {
		return mSharedPreferences.getBoolean(
				ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE,
				ARCHIVE_SETTING_CHART_DESCRIPTION_VISIBLE_DEFAULT);
	}

	/**
	 * Checks whether the filter status should be shown in the archive action
	 * bar.
	 * 
	 * @return True in case the filter status should be shown in the archive
	 *         action bar. False otherwise.
	 */
	public boolean isArchiveStatusFilterVisible() {
		return mSharedPreferences.getBoolean(
				ARCHIVE_SETTING_STATUS_FILTER_VISIBLE,
				ARCHIVE_SETTING_STATUS_FILTER_VISIBLE_DEFAULT);
	}

	/**
	 * Checks whether the size status should be shown in the archive action bar.
	 * 
	 * @return True in case the size filter should be shown in the archive
	 *         action bar. False otherwise.
	 */
	public boolean isArchiveSizeFilterVisible() {
		return mSharedPreferences.getBoolean(
				ARCHIVE_SETTING_SIZE_FILTER_VISIBLE,
				ARCHIVE_SETTING_SIZE_FILTER_VISIBLE_DEFAULT);
	}

	/**
	 * Checks whether the archive is visible.
	 * 
	 * @return True in case the archive is visible. False otherwise.
	 */
	public boolean isArchiveAvailable() {
		return mSharedPreferences.getBoolean(ARCHIVE_AVAILABLE,
				ARCHIVE_AVAILABLE_DEFAULT);
	}

	/**
	 * Enables the archive.
	 */
	public void setArchiveVisible() {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putBoolean(ARCHIVE_AVAILABLE, true);
		prefeditor.apply();
	}

	/**
	 * Checks whether the statistics are visible.
	 * 
	 * @return True in case the statistics are visible. False otherwise.
	 */
	public boolean isStatisticsAvailable() {
		return mSharedPreferences.getBoolean(STATISTICS_AVAILABLE,
				STATISTICS_AVAILABLE_DEFAULT);
	}

	/**
	 * Enables the statistics.
	 */
	public void setStatisticsAvailable() {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putBoolean(STATISTICS_AVAILABLE, true);
		prefeditor.apply();
	}

	/**
	 * Get the last value used for the status filter in the archive.
	 * 
	 * @return The last value used for the status filter in the archive.
	 */
	public StatusFilter getArchiveStatusFilterLastValueUsed() {
		return StatusFilter.valueOf(mSharedPreferences.getString(
				ARCHIVE_STATUS_FILTER_LAST_VALUE,
				ARCHIVE_STATUS_FILTER_LAST_VALUE_DEFAULT));
	}

	/**
	 * Set the last value used for the status filter in the archive.
	 * 
	 * @param statusFilter
	 *            The status filter which has to be saved as last selected in
	 *            the archive.
	 */
	public void setArchiveStatusFilterLastValueUsed(StatusFilter statusFilter) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putString(ARCHIVE_STATUS_FILTER_LAST_VALUE,
				statusFilter.toString());
		prefeditor.apply();
	}

	/**
	 * Get the last value used for the size filter in the archive.
	 * 
	 * @return The last value used for the size filter in the archive.
	 */
	public SizeFilter getArchiveSizeFilterLastValueUsed() {
		return SizeFilter.valueOf(mSharedPreferences.getString(
				ARCHIVE_SIZE_FILTER_LAST_VALUE,
				ARCHIVE_SIZE_FILTER_LAST_VALUE_DEFAULT));
	}

	/**
	 * Set the last value used for the size filter in the archive.
	 * 
	 * @param sizeFilter
	 *            The size filter which has to be saved as last selected in the
	 *            archive.
	 */
	public void setArchiveSizeFilterLastValueUsed(SizeFilter sizeFilter) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putString(ARCHIVE_SIZE_FILTER_LAST_VALUE,
				sizeFilter.toString());
		prefeditor.apply();
	}

	/**
	 * Get the id of the grid which was last shown in the archive.
	 * 
	 * @return The id of the grid which was last shown in the archive.
	 */
	public int getArchiveGridIdLastShowed() {
		return mSharedPreferences.getInt(ARCHIVE_GRID_LAST_SHOWED,
				ARCHIVE_GRID_LAST_SHOWED_DEFAULT);
	}

	/**
	 * Set the id of the grid which is currently is shown when the archive is
	 * closed.
	 * 
	 * @return The last value used for the size filter in the archive.
	 */
	public void setArchiveGridIdLastShowed(int gridId) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putInt(ARCHIVE_GRID_LAST_SHOWED, gridId);
		prefeditor.apply();
	}

	/**
	 * Initializes the counters with current values as stored in the
	 * preferences.
	 */
	private void initializeCounters() {
		if (counters == null) {
			counters = new int[12];
			counters[PUZZLE_INPUT_MODE_CHANGED_COUNTER_ID] = mSharedPreferences
					.getInt(PUZZLE_INPUT_MODE_CHANGED_COUNTER,
							PUZZLE_INPUT_MODE_CHANGED_COUNTER_DEFAULT);
		}
	}

	/**
	 * Increase the current value of a preference counter with 1 occurrence.
	 * 
	 * <b>Note:</b> For performance reasons the counter preferences are not
	 * updated on calling this method. Call commitCounters() to commit counters
	 * to storage.
	 * 
	 * @param preferenceName
	 *            The name of the preferences counter.
	 * @return The number of occurrence for this counter (after being updated).
	 */
	private int increaseCounter(int counter_id) {
		if (counters == null) {
			initializeCounters();
		}
		return counters[counter_id]++;
	}

	/**
	 * Get the maximum number of games which should be shown in a
	 * elapsed-time-chart.
	 * 
	 * @return The maximum number of games which should be shown in a
	 *         elapsed-time-chart.
	 */
	public int getStatisticsSettingElapsedTimeChartMaximumGames() {
		return Integer.parseInt(mSharedPreferences.getString(
				STATISTICS_SETTING_ELAPSED_TIME_CHART_MAXIMUM_GAMES,
				STATISTICS_SETTING_ELAPSED_TIME_CHART_MAXIMUM_GAMES_DEFAULT));
	}

	/**
	 * Increase the number of times the input mode has been changed.
	 * 
	 * @return The (updated) number of times the input mode has been changed.
	 */
	public int increaseInputModeChangedCounter() {
		return increaseCounter(PUZZLE_INPUT_MODE_CHANGED_COUNTER_ID);
	}

	/**
	 * Commit all counter values to the preferences.
	 */
	public void commitCounters() {
		if (counters != null) {
			Editor prefeditor = mSharedPreferences.edit();
			prefeditor.putInt(PUZZLE_INPUT_MODE_CHANGED_COUNTER,
					counters[PUZZLE_INPUT_MODE_CHANGED_COUNTER_ID]);
			prefeditor.commit();
		}
	}

	/**
	 * Gets the number of times the input mode has been changed.
	 * 
	 * @return The number of times the input mode has been changed.
	 */
	public int getInputModeChangedCounter() {
		return mSharedPreferences.getInt(PUZZLE_INPUT_MODE_CHANGED_COUNTER,
				PUZZLE_INPUT_MODE_CHANGED_COUNTER_DEFAULT);
	}

	/**
	 * Get the complexity of the puzzle which was last generated.
	 * 
	 * @return The complexity of the puzzle which was last generated.
	 */
	public PuzzleComplexity getPuzzleParameterComplexity() {
		return PuzzleComplexity.valueOf(mSharedPreferences.getString(
				PUZZLE_PARAMETER_COMPLEXITY,
				PUZZLE_PARAMETER_COMPLEXITY_DEFAULT));
	}

	/**
	 * Set the complexity of the puzzle which was last generated. closed.
	 * 
	 * @param puzzleComplexity
	 *            The complexity of the puzzle.
	 */
	public void setPuzzleParameterComplexity(PuzzleComplexity puzzleComplexity) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putString(PUZZLE_PARAMETER_COMPLEXITY,
				puzzleComplexity.toString());
		prefeditor.apply();
	}

	/**
	 * Get the setting for showing or hiding the operators for the puzzle which
	 * was last generated.
	 * 
	 * @return True in case the operators were hidden in the last puzzle
	 *         generated.
	 */
	public boolean getPuzzleParameterOperatorsVisible() {
		return mSharedPreferences.getBoolean(
				PUZZLE_PARAMETER_OPERATORS_VISIBLE,
				PUZZLE_PARAMETER_OPERATORS_VISIBLE_DEFAULT);
	}

	/**
	 * Set the setting for showing or hiding the operators for the puzzle which
	 * was last generated.
	 */
	public void setPuzzleParameterOperatorsVisible(boolean visible) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putBoolean(PUZZLE_PARAMETER_OPERATORS_VISIBLE, visible);
		prefeditor.apply();
	}

	/**
	 * Get the size of the puzzle which was last generated.
	 * 
	 * @return The size of the puzzle which was last generated.
	 */
	public int getPuzzleParameterSize() {
		return mSharedPreferences.getInt(PUZZLE_PARAMETER_SIZE,
				PUZZLE_PARAMETER_SIZE_DEFAULT);
	}

	/**
	 * Set the size of the puzzle which was last generated. closed.
	 */
	public void setPuzzleParameterSize(int gridSize) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putInt(PUZZLE_PARAMETER_SIZE, gridSize);
		prefeditor.apply();
	}

	/**
	 * Checks whether the full screen preference is enabled.
	 * 
	 * @return True in case the activity should request full screen mode while
	 *         playing.
	 */
	public boolean isFullScreenEnabled() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_FULL_SCREEN,
				PUZZLE_SETTING_FULL_SCREEN_DEFAULT);
	}

	/**
	 * Checks whether maybes and definitive digits should be displayed in
	 * distinct colors.
	 * 
	 * @return True in case distinct colors have to be used for displaying
	 *         maybes and definitive digits. False in case the same color is
	 *         used for both type of digits.
	 */
	public boolean isColoredDigitsVisible() {
		return mSharedPreferences.getBoolean(PUZZLE_SETTING_COLORED_DIGITS,
				PUZZLE_SETTING_COLORED_DIGITS_DEFAULT);
	}

	/**
	 * Get the tab which was displayed last time the statistics were displayed.
	 * 
	 * @return The tab which was displayed last time the statistics were
	 *         displayed.
	 */
	public int getStatisticsTabLastDisplayed() {
		return mSharedPreferences.getInt(STATISTICS_TAB_LAST_SHOWED,
				STATISTICS_TAB_LAST_SHOWED_DEFAULT);
	}

	/**
	 * Set the size of the puzzle which was last generated. closed.
	 */
	public void setStatisticsTabLastDisplayed(int tab) {
		Editor prefeditor = mSharedPreferences.edit();
		prefeditor.putInt(STATISTICS_TAB_LAST_SHOWED, tab);
		prefeditor.apply();
	}
}