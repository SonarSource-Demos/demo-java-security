import demo.security.util.RSAKeyGeneratorUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class RSAKeyGeneratorUtilTest {

    // Use a key size that is reasonable for testing purposes
    private final int KEY_SIZE = 1024;

    @Test
    public void testModulusCalculation() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        BigInteger expectedModulus = rsa.getP().multiply(rsa.getQ());
        assertEquals(expectedModulus, rsa.getN(), "Modulus (n) should equal p * q.");
    }

    @Test
    public void testPhiCalculation() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        BigInteger expectedPhi = (rsa.getP().subtract(BigInteger.ONE))
                                   .multiply(rsa.getQ().subtract(BigInteger.ONE));
        assertEquals(expectedPhi, rsa.getPhi(), "Phi should be calculated as (p-1) * (q-1).");
    }

    @Test
    public void testPublicExponentCoprimeWithPhi() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        BigInteger gcd = rsa.getPhi().gcd(rsa.getE());
        assertEquals(BigInteger.ONE, gcd, "Public exponent e must be coprime with phi.");
    }

    @Test
    public void testPrivateExponentCalculation() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        // Check that (e * d) mod phi == 1
        BigInteger productModPhi = rsa.getE().multiply(rsa.getD()).mod(rsa.getPhi());
        assertEquals(BigInteger.ONE, productModPhi, "e * d mod phi should equal 1.");
    }

    @Test
    public void testPrimalityOfPAndQ() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        assertTrue(rsa.getP().isProbablePrime(20), "p should be a probable prime.");
        assertTrue(rsa.getQ().isProbablePrime(20), "q should be a probable prime.");
    }

    @Test
    public void testDistinctPrimes() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        assertNotEquals(rsa.getP(), rsa.getQ(), "p and q should be distinct primes.");
    }

    @Test
    public void testKeyValuesNotNull() {
        RSAKeyGeneratorUtil rsa = new RSAKeyGeneratorUtil(KEY_SIZE);
        assertNotNull(rsa.getN(), "Modulus (n) should not be null.");
        assertNotNull(rsa.getE(), "Public exponent (e) should not be null.");
        assertNotNull(rsa.getD(), "Private exponent (d) should not be null.");
    }
}
