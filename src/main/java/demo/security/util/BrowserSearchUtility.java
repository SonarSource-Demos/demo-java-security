package demo.security.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserSearchUtility {  

    public static void main(String[] args) {
        String query = "sonarsource";
        searchWeb(query);
    }

    public static void searchWeb(String query) {
        String url = "https://www.google.com/search?q=" + query;
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Desktop is not supported. Cannot open the browser.");
        }
    }
}