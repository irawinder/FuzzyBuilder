class Setting {
  public String name, type;
  
  public Setting() {
    name = "";
    type = "";
  }
}

class SettingValue extends Setting {
  public String value;
  
  public SettingValue() {
    value = "";
  }
}

class SettingGroup extends Setting {
  ArrayList<SettingValue> settingValues;
  ArrayList<SettingGroup> settingGroups;
  
  public SettingGroup() {
    this.settingValues = new ArrayList<SettingValue>();
    this.settingGroups = new ArrayList<SettingGroup>();
  }
}
