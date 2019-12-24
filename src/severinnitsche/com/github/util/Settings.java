package severinnitsche.com.github.util;

import severinnitsche.com.github.security.hash.Hash;
import severinnitsche.com.github.security.concrete.OneTimePad;
import severinnitsche.com.github.security.concrete.SimpleHash;
import severinnitsche.com.github.security.algorithm.Decrypt;
import severinnitsche.com.github.security.algorithm.Encrypt;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.4
*
*/

public class Settings implements Encrypt<String>,Decrypt<String>,Hash{

  //----------------static references------------------------
  private static String protocol;
  private static Hash hash;
  private static Encrypt<String> encrypt;
  private static Decrypt<String> decrypt;
  private static boolean newFile;
  public static Maker maker;

  private static interface Maker {
    public String make(String s, int size);
  }

  //----------------static initializer-----------------------
  static {
    hash = new SimpleHash();
    encrypt = OneTimePad::ENCRYPT;
    decrypt = OneTimePad::DECRYPT;
    protocol = Settings.class.getResource("").getProtocol();
    newFile = false;
    maker = Strings::MAKE;
  }

  //----------------static Wrappers--------------------------
  public static String HASH(String s) {
    return hash.hash(s);
  }
  public static String ENCRYPT(String s, String p) {
    return encrypt.encrypt(s,p);
  }
  public static String DECRYPT(String s, String p) {
    return decrypt.decrypt(s,p);
  }
  public static String MAKE(String s, int size) {
    return maker.make(s,size);
  }
  public static boolean NEWFILE() {
    return newFile;
  }

  //----------------static infos-----------------------------
  public static String protocol() {
    return protocol;
  }

  //----------------static Setters---------------------------
  public static void setHASH(Hash hash) {
    if(hash==null) throw new IllegalStateException("Expected hash to be not null.");
    Settings.hash = hash;
  }
  public static void setENCRYPT(Encrypt encrypt) {
    if(encrypt==null) throw new IllegalStateException("Expected encrypt to be not null.");
    Settings.encrypt = encrypt;
  }
  public static void setDECRYPT(Decrypt decrypt) {
    if(decrypt==null) throw new IllegalStateException("Expected decrypt to be not null.");
    Settings.decrypt = decrypt;
  }
  public static void setSTRINGS(Strings strings) {
    if(strings==null) throw new IllegalStateException("Expected strings to be not null.");
    Settings.maker = strings::make;
  }
  public static void setNEWFILE(boolean newFile) {
    Settings.newFile = newFile;
  }

  //----------------instance references----------------------
  private Hash iHash;
  private boolean iNewFile;
  private Maker iMaker;
  private Encrypt<String> iEncrypt;
  private Decrypt<String> iDecrypt;

  //----------------instance initializer----------------------
  {
    hash = new SimpleHash();
    encrypt = OneTimePad::ENCRYPT;
    decrypt = OneTimePad::DECRYPT;
    iNewFile = false;
    iMaker = Strings::MAKE;
  }

  //----------------instance Wrappers------------------------
  @Override
  public String encrypt(String s, String p) {
    return encrypt.encrypt(s,p);
  }
  @Override
  public String decrypt(String s, String p) {
    return decrypt.decrypt(s,p);
  }
  @Override
  public String hash(String key) {
    return iHash.hash(key);
  }
  public String make(String s, int size) {
    return iMaker.make(s,size);
  }
  public boolean newFile() {
    return iNewFile;
  }

  //----------------instance Setters-------------------------
  public void setHash(Hash hash) {
    if(hash==null) throw new IllegalStateException("Expected hash to be not null.");
    iHash = hash;
  }
  public void setEncrypt(Encrypt encrypt) {
    if(encrypt==null) throw new IllegalStateException("Expected encrypt to be not null.");
    iEncrypt = encrypt;
  }
  public void setDecrypt(Decrypt decrypt) {
    if(decrypt==null) throw new IllegalStateException("Expected decrypt to be not null.");
    iDecrypt = decrypt;
  }
  public void setStrings(Strings strings) {
    if(strings==null) throw new IllegalStateException("Expected strings to be not null.");
    iMaker = strings::make;
  }
  public void setNewFile(boolean newFile) {
    iNewFile = newFile;
  }

}
