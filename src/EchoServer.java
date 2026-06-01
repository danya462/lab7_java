import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class EchoServer {
    private static final int DEFAULT_PORT = 12345;
    private static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";

    public static void main(String[] args) {
        int port;
        try {
            port = parsePort(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка аргументов: " + e.getMessage());
            printUsage();
            return;
        }

        String bindAddress = args.length >= 2 ? args[1] : DEFAULT_BIND_ADDRESS;

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(bindAddress, port));

            System.out.printf(
                    "Сервер запущен. Адрес: %s, порт: %d, кодировка: %s%n",
                    bindAddress,
                    port,
                    StandardCharsets.UTF_8.displayName()
            );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключился клиент: " + clientSocket.getRemoteSocketAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Ошибка в работе сервера: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Использование: java EchoServer [port] [bindAddress]");
    }

    private static int parsePort(String[] args) {
        if (args.length == 0) {
            return DEFAULT_PORT;
        }

        try {
            int port = Integer.parseInt(args[0]);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Порт должен быть в диапазоне 1..65535");
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Порт должен быть целым числом", e);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                clientSocket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
                );
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8)
                        ),
                        true
                )
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Получено: " + inputLine);
                String response = removeWhitespace(inputLine);
                out.println(response);
                System.out.println("Отправлено: " + response);
            }

            System.out.println("Клиент отключился: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Ошибка при обработке клиента: " + e.getMessage());
        }
    }

    private static String removeWhitespace(String input) {
        return input.replaceAll("\\s+", "");
    }
}
