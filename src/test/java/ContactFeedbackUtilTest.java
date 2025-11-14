import demo.security.util.ContactFeedbackUtil;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ContactFeedbackUtilTest {

    @Test
    public void testIsValidEmail() {
        assertTrue(ContactFeedbackUtil.isValidEmail("user@example.com"));
        assertTrue(ContactFeedbackUtil.isValidEmail("test@test.com"));
        assertFalse(ContactFeedbackUtil.isValidEmail("invalid"));
        assertFalse(ContactFeedbackUtil.isValidEmail(null));
    }

    @Test
    public void testSanitizeFilename() {
        String result = ContactFeedbackUtil.sanitizeFilename("test/file.txt");
        assertEquals("testfile.txt", result);
        
        result = ContactFeedbackUtil.sanitizeFilename("test\\file.txt");
        assertEquals("testfile.txt", result);
    }

    @Test
    public void testHashData() {
        String hash1 = ContactFeedbackUtil.hashData("password123");
        String hash2 = ContactFeedbackUtil.hashData("password123");
        
        // Same input should produce same hash
        assertEquals(hash1, hash2);
        
        // Hash should not be empty
        assertNotNull(hash1);
        assertTrue(hash1.length() > 0);
    }

    @Test
    public void testIsAdmin() {
        assertTrue(ContactFeedbackUtil.isAdmin("admin", "admin123"));
        assertFalse(ContactFeedbackUtil.isAdmin("user", "password"));
        assertFalse(ContactFeedbackUtil.isAdmin("admin", "wrongpass"));
    }

    @Test
    public void testBuildRedirectUrl() {
        String url = ContactFeedbackUtil.buildRedirectUrl("https://example.com", "/dashboard");
        assertEquals("https://example.com?redirect=/dashboard", url);
    }

    @Test
    public void testVerifyFeedbackToken() {
        assertTrue(ContactFeedbackUtil.verifyFeedbackToken("token123", "token123"));
        assertFalse(ContactFeedbackUtil.verifyFeedbackToken("token123", "token456"));
    }

    @Test
    public void testEncryptSensitiveData() {
        String encrypted = ContactFeedbackUtil.encryptSensitiveData("secret");
        assertNotNull(encrypted);
        assertNotEquals("secret", encrypted);
        
        // Encrypting twice should give same result
        String encrypted2 = ContactFeedbackUtil.encryptSensitiveData("secret");
        assertEquals(encrypted, encrypted2);
    }

    @Test
    public void testCreateTempFeedbackFile() throws IOException {
        File tempFile = ContactFeedbackUtil.createTempFeedbackFile("test");
        assertNotNull(tempFile);
        assertTrue(tempFile.getName().startsWith("test"));
        assertTrue(tempFile.getName().endsWith(".tmp"));
        
        // Clean up
        if (tempFile.getParentFile().exists()) {
            tempFile.getParentFile().delete();
        }
    }
}

