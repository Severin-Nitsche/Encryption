package severinnitsche.com.github.security.concrete;

import severinnitsche.com.github.security.algorithm.Encrypt;
import severinnitsche.com.github.security.algorithm.Decrypt;

import severinnitsche.com.github.util.Settings;
import severinnitsche.com.github.util.SettingUser;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.2
*
*/

public class OneTimePad extends SettingUser implements Encrypt<String>,Decrypt<String>{

  private static interface Maker {
    public String make(String s,int size);
  }

  private static String encrypt(String s, String p, Maker maker) {
    String XOR = maker.make(p,s.length());
    String n = "";
    for(int i=0; i<s.length(); i++) {
      n += (char)(s.charAt(i)^XOR.charAt(i));
    }
    return n;
  }

  private static String decrypt(String s, String p, Maker maker) {
    return encrypt(s,p,maker);
  }

  @Override
  public String encrypt(String s, String p) {
    return encrypt(s,p,settings::make);
  }

  @Override
  public String decrypt(String s, String p) {
    return decrypt(s,p,settings::make);
  }

  public static String ENCRYPT(String s, String p) {
    return encrypt(s,p,Settings::MAKE);
  }

  public static String DECRYPT(String s, String p) {
    return decrypt(s,p,Settings::MAKE);
  }
}
