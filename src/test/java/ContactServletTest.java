import demo.security.servlet.ContactServlet;
import demo.security.util.WebUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContactServletTest {

    @Test
    public void testContactServletInstantiation() {
        ContactServlet servlet = new ContactServlet();
        assertNotNull(servlet);
    }

    @Test
    public void testResolveAttachmentPath() {
        String filename = "test.txt";
        String path = WebUtils.resolveAttachmentPath(filename);
        assertNotNull(path);
        assertTrue(path.contains(filename));
    }

    @Test
    public void testResolveAttachmentPathWithEmptyString() {
        String filename = "";
        String path = WebUtils.resolveAttachmentPath(filename);
        assertNotNull(path);
    }
}

