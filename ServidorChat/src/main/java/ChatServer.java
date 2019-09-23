/*
 * Copyright (c) 2019. This code is purely educational, the rights of use are
 * reserved, the owner of the code is Martin Osorio,
 * contact mob010@alumnos.ucn.cl
 * Do not use in production.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Chat Server class.
 *
 * @author Martin Osorio.
 */
public final class ChatServer {

    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    private static final int PORT = 9000;

    private static List<ChatMessage> messages = new LinkedList<ChatMessage>();

    /**
     * The main class.
     * initializing chat server.
     */

    public static void main(final String[] args) throws IOException {

        log.debug("Starting the Main ..");

        // The Server Socket
        final ServerSocket serverSocket = new ServerSocket(PORT);

        // serverSocket.setReuseAddress(true);
        log.debug("start in port {} ...", PORT);

        while (true) {

            // One socket by request (try with resources).
            try (final Socket socket = serverSocket.accept()) {

                // The remote connection address.
                final InetAddress address = socket.getInetAddress();

                log.debug("========================================================================================");
                log.debug("Connection from {} in port {}.", address.getHostAddress(), socket.getPort());
                processConnection(socket);

            } catch (IOException e) {
                log.error("Error", e);
                throw e;
            }

        }

    }

    /**
     * Process the connection.
     *
     * @param socket to use as source of data.
     */

    private static void processConnection(final Socket socket) throws IOException {

        final List<String> lines = readSocketInput(socket);
        final String request = lines.get(0);
        log.debug("Request: {}", request);

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());


        if (request.contains("GET")) {

            log.debug("GET REQUEST");
            pw.println("HTTP/1.1 200");
            pw.println("Server: DSM v0.0.1");
            pw.println("Date: " + new Date());
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println();
            pw.println(generateHtml());
            pw.flush();

        } else if (request.contains("POST")) {
            log.debug("POST REQUEST");

            log.debug("Input body readed");

            for (int i = 0; i < lines.size(); i++) {
                log.debug("***** " + lines.get(i));
            }

            if (addMessage(lines)) {

                pw.println("HTTP/1.1 200");
                pw.println("Server: DSM v0.0.1");
                pw.println("Date: " + new Date());
                pw.println("Content-Type: text/html; charset=UTF-8");
                pw.println();
                pw.println(generateHtml());
                pw.flush();

                log.debug("MESSAGE ADDED");

            } else {

                pw.println("HTTP/1.1 400 ERROR");
                pw.println("Server: DSM v0.0.1");
                pw.println();
                pw.flush();

            }

        } else {
            log.debug("ERROR REQUEST");
            pw.println("HTTP/1.1 400 ERROR");
            pw.println("Server: ChatServer");
            pw.println();
            pw.flush();
        }


        log.debug("Process ended.");

    }

    /**
     * Read all the input stream.
     *
     * @param socket to use to read.
     * @return all the string readed.
     */
    private static List<String> readInputStreamByLines(final Socket socket) throws IOException {

        final InputStream is = socket.getInputStream();

        // The list of string readed from inputstream.
        final List<String> lines = new ArrayList<>();

        // The Scanner
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        log.debug("Reading the Inputstream ..");


        while (true) {


            final String line = s.nextLine();

            if (line.length() == 0) {
                break;
            } else {
                lines.add(line);
            }
        }
        return lines;

    }

    /**
     * This method read the input stream from a socket.
     *
     * @param socket : The socket to be readed.
     * @return : A List<String> with the lines readed.
     * @throws IOException .
     */

    public static List<String> readSocketInput(Socket socket) throws IOException {

        List<String> input = new ArrayList<String>();
        InputStream is = socket.getInputStream();
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        String line = "";

        while (true) {

            line = bf.readLine();

            boolean isOpen = true;

            try {
                isOpen = bf.ready();
            } catch (Exception e) {
                isOpen = false;
            }

            if ((line == null || line.isEmpty()) && !isOpen) {

                log.debug(" * LINE:" + line + " BF STATUS" + bf.ready());
                break;

            } else if (line.isEmpty() && isOpen) {


                int contentLength = 0;

                for (String s : input) {
                    if (s.contains("Content-Length:")) {
                        contentLength = Integer.parseInt(s.substring(16));
                    }
                }

                log.debug("CONTENT LENGTH: " + contentLength);


                char[] chars = new char[contentLength];

                for (int i = 0; i < contentLength; i++) {
                    chars[i] = (char) bf.read();

                }

                input.add(new String(chars));


                log.debug("CLOSING CONNECTION");
                break;

            } else {
                log.debug("LINE:" + line + " BF STATUS" + bf.ready());
                input.add(line);
            }

        }

        if (input.isEmpty()) {
            input.add("ERROR");
        }
        return input;
    }

    public static boolean addMessage(List<String> input) {

        if (input.isEmpty()) {
            log.error("Error Message ");
            return false;
        }

        String bodyContent = input.get(input.size() - 1);
        bodyContent = bodyContent.replace("username=", "");
        bodyContent = bodyContent.replace("message=", "");

        String username = bodyContent.substring(0, bodyContent.indexOf('&'));
        String message = bodyContent.substring(bodyContent.indexOf('&') + 1, bodyContent.length());

        message = message.replace('+', ' ');


        log.debug("USERNAME: " + username + " MESSAGE: " + message);

        ChatMessage newMessage = new ChatMessage(username, message);
        messages.add(newMessage);

        return true;
    }

    /**
     * @return Html5 code
     * @throws IOException
     */

    public static String generateHtml() throws IOException {

        String html =
                "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>ChatServer</title>\n" +
                        "<link href=\"https://stackpath.bootstrapcdn.com/bootswatch/4.3.1/superhero/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-LS4/wo5Z/8SLpOLHs0IbuPAGOWTx30XSoZJ8o7WKH0UJhRpjXXTpODOjfVnNjeHu\" crossorigin=\"anonymous\">"+
                        "    <style>\n" +
                        "        body{\n" +
                        "        }\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div><h3 align=justify>ChatServer</h3></div>\n" +
                        "<div class=\"container\">\n" +
                        "    <div class=\"col-lg-6 col-lg-offset-3\">\n" +
                        "        <div id=\"chat-window\" class=\"card card-body \">\n";
        for (ChatMessage i : messages) {
            html += "<p>" + i.toString() + "</p>";
        }

        html += "</div>\n" +
                "        <div id=\"chat-input\" class=\"form-group card-header \">\n" +
                "            <form action=\"/\" method=\"post\">\n" +
                "                <input type=\"text\" name=\"username\" class=\"form-control-sm\" placeholder=\"Username\">\n" +
                "                <input type=\"text\" name=\"message\" class=\"form-control-sm\" placeHolder=\"Type your message...\">\n" +
                "                <input type=\"submit\" value=\"Send\" class=\"btn btn-success\"></button>\n" +
                "            </form>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";

        return html;


    }
}
