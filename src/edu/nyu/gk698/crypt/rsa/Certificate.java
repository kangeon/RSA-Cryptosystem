package edu.nyu.gk698.crypt.rsa;

import java.nio.charset.Charset;
import java.util.Arrays;

public class Certificate {
  private byte[] r = new byte[14];
  private int s;
  
  public void createR(String name, int n, int e) {
    
     //fill bytes 1-6 of r with name
     byte[] nameField = name.getBytes(Charset.forName("UTF-8"));
   
     //pad left with blanks if name is shorter than 6 chars
     int padCount = ((nameField.length > 6) ? 0 : 6 - nameField.length);
     Arrays.fill(r, 0, padCount, (byte) 32);
     System.arraycopy(nameField, 0, r, padCount, 6 - padCount);

     //fill bytes 7-10 with n padded with leading 0 bits     
     for(int i= 0; i < 4; i++) {
       r[9-i] = (byte)(n & 0xFF);
       n >>= 8;
     }
     
     //fill bytes 11-14 with e padded with leading 0 bits
     for(int j = 0; j < 4; j++) {
       r[13-j] = (byte)(e & 0xFF);
       e >>= 8;
     }
  }
  
  //get signature with a trusted authority's private key
  public void createSignature(byte[] r, Keypair keys) {
    s = Crypter.crypt(Hasher.hashR(r), keys.getDecryptKey(), keys.getN(), false);
  }
  
  public void createCertificate(String name, Keypair certifiee, Keypair certifier) {
    createR(name, certifiee.getN(), certifiee.getEncryptKey());
    createSignature(r, certifier);
    System.out.println("==============================Line 185==============="
        + "===============");
    System.out.println("r as sequence of bits: " + Helper.byteArrayToBinaryString(r));
    System.out.println("h(r) as sequence of bits: " + Helper.toBinaryString(Hasher.hashR(r), 32));
    System.out.println("s as sequence of bits: " + Helper.toBinaryString(s, 32));
    System.out.println("==============================Line 187==============="
        + "===============");
    System.out.println("h(r) as integer: " + Hasher.hashR(r));
    System.out.println("s as integer: " + s);
    System.out.println(certifier.getDecryptKey() + " " + certifier.getN());
  }
  
  public int getN() {
    return r[9] & 0xFF | (r[8] & 0xFF) << 8 | (r[7] & 0xFF) << 16 | (r[6] & 0xFF) << 24;
  }
  
  public int getE() {
    return r[13] & 0xFF | (r[12] & 0xFF) << 8 | (r[11] & 0xFF) << 16 | (r[10] & 0xFF) << 24;
  }
  
  public byte[] getR() {
    return this.r;
  }
  
  public int getS() {
    return this.s;
  }
}
