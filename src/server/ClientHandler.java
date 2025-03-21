package server;

import server.models.*;
import server.services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private UserService userService;
    private TourService tourService;
    private BookingService bookingService;
    private PaymentService paymentService;
    private DestinationService destinationService;

    public ClientHandler(Socket socket, UserService authService, TourService tourService,
                         BookingService bookingService, PaymentService paymentService,
                         DestinationService destinationService) {
        this.clientSocket = socket;
        this.userService = authService;
        this.tourService = tourService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.destinationService = destinationService;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Получено от клиента: " + inputLine);

                // Обработка запроса
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
        String[] parts = request.split(" ");
        if (parts.length == 0) {
            return "ERROR: Empty request";
        }

        String command = parts[0];
        switch (command) {
            case "LOGIN":
                return handleLogin(parts);
            case "REGISTER":
                return handleRegister(parts);
            case "GET_TOURS":
                return handleGetTours();
            case "BOOK_TOUR":
                return handleBookTour(parts);
            case "GET_BOOKINGS":
                return handleGetBookings(parts);
            case "GET_TOUR_PRICE":
                return handleGetTourPrice(parts);
            case "GET_PAYMENTS":
                return handleGetPayments(parts);
            case "CANCEL_BOOKING":
                return handleCancelBooking(parts);
            case "MAKE_PAYMENT":
                return handleMakePayment(parts);
            case "GET_DESTINATIONS":
                return handleGetDestinations();
            case "GET_TOURS_BY_DESTINATION":
                return handleGetToursByDestination(parts);
            case "GET_BALANCE":
                return handleGetBalance(parts);
            default:
                return "ERROR: Unknown command";
        }
    }

    /**
     * Обрабатывает запрос на авторизацию.
     *
     * @param parts Массив строк, содержащий команду, логин и пароль.
     * @return Ответ сервера.
     */
    private String handleLogin(String[] parts) {
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            User user = userService.authenticate(username, password);
            if (user != null) {
                return "LOGIN_SUCCESS " + user.getRole() + " " + user.getId(); // Возвращаем роль и ID
            }
        }
        return "LOGIN_FAILURE";
    }

    private String handleRegister(String[] parts) {
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            // Проверка, существует ли пользователь с таким логином
            if (userService.getUserByUsername(username) != null) {
                return "REGISTER_FAILURE: Пользователь с таким логином уже существует.";
            }

            // Создание нового пользователя
            User newUser = new User(username, password, "USER", 1000);
            userService.register(newUser);
            return "REGISTER_SUCCESS";
        }
        return "REGISTER_FAILURE: Неверный запрос.";
    }

    /**
     * Обрабатывает запрос на получение списка туров.
     *
     * @return Строка с данными о турах.
     */
    private String handleGetTours() {
        List<Tour> tours = tourService.getAllTours();
        StringBuilder response = new StringBuilder("TOURS ");
        for (Tour tour : tours) {
            response.append(tour.getId()).append(",")
                    .append(tour.getName()).append(",")
                    .append(tour.getDescription()).append(",")
                    .append(tour.getPrice()).append(",")
                    .append(tour.getStartDate()).append(",")
                    .append(tour.getEndDate()).append(",")
                    .append(tour.getDestinationId()).append("|");
        }
        return response.toString();
    }

    /**
     * Обрабатывает запрос на бронирование тура.
     *
     * @param parts Массив строк, содержащий команду, ID пользователя, ID тура и дату бронирования.
     * @return Ответ сервера.
     */
    private String handleBookTour(String[] parts) {
        if (parts.length == 4) {
            try {
                int userId = Integer.parseInt(parts[1]);
                int tourId = Integer.parseInt(parts[2]);
                String bookingDate = parts[3]; // Текущая дата, переданная с клиента

                // Создаем объект Booking с текущей датой
                Booking booking = new Booking(0, userId, tourId, bookingDate, "pending"); // Статус по умолчанию: "pending"
                bookingService.addBooking(booking);
                return "BOOKING_SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR: Invalid user ID or tour ID";
            }
        }
        return "ERROR: Invalid booking request";
    }

    /**
     * Обрабатывает запрос на получение списка бронирований пользователя.
     *
     * @param parts Массив строк, содержащий команду и ID пользователя.
     * @return Строка с данными о бронированиях.
     */
    private String handleGetBookings(String[] parts) {
        if (parts.length == 2) {
            try {
                int userId = Integer.parseInt(parts[1]); // ID текущего пользователя
                List<Booking> bookings = bookingService.getBookingsByUserId(userId);
                StringBuilder response = new StringBuilder("BOOKINGS ");
                for (Booking booking : bookings) {
                    // Получаем тур по ID
                    Tour tour = tourService.getTourById(booking.getTourId());
                    if (tour != null) {
                        String status = "";
                        if (booking.getStatus().equals("confirmed")) {
                            status = "Подтверждено";
                        } else if (booking.getStatus().equals("cancelled")) {
                            status = "Отменено";
                        } else {
                            status = "В ожидании";
                        }

                        // Добавляем данные о бронировании и туре в ответ
                        response.append(booking.getId()).append(",")
                                .append(tour.getName()).append(",") // Название тура
                                .append(booking.getBookingDate()).append(",")
                                .append(tour.getPrice()).append(",") // Стоимость тура
                                .append(status).append("|"); // Статус на русском
                    } else {
                        // Если тур не найден, добавляем данные без названия и стоимости
                        response.append(booking.getId()).append(",")
                                .append("Тур " + booking.getTourId()).append(",") // Заглушка для названия
                                .append(booking.getBookingDate()).append(",")
                                .append(0.0).append(",") // Заглушка для стоимости
                                .append("В ожидании").append("|"); // Статус по умолчанию
                    }
                }
                return response.toString();
            } catch (NumberFormatException e) {
                return "ERROR: Invalid user ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleGetTourPrice(String[] parts) {
        if (parts.length == 2) {
            try {
                int tourId = Integer.parseInt(parts[1]);
                Tour tour = tourService.getTourById(tourId);
                if (tour != null) {
                    return "TOUR_PRICE " + tour.getPrice();
                }
            } catch (NumberFormatException e) {
                return "ERROR: Invalid tour ID";
            }
        }
        return "ERROR: Invalid request";
    }

    /**
     * Обрабатывает запрос на получение списка платежей для бронирования.
     *
     * @param parts Массив строк, содержащий команду и ID бронирования.
     * @return Строка с данными о платежах.
     */
    private String handleGetPayments(String[] parts) {
        if (parts.length == 2) {
            try {
                int bookingId = Integer.parseInt(parts[1]);
                List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
                StringBuilder response = new StringBuilder("PAYMENTS ");
                for (Payment payment : payments) {
                    // Преобразуем статус на русский язык
                    String status = payment.getStatus().equals("paid") ? "Оплачено" : "В ожидании";

                    // Добавляем данные о платеже в ответ
                    response.append(payment.getId()).append(",")
                            .append(payment.getBookingId()).append(",")
                            .append(payment.getAmount()).append(",")
                            .append(payment.getPaymentDate()).append(",")
                            .append(status).append("|"); // Статус на русском
                }
                return response.toString();
            } catch (NumberFormatException e) {
                return "ERROR: Invalid booking ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleCancelBooking(String[] parts) {
        if (parts.length == 3) { // Добавлен ID пользователя
            try {
                int bookingId = Integer.parseInt(parts[1]);
                int userId = Integer.parseInt(parts[2]); // ID текущего пользователя

                // Получаем информацию о бронировании
                Booking booking = bookingService.getBookingById(bookingId);
                if (booking == null) {
                    return "ERROR: Бронирование не найдено.";
                }

                // Проверяем, что бронирование принадлежит текущему пользователю
                if (booking.getUserId() != userId) {
                    return "ERROR: Бронирование не принадлежит текущему пользователю.";
                }

                // Получаем информацию о пользователе
                User user = userService.getUserById(userId);
                if (user == null) {
                    return "ERROR: Пользователь не найден.";
                }

                // Получаем стоимость тура
                Tour tour = tourService.getTourById(booking.getTourId());
                if (tour == null) {
                    return "ERROR: Тур не найден.";
                }

                // Вычисляем штраф (5% от стоимости тура)
                double penalty = tour.getPrice() * 0.05;

                // Проверяем, достаточно ли средств на балансе для штрафа
                if (user.getBalance() < penalty) {
                    return "ERROR: Недостаточно средств для оплаты штрафа.";
                }

                // Списываем штраф с баланса пользователя
                double newBalance = user.getBalance() - penalty;
                userService.updateBalance(userId, newBalance);

                // Обновляем статус бронирования на "Отменено"
                booking.setStatus("cancelled");
                bookingService.updateBooking(booking); // Обновляем бронирование в базе данных

                return "CANCEL_SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR: Некорректный ID бронирования.";
            }
        }
        return "ERROR: Неверный запрос.";
    }

    private String handleMakePayment(String[] parts) {
        if (parts.length == 5) { // Добавлен ID пользователя
            try {
                int bookingId = Integer.parseInt(parts[1]);
                double amount = Double.parseDouble(parts[2]);
                String paymentDate = parts[3];
                int userId = Integer.parseInt(parts[4]); // ID текущего пользователя

                // Получаем информацию о бронировании
                Booking booking = bookingService.getBookingById(bookingId);
                if (booking == null) {
                    return "ERROR: Бронирование не найдено.";
                }

                // Проверяем, что бронирование принадлежит текущему пользователю
                if (booking.getUserId() != userId) {
                    return "ERROR: Бронирование не принадлежит текущему пользователю.";
                }

                // Получаем информацию о пользователе
                User user = userService.getUserById(userId);
                if (user == null) {
                    return "ERROR: Пользователь не найден.";
                }

                // Проверяем, достаточно ли средств на балансе
                if (user.getBalance() < amount) {
                    return "ERROR: Недостаточно средств на балансе.";
                }

                // Списываем средства с баланса пользователя
                double newBalance = user.getBalance() - amount;
                userService.updateBalance(userId, newBalance);

                // Обновляем статус бронирования на "confirmed"
                booking.setStatus("confirmed");
                bookingService.updateBooking(booking); // Обновляем бронирование в базе данных

                // Добавляем запись о платеже
                Payment payment = new Payment(0, bookingId, amount, paymentDate, "paid");
                paymentService.addPayment(payment);

                return "PAYMENT_SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR: Некорректные данные.";
            }
        }
        return "ERROR: Неверный запрос.";
    }

    private String handleGetDestinations() {
        List<Destination> destinations = destinationService.getAllDestinations();
        StringBuilder response = new StringBuilder("DESTINATIONS ");
        for (Destination destination : destinations) {
            response.append(destination.getId()).append(",")
                    .append(destination.getName()).append(",")
                    .append(destination.getCountry()).append(",")
                    .append(destination.getDescription()).append("|");
        }
        return response.toString();
    }

    private String handleGetToursByDestination(String[] parts) {
        if (parts.length == 2) {
            try {
                int destinationId = Integer.parseInt(parts[1]);
                List<Tour> tours = tourService.getToursByDestinationId(destinationId);
                StringBuilder response = new StringBuilder("TOURS ");
                for (Tour tour : tours) {
                    response.append(tour.getId()).append(",")
                            .append(tour.getName()).append(",")
                            .append(tour.getDescription()).append(",")
                            .append(tour.getPrice()).append(",")
                            .append(tour.getStartDate()).append(",")
                            .append(tour.getEndDate()).append(",")
                            .append(tour.getDestinationId()).append("|");
                }
                return response.toString();
            } catch (NumberFormatException e) {
                return "ERROR: Invalid destination ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleGetBalance(String[] parts) {
        if (parts.length == 2) {
            try {
                int userId = Integer.parseInt(parts[1]);
                double balance = userService.getBalance(userId);
                System.out.println("Запрошен баланс для пользователя " + userId + ": " + balance); // Вывод для отладки
                return "BALANCE " + balance;
            } catch (NumberFormatException e) {
                return "ERROR: Invalid user ID";
            }
        }
        return "ERROR: Invalid request";
    }
}