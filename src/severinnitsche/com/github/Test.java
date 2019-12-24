package severinnitsche.com.github;

import severinnitsche.com.github.math.Prime;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Set;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.1
*
*/

public class Test {

  public static void main(String[] args) {
    if(args.length!=3 && args.length!=2) {
      System.out.println("Arguments: <Directory> <Output File> <Password> | <Input File> <Password>");
      System.exit(1);
    }
    if(args.length==3) oneDirectoryEncrypt(new File(args[0]),new File(args[1]),args[2]);
    else oneDirectoryDecrypt(new File(args[0]),"",args[1]);
  }

  public static File oneDirectoryDecrypt(File in, String dirName, String password) {
    try {
      File out = File.createTempFile("OneDirectory",".tmp");
      out = oneFile(in,out,password);
      BufferedReader br = new BufferedReader(new FileReader(out));
      String dir = "";
      while(br.ready()) {
        char c = (char)br.read();
        if(c!=':') dir += c;
        else break;
      }
      File directory = new File(dirName+dir);
      if(!directory.mkdir()) throw new IllegalStateException("Cannot create directory "+dir);
      LinkedHashMap<String, Integer> files = new LinkedHashMap<>();
      String fileName = "";
      while(br.ready()) {
        char c = (char)br.read();
        if(c==':'&&fileName.equals("")) break;
        if(c!=':') fileName += c;
        else {
          String size = "";
          while(br.ready()) {
            char d = (char)br.read();
            if(d!=':') size += d;
            else break;
          }
          files.put(fileName,Integer.parseInt(size));
          fileName = "";
        }
      }
      Set<String> keys = files.keySet();
      for(String key : keys) {
        File f = new File(dirName+dir+File.separator+key);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        for(int i=0; i<files.get(key); i++) {
          bw.write(br.read());
        }
        bw.flush();
        bw.close();
        if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".dir")) {
          oneDirectoryDecrypt(f,dirName+dir+"/",password);
          f.delete();
        }
      }
      return directory;
    } catch(IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("File read/write exception occurred");
    }
  }

  public static File oneDirectoryEncrypt(File in, File out, String password) {
    try {
      File directory = in;
      if(!directory.isDirectory()) throw new IllegalStateException(directory+" is no directory");
      LinkedHashMap<String, File> files = new LinkedHashMap<>();
      for(File f : directory.listFiles()) {
        if(f.isDirectory()) {
          File temp = File.createTempFile("OneDirectory",".dir");
          f = oneDirectoryEncrypt(f,temp,password);
        }
        files.put(f.getName(),f);
      }
      BufferedWriter bw = new BufferedWriter(new FileWriter(out));
      char seperator = ':'; //Space
      bw.write(in.getName()+seperator);
      Set<String> keys = files.keySet();
      for(String key : keys) {
        System.out.println(getSize(files.get(key)));
        bw.write(key+seperator+getSize(files.get(key))+seperator);
      }
      bw.write(seperator);
      for(String key : keys) {
        BufferedReader br = new BufferedReader(new FileReader(files.get(key)));
        while(br.ready()) {
          bw.write((char)br.read());
        }
        br.close();
      }
      bw.flush();
      bw.close();
      return oneFile(out,out,password);
    } catch(IOException e) {
      throw new IllegalStateException("A Fileread/write Exception occurred.");
    }
  }

  public static File oneFile(File in, File out, String password) {
    String s = "";
    try {
      BufferedReader br = null;
      br = new BufferedReader(new FileReader(in));
      while(br.ready()) {
        s += (char)br.read();
      }
      System.out.println(s);
      System.out.println(oneTimePad(s,password));
      br.close();
    } catch(IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Datei "+in+" nicht lesbar!");
    }
    try {
      BufferedWriter bw = null;
      bw = new BufferedWriter(new FileWriter(out));
      if(password.length()<8) System.out.println("Warnung: Kurzes Passwort");
      bw.write(oneTimePad(s,password));
      bw.flush();
      bw.close();
      return out;
    } catch(IOException e) {
      throw new IllegalStateException("Datei "+out+" nicht beschreibbar!");
    }
  }

  public static int getSize(File f) {
    try {
      int s = 0;
      BufferedReader br = new BufferedReader(new FileReader(f));
      while(br.ready()) {
        br.read();
        s++;
      }
      return s;
    }catch(IOException e) {
      throw new IllegalStateException("Cannot read "+f);
    }
  }

  public static String oneTimePad(String s, String p) {
    String XOR = make(p,s.length());
    String n = "";
    for(int i=0; i<s.length(); i++) {
      n += (char)(s.charAt(i)^XOR.charAt(i));
    }
    return n;
  }

  public static char encrypt(char c) {
      //System.out.println("Token: "+c+"\nAlias: "+((int)c));
      int p = Prime.findClosePrime(c);
      //System.out.println("Prime: "+p+"\nAlias: "+((char)p));
      int e = ((int)c)*p ^ p;
      //System.out.println("Encrypted: "+e+"\nAlias: "+((char)e));
      return (char)e;
  }

  public static String hash(String p) {
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

  public static String make(String s, int size) {
    String ret = "";
    while(ret.length()<size) {
      String hash = hash(s);
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
