package edu.mit.ira.fuzzy.data;

import java.util.ArrayList;

/**
 * A SettingGroup contains SettingValues and other SettingGroups
 *
 * @author Ira
 *
 */
public class SettingGroup extends Setting {
  public ArrayList<SettingValue> settingValues;
  public ArrayList<SettingGroup> settingGroups;
  
  public SettingGroup() {
    this.settingValues = new ArrayList<SettingValue>();
    this.settingGroups = new ArrayList<SettingGroup>();
  }
}