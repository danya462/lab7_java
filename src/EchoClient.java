import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EchoClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        String host = args.length >= 1 ? args[0] : DEFAULT_HOST;
        int port;
        try {
            port = args.length >= 2 ? parsePort(args[1]) : DEFAULT_PORT;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка аргументов: " + e.getMessage());
            printUsage();
            return;
        }

        String[] testMessages = args.length > 2 ? Arrays.copyOfRange(args, 2, args.length) : new String[0];

        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                        ),
                        true
                );
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8)
                )
        ) {
            System.out.printf("Подключено к %s:%d%n", host, port);

            if (testMessages.length > 0) {
                runAutomaticTest(testMessages, in, out);
            } else {
                runInteractiveMode(stdIn, in, out);
            }
        } catch (UnknownHostException e) {
            System.err.println("Неизвестный хост: " + host);
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Использование: java EchoClient [host] [port] [message1 message2 ...]");
    }

    private static int parsePort(String portValue) {
        try {
            int port = Integer.parseInt(portValue);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Порт должен быть в диапазоне 1..65535");
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Порт должен быть целым числом", e);
        }
    }

    private static void runAutomaticTest(String[] testMessages, BufferedReader in, PrintWriter out)
            throws IOException {
        System.out.println("Автоматическая отправка тестовых строк:");
        for (String message : testMessages) {
            out.println(message);
            String response = in.readLine();
            System.out.printf("Клиент -> \"%s\" | Сервер -> \"%s\"%n", message, response);
        }
    }

    private static void runInteractiveMode(BufferedReader stdIn, BufferedReader in, PrintWriter out)
            throws IOException {
        System.out.println("Введите строки. Для завершения используйте Ctrl+Z и Enter.");
        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);
        }
    }
}
