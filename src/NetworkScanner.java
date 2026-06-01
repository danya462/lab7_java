import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class NetworkScanner {
    private static final int DEFAULT_PORT = 12345;
    private static final int DEFAULT_TIMEOUT_MS = 500;
    private static final int DEFAULT_START_HOST = 1;
    private static final int DEFAULT_END_HOST = 254;
    private static final String DEFAULT_TEST_MESSAGE = "ping test";

    public static void main(String[] args) {
        String baseIp = args.length >= 1 ? args[0] : detectBaseIp();
        int port = args.length >= 2 ? parseInt(args[1], DEFAULT_PORT, "порт") : DEFAULT_PORT;
        int timeoutMs = args.length >= 3 ? parseInt(args[2], DEFAULT_TIMEOUT_MS, "таймаут") : DEFAULT_TIMEOUT_MS;

        if (baseIp == null) {
            System.err.println("Не удалось определить подсеть автоматически. Укажите префикс вручную, например 192.168.1.");
            return;
        }

        String myIp = getMyIp();
        System.out.printf("Сканируем сеть %s* по порту %d (таймаут %d мс)%n", baseIp, port, timeoutMs);

        for (int i = DEFAULT_START_HOST; i <= DEFAULT_END_HOST; i++) {
            String ip = baseIp + i;
            if (ip.equals(myIp)) {
                continue;
            }

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ip, port), timeoutMs);

                try (
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                        );
                        PrintWriter out = new PrintWriter(
                                new BufferedWriter(
                                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                                ),
                                true
                        )
                ) {
                    out.println(DEFAULT_TEST_MESSAGE);
                    String response = in.readLine();
                    System.out.printf("Найден сервер: %s | Ответ: %s%n", ip, response);
                }
            } catch (IOException ignored) {
                // Хост не отвечает на указанном порту — это нормальная ситуация при сканировании.
            }
        }
    }

    private static int parseInt(String value, int defaultValue, String label) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new IllegalArgumentException(label + " должен быть положительным числом");
            }
            return parsed;
        } catch (NumberFormatException e) {
            System.err.printf("Некорректный %s \"%s\". Используется значение по умолчанию: %d%n", label, value, defaultValue);
            return defaultValue;
        }
    }

    private static String detectBaseIp() {
        String myIp = getMyIp();
        if (myIp == null || !myIp.contains(".")) {
            return null;
        }

        int lastDotIndex = myIp.lastIndexOf('.') + 1;
        return myIp.substring(0, lastDotIndex);
    }

    private static String getMyIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Не удалось получить IP-адрес: " + e.getMessage());
        }
        return null;
    }
}
