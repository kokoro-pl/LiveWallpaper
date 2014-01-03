package com.il.livewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class LiveWallpaperSettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
	public static final String BACKGROUND_ENABLED_CHECKBOX_KEY = "mCheckBoxBackgroundEnabled";
	public static final String BACKGROUND_SPEED_LIST_KEY = "mListPreferenceBackgroundSpeed";
	public static final String H_ENABLED_CHECKBOX_KEY = "mCheckBoxHEnabled";
	public static final String HF_COUNT_LIST_KEY = "mListPreferenceHFCount";
	public static final String HF_SPEED_LIST_KEY = "mListPreferenceHFSpeed";
	public static final String HB_COUNT_LIST_KEY = "mListPreferenceHBCount";
	public static final String HB_SPEED_LIST_KEY = "mListPreferenceHBSpeed";
	public static final String V_ENABLED_CHECKBOX_KEY = "mCheckBoxVEnabled";
	public static final String VF_SPEED_LIST_KEY = "mListPreferenceVFSpeed";
	public static final String VF_COUNT_LIST_KEY = "mListPreferenceVFCount";
	public static final String VF_TOUCH_ENABLED_CHECKBOX_KEY = "mCheckBoxVFTouchEnabled";
	public static final String VB_COUNT_LIST_KEY = "mListPreferenceVBCount";
	public static final String VB_SPEED_LIST_KEY = "mListPreferenceVBSpeed";
	public static final String V_SCROLL_ENABLED_CHECKBOX_KEY = "mCheckBoxVScrollEnabled";
	public static final String V_SCROLL_SPEED_LIST_KEY = "mListPreferenceVScrollSpeed";

	// Закомментированные свойства пока не поддерживаются
	private CheckBoxPreference mCheckBoxBackgroundEnabled;
	private CheckBoxPreference mCheckBoxHEnabled;
	private CheckBoxPreference mCheckBoxVEnabled;
	//private CheckBoxPreference mCheckBoxVFTouchEnabled;
	//private CheckBoxPreference mCheckBoxVScrollEnabled;
	//private ListPreference mListPreferenceBackgroundSpeed;
	private ListPreference mListPreferenceHFCount;
	private ListPreference mListPreferenceHFSpeed;
	private ListPreference mListPreferenceHBCount;
	private ListPreference mListPreferenceHBSpeed;
	private ListPreference mListPreferenceVFCount;
	private ListPreference mListPreferenceVFSpeed;
	private ListPreference mListPreferenceVBCount;
	private ListPreference mListPreferenceVBSpeed;
	//private ListPreference mListPreferenceVScrollSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.live_wallpaper_settings);
		//SharedPreferences p = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

		mCheckBoxBackgroundEnabled = (CheckBoxPreference) findPreference(BACKGROUND_ENABLED_CHECKBOX_KEY);
		mCheckBoxBackgroundEnabled.setOnPreferenceChangeListener(this);

		//mListPreferenceBackgroundSpeed = (ListPreference) findPreference(BACKGROUND_SPEED_LIST_KEY);
		//mListPreferenceBackgroundSpeed.setOnPreferenceChangeListener(this);

		mCheckBoxHEnabled = (CheckBoxPreference) findPreference(H_ENABLED_CHECKBOX_KEY);
		mCheckBoxHEnabled.setOnPreferenceChangeListener(this);

		mListPreferenceHBCount = (ListPreference) findPreference(HB_COUNT_LIST_KEY);
		mListPreferenceHBCount.setOnPreferenceChangeListener(this);

		mListPreferenceHBSpeed = (ListPreference) findPreference(HB_SPEED_LIST_KEY);
		mListPreferenceHBSpeed.setOnPreferenceChangeListener(this);

		mListPreferenceHFCount = (ListPreference) findPreference(HF_COUNT_LIST_KEY);
		mListPreferenceHFCount.setOnPreferenceChangeListener(this);

		mListPreferenceHFSpeed = (ListPreference) findPreference(HF_SPEED_LIST_KEY);
		mListPreferenceHFSpeed.setOnPreferenceChangeListener(this);

		mCheckBoxVEnabled = (CheckBoxPreference) findPreference(V_ENABLED_CHECKBOX_KEY);
		mCheckBoxVEnabled.setOnPreferenceChangeListener(this);

		mListPreferenceVFCount = (ListPreference) findPreference(VF_COUNT_LIST_KEY);
		mListPreferenceVFCount.setOnPreferenceChangeListener(this);

		mListPreferenceVFSpeed = (ListPreference) findPreference(VF_SPEED_LIST_KEY);
		mListPreferenceVFSpeed.setOnPreferenceChangeListener(this);

		//mCheckBoxVFTouchEnabled = (CheckBoxPreference) findPreference(VF_TOUCH_ENABLED_CHECKBOX_KEY);
		//mCheckBoxVFTouchEnabled.setOnPreferenceChangeListener(this);

		mListPreferenceVBCount = (ListPreference) findPreference(VB_COUNT_LIST_KEY);
		mListPreferenceVBCount.setOnPreferenceChangeListener(this);

		mListPreferenceVBSpeed = (ListPreference) findPreference(VB_SPEED_LIST_KEY);
		mListPreferenceVBSpeed.setOnPreferenceChangeListener(this);

		//mCheckBoxVScrollEnabled = (CheckBoxPreference) findPreference(V_SCROLL_ENABLED_CHECKBOX_KEY);
		//mCheckBoxVScrollEnabled.setOnPreferenceChangeListener(this);

		//mListPreferenceVScrollSpeed = (ListPreference) findPreference(V_SCROLL_SPEED_LIST_KEY);
		//mListPreferenceVScrollSpeed.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object o) {
		SharedPreferences.Editor e = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).edit();
		String key = preference.getKey();
		// Вычитываем параметры
		// @todo: нужен рефакторинг повторных кусков кода
		if (key.equals(BACKGROUND_ENABLED_CHECKBOX_KEY)) {
			Boolean enabled = Boolean.parseBoolean(o.toString());
			e.putBoolean(BACKGROUND_ENABLED_CHECKBOX_KEY, enabled);
		} else if (key.equals(H_ENABLED_CHECKBOX_KEY)) {
			Boolean enabled = Boolean.parseBoolean(o.toString());
			e.putBoolean(H_ENABLED_CHECKBOX_KEY, enabled);
		} else if (key.equals(V_ENABLED_CHECKBOX_KEY)) {
			Boolean enabled = Boolean.parseBoolean(o.toString());
			e.putBoolean(V_ENABLED_CHECKBOX_KEY, enabled);
		} else if (key.equals(VF_TOUCH_ENABLED_CHECKBOX_KEY)) {
			Boolean enabled = Boolean.parseBoolean(o.toString());
			e.putBoolean(VF_TOUCH_ENABLED_CHECKBOX_KEY, enabled);
		} else if (key.equals(V_SCROLL_ENABLED_CHECKBOX_KEY)) {
			Boolean enabled = Boolean.parseBoolean(o.toString());
			e.putBoolean(V_SCROLL_ENABLED_CHECKBOX_KEY, enabled);
		} else if (key.equals(VF_COUNT_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(VF_COUNT_LIST_KEY, id);
		} else if (key.equals(VB_COUNT_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(VB_COUNT_LIST_KEY, id);
		} else if (key.equals(HB_COUNT_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(HB_COUNT_LIST_KEY, id);
		} else if (key.equals(HF_COUNT_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(HF_COUNT_LIST_KEY, id);
		} else if (key.equals(BACKGROUND_SPEED_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(BACKGROUND_SPEED_LIST_KEY, id);
		} else if (key.equals(HF_SPEED_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(HF_SPEED_LIST_KEY, id);
		} else if (key.equals(HB_SPEED_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(HB_SPEED_LIST_KEY, id);
		} else if (key.equals(VF_SPEED_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(VF_SPEED_LIST_KEY, id);
		} else if (key.equals(VB_SPEED_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(VB_SPEED_LIST_KEY, id);
		} else if (key.equals(V_SCROLL_SPEED_LIST_KEY)) {
			int id = Integer.parseInt(o.toString());
			e.putInt(V_SCROLL_SPEED_LIST_KEY, id);
		}

		e.commit();

		return true;
	}
}
