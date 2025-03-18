package server;

import server.services.AuthService;
import server.services.TourService;
import server.services.BookingService;
import server.services.PaymentService;
import server.services.impl.AuthServiceImpl;
import server.services.impl.TourServiceImpl;
import server.services.impl.BookingServiceImpl;
import server.services.impl.PaymentServiceImpl;
import server.database.DAO.impl.UserDAOImpl;
import server.database.DAO.impl.TourDAOImpl;
import server.database.DAO.impl.BookingDAOImpl;
import server.database.DAO.impl.PaymentDAOImpl;
import server.utils.ConfigLoader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning;

    private AuthService authService;
    private TourService tourService;
    private BookingService bookingService;
    private PaymentService paymentService;

    public MainServer() {
        // Инициализация сервисов
        this.authService = new AuthServiceImpl(new UserDAOImpl());
        this.tourService = new TourServiceImpl(new TourDAOImpl());
        this.bookingService = new BookingServiceImpl(new BookingDAOImpl());
        this.paymentService = new PaymentServiceImpl(new PaymentDAOImpl());
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newCachedThreadPool();
            isRunning = true;
            System.out.println("Сервер запущен на порту " + port);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое подключение: " + clientSocket.getInetAddress());

                // Создаем ClientHandler с передачей сервисов
                ClientHandler clientHandler = new ClientHandler(
                        clientSocket, authService, tourService, bookingService, paymentService
                );
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        } finally {
            stop();
        }
    }

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