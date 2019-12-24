package severinnitsche.com.github.security.algorithm;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.0
*
*/

public interface Decrypt<T> {
  public T decrypt(T plain, String password);
}
