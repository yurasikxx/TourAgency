package main.server;

import main.server.utils.ConfigLoader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс сервера, который запускает сервер и обрабатывает подключения клиентов.
 */
public class MainServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning;

    /**
     * Запускает сервер на указанном порту.
     *
     * @param port Порт, на котором будет запущен сервер.
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newCachedThreadPool();
            isRunning = true;
            System.out.println("Сервер запущен на порту " + port);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое подключение: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Останавливает сервер.
     */
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            System.out.println("Сервер остановлен.");
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();
        int port = configLoader.getIntProperty("server.port", 12345);

        MainServer server = new MainServer();
        server.start(port);
    }
}