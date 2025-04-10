package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String encryptedData;

    public HomeServlet() {
        super();
        try {
            // Generate RSA key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing RSA key pair", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            if (encryptedData != null) {
                // Decrypt the encrypted data
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
                String decryptedData = new String(decryptedBytes);

                out.print("<h2>Decrypted Data: " + decryptedData + "</h2>");
            } else {
                out.print("<h2>No data to decrypt</h2>");
            }
        } catch (Exception e) {
            out.print("<h2>Error during decryption: " + e.getMessage() + "</h2>");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            String plaintext = request.getParameter("plaintext");
            if (plaintext != null && !plaintext.isEmpty()) {
                // Encrypt the plaintext
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
                encryptedData = Base64.getEncoder().encodeToString(encryptedBytes);

                out.print("<h2>Data encrypted successfully</h2>");
            } else {
                out.print("<h2>Invalid plaintext input</h2>");
            }
        } catch (Exception e) {
            out.print("<h2>Error during encryption: " + e.getMessage() + "</h2>");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
