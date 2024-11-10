import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleHttpServer {
    private static final int PORT = 8088;
    private static List<Item> items = new ArrayList<>(Arrays.asList(
            new Item("Товар1", 100),
            new Item("Товар2", 200),
            new Item("Товар3", 300),
            new Item("Товар4", 400),
            new Item("Товар5", 500)));

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущено на порту " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine = in.readLine();
            if (requestLine == null)
                return;

            System.out.println("Запит: " + requestLine);

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            if (method.equals("GET") && path.equals("/home")) {
                sendHomePage(out);
            } else if (method.equals("GET") && path.equals("/about")) {
                sendAboutPage(out);
            } else if (method.equals("GET") && path.startsWith("/items")) {
                handleItemsRequest(path, out);
            } else {
                sendNotFoundResponse(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendHomePage(PrintWriter out) {
        String body = "<html><body><h1>Домашня сторінка</h1></body></html>";
        sendResponse(out, 200, "text/html", body);
    }

    private static void sendAboutPage(PrintWriter out) {
        String body = "<html><body><h1>Про мене</h1><p>Прізвище: Глух</p><p>Група: ПС 4-1</p><p>Улюблений фільм: Ранго</p><p>Улюблена пісня: Fade to Black</p></body></html>";
        sendResponse(out, 200, "text/html", body);
    }

    private static void sendNotFoundResponse(PrintWriter out) {
        String body = "<html><body><h1>404 Not Found</h1></body></html>";
        sendResponse(out, 404, "text/html", body);
    }

 private static void sendResponse(PrintWriter out, int statusCode, String contentType, String body) {
    System.out.println("Status Code: " + statusCode + ", Content-Type: " + contentType);

    out.println("HTTP/1.1 " + statusCode + " OK");
    out.println("Content-Type: " + contentType + "; charset=UTF-8");
    out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
    out.println();
    out.println(body);
}


    private static void handleItemsRequest(String path, PrintWriter out) {
        String body = "<html><body><h1>Список товарів</h1><ul>";
        for (Item item : items) {
            body += "<li>" + item.getName() + ": " + item.getPrice() + "</li>";
        }
        body += "</ul></body></html>";
        sendResponse(out, 200, "text/html; charset=UTF-8", body);
    }

    static class Item {
        private String name;
        private int price;

        public Item(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }
    }
}
