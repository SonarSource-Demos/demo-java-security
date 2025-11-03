package demo.security.servlet;

import demo.security.util.DBUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Giant method with lots of code duplication and deep nesting - maintainability issue
        if (action != null) {
            if (action.equals("search")) {
                String productId = request.getParameter("id");
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(productId);
                    out.print("<html><body><h1>Search Results</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("list")) {
                String category = request.getParameter("category");
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(category);
                    out.print("<html><body><h1>Product List</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("details")) {
                String productId = request.getParameter("id");
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(productId);
                    out.print("<html><body><h1>Product Details</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("featured")) {
                String featured = "featured";
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(featured);
                    out.print("<html><body><h1>Featured Products</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                out.print("<html><body><h1>Unknown action</h1></body></html>");
                out.close();
            }
        } else {
            out.print("<html><body><h1>No action specified</h1></body></html>");
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // More duplicated code - same maintainability issue
        if (action != null) {
            if (action.equals("search")) {
                String productId = request.getParameter("id");
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(productId);
                    out.print("<html><body><h1>Search Results</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("list")) {
                String category = request.getParameter("category");
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(category);
                    out.print("<html><body><h1>Product List</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("details")) {
                String productId = request.getParameter("id");
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(productId);
                    out.print("<html><body><h1>Product Details</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("featured")) {
                String featured = "featured";
                try {
                    DBUtils db = new DBUtils();
                    List<String> products = db.findItem(featured);
                    out.print("<html><body><h1>Featured Products</h1>");
                    products.forEach((result) -> {
                        out.print("<div class='product'>");
                        out.print("<h2>Product " + result + "</h2>");
                        out.print("<p>Description for " + result + "</p>");
                        out.print("<p>Price: $99.99</p>");
                        out.print("</div>");
                    });
                    out.print("</body></html>");
                    out.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                out.print("<html><body><h1>Unknown action</h1></body></html>");
                out.close();
            }
        } else {
            out.print("<html><body><h1>No action specified</h1></body></html>");
            out.close();
        }
    }
}

