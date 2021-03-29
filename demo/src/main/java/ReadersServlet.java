import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "ReadersServlet", value = "/ReadersServlet")
public class ReadersServlet extends HttpServlet {

    public static String erase(String field, String val) {
        String sql = "DELETE FROM books WHERE " + field + " = '" + val + "';";
        return sql;
    }
//
//    public static String erase(String field, String val) {
//        String sql = "DELETE FROM books WHERE " + field + " = '" + val + "';";
//        return sql;
//    }


    public static String addReader (String name) {
        String sql = "INSERT INTO readers (name) VALUES('"+ name +  "');";
        return sql;
    }


    public static String addBook (String cipher, String title, String author) {
        String sql = "INSERT INTO books VALUES ('"+ cipher + "', '" + title + "', '" + author + "');";
        return sql;
    }


    public static String updateCipherBooks (String newVal, String conditionVal) {
        String sql = "UPDATE books SET cipher = '"+newVal+"' WHERE cipher = '"+conditionVal+"';";
        return sql;
    }

    public static String updateCipherBookReaders (String newVal, String conditionVal) {
        String sql = "UPDATE book_readers SET book_cipher = '"+newVal+"' WHERE book_cipher = '"+conditionVal+"';";
        return sql;
    }



    public static String selectBookByCipher (String cipher) {
        String sql = "SELECT * FROM books WHERE cipher = '" + cipher +"';" ;
        return sql;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        String getId = request.getParameter("id");
        String newReader = request.getParameter("add_reader");
        String newCipher = request.getParameter("add_cipher");
        String newTitle = request.getParameter("add_title");
        String newAuthor = request.getParameter("add_author");
        String cipher = request.getParameter("cipher");
        String erase = request.getParameter("erase");
        String field = request.getParameter("field");
        String newVal = request.getParameter("newVal");
        String conditionVal = request.getParameter("conditionVal");


        PrintWriter out = response.getWriter();
        out.println("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head><body>");

        String userName = "max";
        String password = "pass";
        String connectionURL = "jdbc:mysql://localhost:3306/maxlib";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        out.println("<h2>Читатели</h2>");


        out.println("<div style='float:left; padding:25px; BORDER: #777 1px solid;'>");
        out.println(" читатели<p>");
        try (Connection connection = DriverManager.getConnection(connectionURL, userName, password)) {

            Statement statement = connection.createStatement();
            if (newReader != null && !newReader.equals(""))
                statement.execute(addReader(newReader));
            ResultSet resultSet = statement.executeQuery("Select * from readers");
            String reader = "";
            while (resultSet.next()) {
                String readersId = resultSet.getString("id");

                out.println(readersId);
                if (readersId.equals(getId)) {
                    out.println("<b>");
                }
                out.println(" <a href=?id=" + readersId + ">");
                String name = resultSet.getString("name");
                out.println(name);
                out.println("</a><br>");
                if (readersId.equals(getId)) {
                    out.println("</b>");
                    reader = name;
                }
            }

            out.println("<p><form action=\"\">\n" +
                    "  <input id=\"GET-name\" type=\"text\" name=\"add_reader\">\n" +
                    "  <input type=\"submit\" value=\"Добавить\">\n" +
                    "</form>");




            String query_txt = "" +
                    "Select * " +
                    "from book_readers br, books b " +
                    "where br.book_cipher = b.cipher " +
                    "and br.reader_id = " + getId;

            ResultSet resBook_readers = statement.executeQuery(query_txt);

            out.println("</div><div style='float:left; padding:25px; BORDER: #777 1px solid;'>");
            out.println("книги закреплены за читателем " + reader + ":<p>");

            while (resBook_readers.next()) {
                out.println(resBook_readers.getString("br.book_cipher") + " ");
                out.println(resBook_readers.getString("b.title") + " ");
                out.println(resBook_readers.getString("b.author") + " ");
                out.println("</br>");
            }


            out.println("</div>");
            out.println("<div style='clear: both;'>&nbsp;</div>");
            out.println("<h2>Книжки</h2>");

            out.println("найти книгу по шифру");
            out.println("<p><form action=\"\">\n" +
                    "  <input id=\"GET-cipher\" type=\"text\" name=\"cipher\">\n" +
                    "  <input type=\"submit\" value=\"Найти\">\n" +
                    "</form>");

            if (cipher != null && !cipher.equals("")) {
                ResultSet titAuth = statement.executeQuery(selectBookByCipher(cipher));
                while (titAuth.next()) {
                    out.println(titAuth.getString("title") + " " + titAuth.getString("author") + "<br>");
                }
            }
            out.println("<p>добавить новую книгу");

            out.println("<p><form action=\"\">\n" +
                    "  <label for=\"GET-add_cipher\">шифр:</label>\n" +
                    "  <input id=\"GET-add_cipher\" type=\"text\" name=\"add_cipher\">\n" +
                    "  <label for=\"GET-add_title\">название:</label>\n" +
                    "  <input id=\"GET-add_title\" type=\"text\" name=\"add_title\">\n" +
                    "  <label for=\"GET-add_author\">автор:</label>\n" +
                    "  <input id=\"GET-add_author\" type=\"text\" name=\"add_author\">\n" +
                    "  <input type=\"submit\" value=\"Добавить\">\n" +
                    "</form>");


            if (newCipher != null && !newCipher.equals("") && newTitle != null && !newTitle.equals("") && newAuthor != null && !newAuthor.equals("")) {
                statement.execute(addBook(newCipher, newTitle, newAuthor));
            }

            out.println("<p>списать старую книгу");
            out.println("<p><form action=\"\">\n" +
                    "<label for=\"GET-field\">идентификатор:</label>\n" +
                    "  <input id=\"GET-field\" type=\"text\" name=\"field\">\n" +
                    "<label for=\"GET-erase\">значение:</label>\n" +
                    "  <input id=\"GET-erase\" type=\"text\" name=\"erase\">\n" +
                    "  <input type=\"submit\" value=\"Списать\">\n" +
                    "</form>");

            if (field != null && !field.equals("") && erase != null && !erase.equals("")) {
                statement.execute(erase( field, erase));
            }


            out.println("<p> изменить шифр книги");
            out.println("<p><form action=\"\">\n" +
                    "<label for=\"GET-conditionVal\">старый:</label>\n" +
                    "  <input id=\"GET-conditionVal\" type=\"text\" name=\"conditionVal\">\n" +
                    "<label for=\"GET-newVal\">новый:</label>\n" +
                    "  <input id=\"GET-newVal\" type=\"text\" name=\"newVal\">\n" +
                    "  <input type=\"submit\" value=\"Изменить\">\n" +
                    "</form>");


            if (newVal != null && !newVal.equals("") && conditionVal != null && !conditionVal.equals("")) {
                statement.execute(updateCipherBooks(newVal, conditionVal));
                statement.execute(updateCipherBookReaders(newVal, conditionVal));
            }


            out.println("</body></html>");
            connection.close();
            //           out.println("We're connected!!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
