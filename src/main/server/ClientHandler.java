package main.server;

import java.io.*;
import java.net.Socket;

/**
 * Класс для обработки запросов от клиента в отдельном потоке.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Инициализация потоков ввода/вывода
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Получено от клиента: " + inputLine);

                // Обработка запроса (пример)
                String response = processRequest(inputLine);

                // Отправка ответа клиенту
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при обработке клиента: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    /**
     * Обрабатывает запрос от клиента и возвращает ответ.
     *
     * @param request Запрос от клиента.
     * @return Ответ сервера.
     */
    private String processRequest(String request) {
        // Пример обработки запроса
        if (request.equals("PING")) {
            return "PONG";
        } else if (request.startsWith("LOGIN")) {
            // Пример обработки логина
            String[] parts = request.split(" ");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];
                return "LOGIN_SUCCESS"; // Заглушка
            } else {
                return "LOGIN_FAILURE";
            }
        } else {
            return "UNKNOWN_COMMAND";
        }
    }
}