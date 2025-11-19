package demo.security.util;

public class ContactFeedbackException extends Exception {
    
    public ContactFeedbackException(String message) {
        super(message);
    }
    
    public ContactFeedbackException(String message, Throwable cause) {
        super(message, cause);
    }
}

