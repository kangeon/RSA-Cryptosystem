package edu.nyu.gk698.crypt.rsa;

import java.util.Random;

public class Helper {
  
  public static String toBinaryString(int number, int totalBits) {
    String binaryWithOutLeading0 = Integer.toBinaryString(number);
    StringBuilder sb = new StringBuilder();
    
    for(int i = 0; i < totalBits; i++) {
      sb.append("0");
    }
    
    return sb.toString().substring(binaryWithOutLeading0.length()) 
        + binaryWithOutLeading0;
  }
  
  public static String byteArrayToBinaryString(byte[] byteArray) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < byteArray.length; i++) {
      sb.append(Integer.toBinaryString(byteArray[i] & 255 | 256).substring(1));
    }
    return sb.toString();
  }
  
  //referred to code from
  //http://stackoverflow.com/questions/9117793/bitwise-most-significant-set-bit
  public static int positionOfMostSignificantBit(int number) {
    int mask = 1 << 31;
    for(int i = 31; i >= 0; i--) {
      if((number & mask) != 0){
        return i;
      }
      mask >>>= 1;
    }
    return -1;
  }
  
  public static int pickRandomU(Certificate cert) {
    //extract n from certificate
    int n = cert.getN();
    
    int k = positionOfMostSignificantBit(n);
    int u = 1;
    
    //set k-1 bit to 1
    u <<= k-1;
    
    //get random bits for k-2 to 0
    Random rand = new Random();
    int randomNumber;
    int leastSignificantBit = 0;
    
    for (int i = 0; i < k-2; i++) {
      randomNumber = rand.nextInt(Integer.MAX_VALUE) + 1;
      
      //extract least significant bit from the random number with bitwise AND
      leastSignificantBit = randomNumber & 1;
      
      //add the random bit appropriate place in middle 5 bits of the 
      //prime candidate number
      leastSignificantBit <<= ((k-2) - i);
      u = u + leastSignificantBit;
    }
    
    System.out.println("========================="
        + "=====Line 206==============================");
    System.out.println("k as integer: " + k);
    System.out.println("u as integer: " + u);
    
    System.out.println("========================="
        + "=====Line 208==============================");
    System.out.println("u as sequence of bits: " + toBinaryString(u, 32));
    
    return u;  
  }
}
