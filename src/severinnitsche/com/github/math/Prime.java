package severinnitsche.com.github.math;

import java.util.ArrayList;

/**
*
* @author Severin Leonard Christian Nitsche
*
* @version 0.0
*
*/

public class Prime {

  private static Integer[] lowPrimes;

  static {
    System.out.println("Started lazy (low)Prime finder.");
    ArrayList<Integer> primes = new ArrayList<>();
    //add lowest lowest primes
    System.out.println("About to add lowest Primes.");
    primes.add(2);
    primes.add(3);
    System.out.println("Searching for low Primes");
    for(int i=6; i<256; i+=6) {
      for(int j=-1; j<2; j+=2) {
        int check = i+j;
        boolean prime = true;
        for(int p : primes) {
          if(check%p==0) {
            prime = false;
            break;
          } else if(p*p>check) {
              break;
          }
        }
        if(prime) primes.add(check);
      }
    }
    System.out.println("Reformatting data.");
    lowPrimes = new Integer[primes.size()];
    primes.toArray(lowPrimes);
    System.out.println("Done.");
  }

  public static int findClosePrime(char token) {
    int c = (int)token;
    for(int next = c-c%6; next>0; next-=6) {
      for(int j=-1; j<2; j+=2) {
        int check = next+j;
        boolean prime = true;
        for(int p : lowPrimes) {
          if(check%p==0) {
            prime = false;
            break;
          } else if(p*p>check) {
              break;
          }
        }
        if(prime) return check;
      }
    }
    return -1;
  }

}
