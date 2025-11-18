import demo.security.util.ContactFeedbackUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContactFeedbackUtilTest {
    
    private ContactFeedbackUtil contactFeedbackUtil;
    
    @Before
    public void setUp() throws Exception {
        contactFeedbackUtil = new ContactFeedbackUtil();
    }
    
    @Test
    public void testGenerateToken() {
        String token = contactFeedbackUtil.generateToken();
        assertNotNull("Token should not be null", token);
        assertFalse("Token should not be empty", token.isEmpty());
    }
    
    @Test
    public void testHashEmail() throws Exception {
        String email = "test@example.com";
        String hash = contactFeedbackUtil.hashEmail(email);
        assertNotNull("Hash should not be null", hash);
        assertEquals("Hash should be 32 characters (MD5)", 32, hash.length());
    }
    
    @Test
    public void testEncryptSensitiveData() throws Exception {
        String data = "sensitive data";
        String encrypted = contactFeedbackUtil.encryptSensitiveData(data);
        assertNotNull("Encrypted data should not be null", encrypted);
        assertFalse("Encrypted data should not be empty", encrypted.isEmpty());
    }
    
    @Test
    public void testAuthenticateAdmin() {
        assertTrue("Should authenticate with correct credentials",
                contactFeedbackUtil.authenticateAdmin("admin", "admin123"));
        assertFalse("Should not authenticate with wrong credentials",
                contactFeedbackUtil.authenticateAdmin("admin", "wrongpass"));
    }
    
    @Test
    public void testExecuteSystemCommand() throws Exception {
        String result = contactFeedbackUtil.executeSystemCommand("test");
        assertEquals("Should return success message", "Command executed", result);
    }
}

