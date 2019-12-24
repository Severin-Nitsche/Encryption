package severinnitsche.com.github.security.concrete;

import severinnitsche.com.github.security.hash.Hash;
import severinnitsche.com.github.math.Prime;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.0
*
*/

public class SimpleHash implements Hash{
  public String hash(String p) {
    //TODO: remove this and implement proper S Code
    if(p.length()>32) throw new IllegalArgumentException("String shall not be any longer than 32 letters.");
    char[] chars = p.toCharArray();
    byte[] encrypted = new byte[chars.length*2];
    for(int i=0; i<chars.length; i++) {
      char t = encrypt(chars[i]);
      encrypted[2*i] = (byte)((t & 0xff00)>>>8);
      encrypted[2*i+1] = (byte)(t & 0x00ff);
    }
    int[] parts = new int[8];
    String ret = "";
    for(int i=0; i<8; i++) {
      for(byte b : encrypted) {
        parts[i] = (parts[i]<<1) | (((int)b) & lowPower(i));
      }
      ret += (char)(parts[i] & 0xffff);
      ret += (char)((parts[i] & 0xffff0000) >>> 16);
    }
    return ret;
  }

  private static int lowPower(int i) {
    int[] lp = {1,2,4,8,16,32,64,128,256};
    return lp[i];
  }

  public static char encrypt(char c) {
      //System.out.println("Token: "+c+"\nAlias: "+((int)c));
      int p = Prime.findClosePrime(c);
      //System.out.println("Prime: "+p+"\nAlias: "+((char)p));
      int e = ((int)c)*p ^ p;
      //System.out.println("Encrypted: "+e+"\nAlias: "+((char)e));
      return (char)e;
  }
}
