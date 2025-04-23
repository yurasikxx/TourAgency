package server;

import server.database.DAO.impl.BookingDAOImpl;
import server.database.DAO.impl.DestinationDAOImpl;
import server.database.DAO.impl.PaymentDAOImpl;
import server.database.DAO.impl.ReviewDAOImpl;
import server.database.DAO.impl.TourDAOImpl;
import server.database.DAO.impl.UserDAOImpl;
import server.services.BookingService;
import server.services.DestinationService;
import server.services.PaymentService;
import server.services.ReviewService;
import server.services.TourService;
import server.services.UserService;
import server.services.impl.BookingServiceImpl;
import server.services.impl.DestinationServiceImpl;
import server.services.impl.PaymentServiceImpl;
import server.services.impl.ReviewServiceImpl;
import server.services.impl.TourServiceImpl;
import server.services.impl.UserServiceImpl;
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

    private final UserService userService;
    private final TourService tourService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final DestinationService destinationService;
    private final ReviewService reviewService;

    public MainServer() {
        this.userService = new UserServiceImpl(new UserDAOImpl());
        this.tourService = new TourServiceImpl(new TourDAOImpl());
        this.bookingService = new BookingServiceImpl(new BookingDAOImpl());
        this.paymentService = new PaymentServiceImpl(new PaymentDAOImpl());
        this.destinationService = new DestinationServiceImpl(new DestinationDAOImpl());
        this.reviewService = new ReviewServiceImpl(new ReviewDAOImpl());
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

                ClientHandler clientHandler = new ClientHandler(
                        clientSocket, userService, tourService, bookingService,
                        paymentService, destinationService, reviewService
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