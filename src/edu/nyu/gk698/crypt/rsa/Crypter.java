package edu.nyu.gk698.crypt.rsa;

public class Crypter {

  //Encrypt/Decrypt using Fast exponentiation
  public static int crypt(int a, int x, int n, boolean trace) {
    int y = 1;
    int k = Helper.positionOfMostSignificantBit(x);
   
    if (trace == true) {
      System.out.println("========================="
          + "=====Line 219==============================");
      System.out.println("Trace for E(e, v)");
      System.out.println("e = " + x);
      System.out.println("v = " + a);
      System.out.println("n = " + n);
      System.out.println("Using fast exponentiation to calculate "
          + a + "^" + x + " % " + n);
    }
    
    for(int i = k; i >=0; i--) {
      y = y * y % n;
      
      if (trace == true) {
        System.out.println("y = " + y + " | bit of x" + i 
            + " = " + ((x >> i) & 1) + " | y2 = " 
            + ((((x >> i) & 1) == 1) ? y * a % n : y * y % n));  
      }

      
      if(((x >> i) & 1) == 1) {
        y = y * a % n;
      } 
    }
    return y;
  }
}
