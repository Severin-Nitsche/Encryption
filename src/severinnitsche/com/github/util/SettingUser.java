package severinnitsche.com.github.util;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.0
*
*/

public abstract class SettingUser {

  protected Settings settings;

  {
    settings = new Settings();
  }

  public void setSettings(Settings settings) {
    if(settings==null) throw new IllegalStateException("Expected settings to not be null.");
    this.settings = settings;
  }

}
