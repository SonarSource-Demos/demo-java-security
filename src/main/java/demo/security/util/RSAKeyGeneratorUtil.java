package demo.security.util;

import java.math.BigInteger;
import java.util.Random;

public class RSAKeyGeneratorUtil {

    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private int bitLength;

    public RSAKeyGeneratorUtil(int bitLength) {
        this.bitLength = bitLength;
        generateKeys();
    }

    private void generateKeys() {
        Random rnd = new Random();
        // Generate two distinct random primes of bitLength/2 bits
        p = generatePrime(bitLength / 2, rnd);
        q = generatePrime(bitLength / 2, rnd);
        while (p.equals(q)) {
            q = generatePrime(bitLength / 2, rnd);
        }
        // Compute modulus n = p * q
        n = p.multiply(q);
        // Compute Euler's totient: phi(n) = (p-1) * (q-1)
        phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Choose public exponent e; 65537 is a common choice.
        e = new BigInteger("65537");
        // Make sure e and phi(n) are coprime; adjust e if necessary.
        while (!phi.gcd(e).equals(BigInteger.ONE)) {
            e = e.add(new BigInteger("2")); // try next odd number
        }

        // Calculate private exponent d, the modular inverse of e mod phi(n)
        d = e.modInverse(phi);
    }

    // Generates a probable prime number with the specified bit length.
    private BigInteger generatePrime(int bitLength, Random rnd) {
        BigInteger prime;
        do {
            // Create a random number with the desired bit length; force it to be odd.
            prime = new BigInteger(bitLength, rnd).setBit(0);
        } while (!prime.isProbablePrime(20)); // 20 rounds of testing for a decent probability
        return prime;
    }

    public BigInteger getModulus() {
        return n;
    }

    public BigInteger getPublicExponent() {
        return e;
    }

    public BigInteger getPrivateExponent() {
        return d;
    }

        public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getPhi() {
        return phi;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public static void main(String[] args) {
        int keySize = 1024; // Key size in bits (commonly 1024, 2048, or larger)
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(keySize);
        System.out.println("RSA Public Key:");
        System.out.println("Modulus (n): " + rsa.getModulus());
        System.out.println("Public Exponent (e): " + rsa.getPublicExponent());
        System.out.println("\nRSA Private Key:");
        System.out.println("Private Exponent (d): " + rsa.getPrivateExponent());
    }
}
