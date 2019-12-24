package severinnitsche.com.github.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;
import java.io.File;

import severinnitsche.com.github.security.algorithm.Encrypt;
import severinnitsche.com.github.security.algorithm.Decrypt;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.5
*
*/

public class Files extends SettingUser implements Encrypt<File>,Decrypt<File> {

  //-----------------File information----------------------
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

  public static String getSuffix(File f) {
    if(f.isDirectory()) throw new IllegalStateException("File "+f+" is a directory.");
    try {
      return f.getName().substring(f.getName().lastIndexOf("."));
    } catch (java.lang.StringIndexOutOfBoundsException e) {
      return "";
    }
  }

  //-----------------private File encryption---------------
  private static interface Coding {
    String code(String s, String p);
  }

  /**
  *
  * coder ist entweder Subtyp von Encrypt oder Decrypt
  * und wird via method referenz angegeben z.b.
  * oneFile(in,out,"password",Test::encrypt)
  *
  */
  private static File oneFile(File in, File out, String password, Coding coder) {
    String s = "";
    try {
      BufferedReader br = null;
      br = new BufferedReader(new FileReader(in));
      while(br.ready()) {
        s += (char)br.read();
      }
      br.close();
    } catch(IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Datei "+in+" nicht lesbar!");
    }
    try {
      BufferedWriter bw = null;
      bw = new BufferedWriter(new FileWriter(out));
      if(password.length()<8) System.out.println("Warnung: Kurzes Passwort");
      bw.write(coder.code(s,password));
      bw.flush();
      bw.close();
      return out;
    } catch(IOException e) {
      throw new IllegalStateException("Datei "+out+" nicht beschreibbar!");
    }
  }

  private static File oneDirectoryDecrypt(File in, String dirName, String password, Coding coder) {
    try {
      File out = File.createTempFile("OneDirectory",".tmp");
      out = oneFile(in,out,password,coder);
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
          oneDirectoryDecrypt(f,dirName+dir+"/",password,coder);
          f.delete();
        }
      }
      return directory;
    } catch(IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("File read/write exception occurred");
    }
  }

  private static File oneDirectoryEncrypt(File in, File out, String password, Coding coder) {
    try {
      File directory = in;
      if(!directory.isDirectory()) throw new IllegalStateException(directory+" is no directory");
      LinkedHashMap<String, File> files = new LinkedHashMap<>();
      for(File f : directory.listFiles()) {
        if(f.isDirectory()) {
          File temp = File.createTempFile("OneDirectory",".dir");
          f = oneDirectoryEncrypt(f,temp,password,coder);
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
      return oneFile(out,out,password,coder);
    } catch(IOException e) {
      throw new IllegalStateException("A Fileread/write Exception occurred.");
    }
  }

  //-----------------static file encryption----------------
  public static File ENCRYPT(File plain, String password) {
    if(plain.isDirectory()) return oneDirectoryEncrypt(plain,new File(plain.getName()+".dir"),password,Settings::ENCRYPT);
    else return oneFile(plain,Settings.NEWFILE()?new File("out.sec"):plain,password,Settings::ENCRYPT);
  }

  public static File DECRYPT(File plain, String password) {
    System.out.println(getSuffix(plain));
    if(getSuffix(plain).equals(".dir")) return oneDirectoryDecrypt(plain,"",password,Settings::DECRYPT);
    else return oneFile(plain,Settings.NEWFILE()?new File("out.ins"):plain,password,Settings::DECRYPT);
  }

  //-----------------instance File encryption--------------

  @Override
  public File encrypt(File plain, String password) {
    if(plain.isDirectory()) return oneDirectoryEncrypt(plain,new File(plain.getName()+".dir"),password,settings::encrypt);
    else return oneFile(plain,settings.newFile()?new File("out.sec"):plain,password,settings::encrypt);
  }

  @Override
  public File decrypt(File plain, String password) {
    System.out.println(getSuffix(plain));
    if(getSuffix(plain).equals(".dir")) return oneDirectoryDecrypt(plain,"",password,settings::decrypt);
    else return oneFile(plain,settings.newFile()?new File("out.ins"):plain,password,settings::decrypt);
  }

}
