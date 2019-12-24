package severinnitsche.com.github.util;

import severinnitsche.com.github.util.Settings;
import severinnitsche.com.github.security.hash.Hash;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.2
*
*/

public class Strings extends SettingUser {
  
  public String make(String s, int size) {
    return make(s,size,settings::hash);
  }

  public static String MAKE(String s, int size) {
    return make(s,size,Settings::HASH);
  }

  private static String make(String s, int size, Hash hasher) {
    String ret = "";
    while(ret.length()<size) {
      String hash = hasher.hash(s);
      for(int i=0; (i<hash.length() || i<s.length()) && ret.length()<size; i++) {
        char a = hash.charAt(i%hash.length());
        char b = s.charAt(i%s.length());

        char c = (char)(a ^ b);
        char d = (char)(~a);
        char e = (char)(~b);
        char f = (char)(a | b);
        char g = (char)(a & b);
        if(ret.length()<size) ret+=c;
        if(ret.length()<size) ret+=d;
        if(ret.length()<size) ret+=e;
        if(ret.length()<size) ret+=f;
        if(ret.length()<size) ret+=g;
      }
      if(ret.length()==size) return ret;
      int magic = 0;
      for(int i=0; i<16 && ret.length()<size; i++) {
        ret += hash.charAt(i);
      }
      s = hash;
    }
    return ret;
  }
}
