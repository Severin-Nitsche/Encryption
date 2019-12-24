package severinnitsche.com.github.security.algorithm;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.0
*
*/

public interface Encrypt<T> {
  public T encrypt(T plain, String password);
}
