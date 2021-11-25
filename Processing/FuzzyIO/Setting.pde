/**
 * Base class for SettingValue and SettingGroup
 *
 * @author Ira
 *
 */
 class Setting {
  public String name, type;
  
  public Setting() {
    name = "";
    type = "";
  }
}

/**
 * A SettingValue is a singular setting and its value
 *
 * @author Ira
 *
 */
class SettingValue extends Setting {
  public String value;
  
  public SettingValue() {
    value = "";
  }
}

/**
 * A SettingGroup contains SettingValues and other SettingGroups
 *
 * @author Ira
 *
 */
class SettingGroup extends Setting {
  ArrayList<SettingValue> settingValues;
  ArrayList<SettingGroup> settingGroups;
  
  public SettingGroup() {
    this.settingValues = new ArrayList<SettingValue>();
    this.settingGroups = new ArrayList<SettingGroup>();
  }
}

/**
 * A Utility Class to deserialize setting data
 *
 * @author Ira
 *
 */
class SettingGroupAdapter {
  
  /**
   * Convert a JSON string of model settings to the SettingGroup class
   *
   * @param data settings formatted as json string
   * @return settings formatted as SettingGRoup class
   */
  public SettingGroup parse(String data) {
    JSONObject settingGroupJSON = parseJSONObject(data);
    try {
      return this.adapt(settingGroupJSON);
    } catch (Exception e) {
      println("JSON is not formatted correctly");
      return new SettingGroup();
    }
  }
  
  /**
   * serialize a JSONObject to SettingGroup class
   *
   * @param settingGroup data formatted as JSON
   * @return data formatted as SettingGroup class
   */
  private SettingGroup adapt(JSONObject settingGroup) {
    SettingGroup group = new SettingGroup();
    
    // Check for type group
    if (settingGroup.getString("type").equals("group")) {
      group.type = settingGroup.getString("type");
      group.name = settingGroup.getString("name");
      
      // Add SettingValues Associated with group
      JSONArray settingValues = settingGroup.getJSONArray("settingValues");
      for (int i=0; i<settingValues.size(); i++) {
        JSONObject settingValue = settingValues.getJSONObject(i);
        SettingValue value = new SettingValue();
        value.name = settingValue.getString("name");
        value.type = settingValue.getString("type");
        value.value = settingValue.getString("value");
        group.settingValues.add(value);
      }
      
      // Add Child SettingGroups attached to Group
      JSONArray settingGroups = settingGroup.getJSONArray("settingGroups");
      for (int i=0; i<settingGroups.size(); i++) {
        JSONObject childSettingGroup = settingGroups.getJSONObject(i);
        SettingGroup childGroup = adapt(childSettingGroup);
        group.settingGroups.add(childGroup);
      }
    } else {
      println("type must be group");
    }
    return group;
  }  
}
