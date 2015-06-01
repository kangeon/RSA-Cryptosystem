package edu.nyu.gk698.crypt.rsa;

import java.nio.ByteBuffer;

public class Hasher {
  public static int hashR(byte[] r) {
    byte unpaddedHash = 0;
    
    for(int i = 0; i < r.length; i++) {
      unpaddedHash ^= r[i];
    }

    //pad hash with leading zeros and store as 32 bit integer
    int paddedHash = unpaddedHash & 0xFF;
    return paddedHash;
  }
  
  public static int hashU(int u) {
    byte[] uBytes = ByteBuffer.allocate(4).putInt(u).array();
    byte unpaddedHash = 0;
    
    for(int i = 0; i < uBytes.length; i++) {
      unpaddedHash ^= uBytes[i];
    }

    //pad hash with leading zeros and store as 32 bit integer
    int paddedHash = unpaddedHash & 0xFF;
    return paddedHash;
  }
}
