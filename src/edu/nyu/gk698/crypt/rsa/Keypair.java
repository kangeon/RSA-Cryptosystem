package edu.nyu.gk698.crypt.rsa;

import java.util.Random;

public class Keypair {
  
  private int n;
  private int encryptKey;
  private int decryptKey;
  private int aValue;
  private boolean randomBitsTraced = false;
  private boolean notPrimeTraced = false;
  private boolean primeTraced = false;
  
  public int getPrimeCandidate(boolean trace) {
    
    //set first bit of candidate to be 1 by left shifting
    int primeCandidate = 1;
    primeCandidate <<= 6;
    
    Random rand = new Random();
    int randomNumber = 0;
    
    int leastSignificantBit = 0;
    
    if(randomBitsTraced == false && trace == true) System.out.println("========================="
        + "=====Line 104==============================");
    
    //get middle 5 bits by generating 5 random numbers with
    //pseudorandom number routine and extracting the least significant
    //bit from each.
    for (int i = 0; i < 5; i++) {
      randomNumber = rand.nextInt(Integer.MAX_VALUE) + 1;
      if(randomBitsTraced == false && trace == true) {
        System.out.println("Random number #" + (i + 1) + " is: " + randomNumber);
        System.out.println("Random number #" + (i + 1) + " in binary format is: " 
            + Helper.toBinaryString(randomNumber, 32));        
      }
      
      //extract least significant bit from the random number with bitwise AND
      leastSignificantBit = randomNumber & 1;
      if(randomBitsTraced == false && trace == true)
        System.out.println("Extracted bit for random number #" + (i + 1) + " is: "
          + leastSignificantBit);
      
      //add the random bit appropriate place in middle 5 bits of the 
      //prime candidate number
      leastSignificantBit <<= (5 - i);
      primeCandidate = primeCandidate + leastSignificantBit;
    }
    
    //set the last bit of the prime candidate to 1
    primeCandidate = primeCandidate + 1;
    
    if(randomBitsTraced == false && trace == true)
      System.out.println("Prime candidate generated is: " + primeCandidate);
    String primeCandidateInBinary = Helper.toBinaryString(primeCandidate, 32);
    if(randomBitsTraced == false && trace == true)
      System.out.println("Prime candidate in binary format is: " 
        + primeCandidateInBinary);
    
    randomBitsTraced = true;
    return primeCandidate;
  }
  
  //Miller-Rabin algorithm as taught in class notes
  public boolean millerRabin(int a, int n, boolean trace) {
    
    boolean perhapsPrime = false;
    int x = n - 1;
    int y = 1;
    int z = 0;
    int k = Helper.positionOfMostSignificantBit(x);
    
    if (trace == true) {
      System.out.println("Trace of prime candidate n = " + n 
          + " with a = " + a);
    }
    
    for (int i = k; i >= 0; i--) {
      z = y;
      y = y * y % n;
      if (trace == true) {
        System.out.println("z = " + z + " | y = " + y + " | bit of x" + i 
            + " = " + ((x >> i) & 1) + " | y2 = " 
            + ((((x >> i) & 1) == 1) ? y * a % n : y * y % n));  
      }

      if (y == 1 && z != 1 && z != (n - 1)) {
        perhapsPrime = false;
        aValue = a;
        if (trace == true) {
          System.out.println(n + " is not prime: bad square root");  
        }
        return perhapsPrime;
      }
      
      //check the bit of x sub i
      if(((x >> i) & 1) == 1) {
        y = y * a % n;
      }
    }
    
    if(y != 1) {
      perhapsPrime = false;
      aValue = a;
      if (trace == true) {
        System.out.println(n + " is not prime: bad final value");  
      }
      return perhapsPrime;
    }
    else {
      perhapsPrime = true;
      aValue = a;
      return perhapsPrime;
    }
  }
  
  public boolean testPrime(int n) {
    
    boolean testPassed = false;
    Random rand = new Random();
    
    //generate a values such that 0 < a < n by getting a pseudorandom number 
    //and cutting it down to size by computing its remainder modulo n
    //20 values of a are stored in an int array to ensure the same a value is
    //not used in more than one test.
    boolean isDistinct;
    int a = 0;
    int[] aValues = new int[20];
    
    for (int i = 0; i < 20; i++) {
      //keep generating random a if value of a has already been tested
      do {
        
        do {
          a = (rand.nextInt(Integer.MAX_VALUE) + 1) % n; 
        } while (a == 0);
        
        isDistinct = true;
        //check if the value of a was tested already
        for (int j = 0; j < i; j++) {
          if(aValues[j] == a) {
            isDistinct = false;
            break;
          }
        }
        if(isDistinct) {
          aValues[i] = a;
          testPassed = millerRabin(a, n, false);
          if(testPassed == false) {
            return testPassed;
          }
        }
      } while (isDistinct == false);
    }
    return testPassed;
  }
  
