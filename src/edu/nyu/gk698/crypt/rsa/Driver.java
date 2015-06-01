package edu.nyu.gk698.crypt.rsa;

public class Driver {
  
  public static void main (String[] args) {
    System.out.println("Building RSA System");
    System.out.println("Generating keypair for Alice");
    System.out.println("Trace for Alice's keypair:");
    Keypair aliceKeys = new Keypair();
    aliceKeys.createKeypair(true);
    
    System.out.println("");
    System.out.println("Creating digital certificate for Alice");
    System.out.println("Generating keypair for Trent");
    Keypair trentKeys = new Keypair();
    trentKeys.createKeypair(false);
    
    System.out.println("Generating certificate for Alice signed by"
        + " Trent");
    Certificate aliceCert = new Certificate();
    aliceCert.createCertificate("Alice", aliceKeys, trentKeys);
    
    System.out.println("Alice authenticates herself to Bob");
    System.out.println("Bob picks random u");
    int u = Helper.pickRandomU(aliceCert);
    
    System.out.println("Bob sends u to Alice");
    System.out.println("Alice computes h(u)");
    
    int hu = Hasher.hashU(u);
    
    System.out.println("========================="
        + "=====Line 215==============================");
    System.out.println("u as integer: " + u);
    System.out.println("u as sequence of bits: " + Helper.toBinaryString(u, 32));
    
    System.out.println("h(u) as integer: " + hu);
    System.out.println("h(u) as sequence of bits: " + Helper.toBinaryString(hu, 32));
    
    System.out.println("Alice decrypts h(u) with her private key to get v");
    int v = Crypter.crypt(hu, aliceKeys.getDecryptKey(), aliceKeys.getN(), false);
    System.out.println("v as integer: " + v);
    System.out.println("v as sequence of bits: " + Helper.toBinaryString(v, 32));
    
    System.out.println("Alice sends v to Bob");
    System.out.println("Bob encrypts v with Alice's public key");
    
    //get alice's public key from her certificate
    int e = aliceCert.getE();
    int ev = Crypter.crypt(v, e, aliceCert.getN(), false);
    
    System.out.println("E(e, v) as integer: " + ev);
    System.out.println("E(e, v) as sequence of bits: " + Helper.toBinaryString(ev, 32));
    
    Crypter.crypt(v, e, aliceCert.getN(), true);
  }
}
