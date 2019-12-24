package severinnitsche.com.github;

import java.io.File;
import severinnitsche.com.github.util.Settings;
import static severinnitsche.com.github.util.Files.ENCRYPT;
import static severinnitsche.com.github.util.Files.DECRYPT;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.0
*
*/

public class Code {

  static String execute = Settings.protocol().equals("jar")?"java -jar "+new java.io.File(Code.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName():"java "+Code.class.getName();
  static String help = "Usage: "+execute+" [-e|-d|-h] <input File> <password>";

  public static void main(String[] args) {
    try {
      switch(args[0]) {
        case "-e":
          ENCRYPT(new File(args[1]),args[2]);
          break;
        case "-d":
          DECRYPT(new File(args[1]),args[2]);
          break;
        case "-h":
        default:
          System.out.println(help);
      }
    } catch(java.lang.ArrayIndexOutOfBoundsException e) {
      System.out.println(help);
    } catch(Exception e) {
      e.printStackTrace();
      System.out.println("Whoopsi! Something unexpected happened.\nPlease contact your software provider and send him the error log with a short explanation of what you did and how one might reproduce the bug.\nI'm sorry for your inconvenience.");
    }
  }
}