  public int getPrime(boolean trace) {
    int candidate;
    //generate candidates until we get a not prime number using the 
    //Miller-Rabin test
    do {
      candidate = getPrimeCandidate(trace);
    } while(testPrime(candidate) == true);
    
    if (notPrimeTraced == false && trace == true) {
      System.out.println("==============================Line 119==============="
          + "===============");
      System.out.println(candidate + " was not prime for a = " + aValue);
      //produce trace of not prime candidate test
      millerRabin(aValue, candidate, true);
      notPrimeTraced = true;
    }
    
    do {
      candidate = getPrimeCandidate(trace);
    } while(testPrime(candidate) == false);
    if (primeTraced == false && trace == true) {
      System.out.println("==============================Line 123==============="
          + "===============");
      System.out.println(candidate + " is perhaps prime. One value of a"
          + " used to test is a = " + aValue);
      millerRabin(aValue, candidate, true);
      primeTraced = true;
    }
    return candidate;
  }
  
  //Find multiplicative inverse of a mod n using the extended euclidian 
  //algorithm
  public int getMultiplicativeInverse(int a, int n, boolean trace) {
   
    int q = 0;
    int r = 0;
    int s = 0;
    int sPrevious = 1;
    int t = 1;
    int tPrevious = 0;
    int sNext, tNext;
    int aOriginal = a;
    int nOriginal = n;
    
    if (trace == true) {
      System.out.println("Trace for e = " + a);
      System.out.println("q = " + (n / a) + " | r1 = " + n + " | r2 = " + a 
          + " | r3 = " + (n % a) + " | s = " + sPrevious + " | t = " + tPrevious); 
    }
    
    while(a != 0) {
      q = n / a;
      r = n % a;
      sNext = sPrevious - q * s;
      tNext = tPrevious - q * t;
      sPrevious = s;
      tPrevious = t;
      s = sNext;
      t = tNext;
      n = a;
      a = r;
      if (trace == true) {
        System.out.println(((a == 0) ? "q = " : "q = " + n / a)  
            + " | r1 = " + n + " | r2 = " + a 
            + ((a == 0) ? " | r3 = " : " | r3 = " + n % a)
            + " | s = " + sPrevious + " | t = " + tPrevious); 
      }
    }
    
    
    if(n != 1) {
      if (trace == true) {
        System.out.println("No multiplicative inverse for " + aOriginal
            + " mod " + nOriginal); 
      }
      return -1;
    }
    
    if (trace == true) {
      System.out.println("multiplicative inverse for " + aOriginal + " mod " 
          + nOriginal + " (e mod n) is "+ (tPrevious + nOriginal) % nOriginal);
    }

    return (tPrevious + nOriginal) % nOriginal; 
  }
  
  public void createKeypair(boolean trace) {
    
    int p, q, n, e, d, phi;
    
    //do while (e >= phi) for extremely unlikely case that all values of e
    //do not work
    do {
      //ensure p != q
      p = getPrime(trace);
      do {
        q = getPrime(trace);
      } while (p == q);
    
      n = p * q;
      e = 3;
      phi = (p-1) * (q-1);
      if (trace == true) {
        System.out.println("==============================Line 142==============="
            + "==============="); 
      }
      while (getMultiplicativeInverse(e, phi, trace) == -1) {
        e++;
      }
    } while (e >= phi);
    
    d = getMultiplicativeInverse(e, phi, false);
    
    this.n = n;
    this.encryptKey = e;
    this.decryptKey = d;
    
    if (trace == true) {
      System.out.println("==============================Line 152==============="
          + "===============");
      System.out.println("Value of d: " + d);
      
      
      System.out.println("==============================Line 156==============="
          + "===============");
      System.out.println("integer value of p: " + p);
      System.out.println("bit representation of p: " + Helper.toBinaryString(p, 32));
      System.out.println("integer value of q: " + q);
      System.out.println("bit representation of q: " + Helper.toBinaryString(q, 32));
      System.out.println("integer value of n: " + n);
      System.out.println("bit representation of n: " + Helper.toBinaryString(n, 32));
      System.out.println("integer value of e: " + e);
      System.out.println("bit representation e: " + Helper.toBinaryString(e, 32));
      System.out.println("integer value of d: " + d);
      System.out.println("bit representation of d: " + Helper.toBinaryString(d, 32));
    }
  }
  
  public int getN() {
    return this.n;
  }
  
  public int getEncryptKey() {
    return this.encryptKey;
  }
  
  public int getDecryptKey() {
    return this.decryptKey;
  }
  
}
