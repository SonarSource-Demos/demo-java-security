import java.util.logging.Logger;

public class Secrets {
    private static Logger logger = Logger.getLogger(Secrets.class.getName());
    public static void main(String[] args) {
        String password = "MyCustomSecret_123";
        logger.println(password);
    }
    
}
