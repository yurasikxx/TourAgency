package server;

import server.models.*;
import server.services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;
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
    private ReviewService reviewService;

    public ClientHandler(Socket socket, UserService authService, TourService tourService,
                         BookingService bookingService, PaymentService paymentService,
                         DestinationService destinationService, ReviewService reviewService) {
        this.clientSocket = socket;
        this.userService = authService;
        this.tourService = tourService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.destinationService = destinationService;
        this.reviewService = reviewService;
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
                return handleGetAllDestinations();
            case "GET_TOURS_BY_DESTINATION":
                return handleGetToursByDestination(parts);
            case "GET_BALANCE":
                return handleGetBalance(parts);
            case "GET_ALL_USERS":
                return handleGetAllUsers();
            case "ADD_USER":
                return handleAddUser(parts);
            case "UPDATE_USER":
                return handleUpdateUser(parts);
            case "DELETE_USER":
                return handleDeleteUser(parts);
            case "GET_ALL_TOURS_ADMIN":
                return handleGetAllToursAdmin();
            case "ADD_TOUR":
                return handleAddTour(parts);
            case "UPDATE_TOUR":
                return handleUpdateTour(parts);
            case "DELETE_TOUR":
                return handleDeleteTour(parts);
            case "GET_ALL_DESTINATIONS":
                return handleGetAllDestinations();
            case "ADD_DESTINATION":
                return handleAddDestination(parts);
            case "UPDATE_DESTINATION":
                return handleUpdateDestination(parts);
            case "DELETE_DESTINATION":
                return handleDeleteDestination(parts);
            case "SEARCH_TOURS":
                return handleSearchTours(parts);
            case "GET_POPULAR_TOURS":
                return handleGetPopularTours(parts);
            case "ADD_REVIEW":
                return handleAddReview(parts);
            case "GET_REVIEWS":
                return handleGetReviews(parts);
            case "HAS_REVIEWED":
                return handleHasReviewed(parts);
            case "GET_TOUR_RATING":
                return handleGetTourRating(parts);
            case "HAS_BOOKED":
                return handleHasBooked(parts);
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

    private String handleGetAllUsers() {
        List<User> users = userService.getAllUsers();
        StringBuilder response = new StringBuilder("USERS ");
        for (User user : users) {
            response.append(user.getId()).append(",")
                    .append(user.getUsername()).append(",")
                    .append(user.getRole()).append(",")
                    .append(user.getBalance()).append("|");
        }
        return response.toString();
    }

    private String handleAddUser(String[] parts) {
        if (parts.length == 5) {
            try {
                String username = parts[1];
                String password = parts[2];
                String role = parts[3];
                double balance = Double.parseDouble(parts[4]);

                User user = new User(username, password, role, balance);
                userService.register(user);
                return "USER_ADDED";
            } catch (Exception e) {
                return "ERROR: Invalid user data";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleUpdateUser(String[] parts) {
        if (parts.length == 6) {
            try {
                int id = Integer.parseInt(parts[1]);
                String username = parts[2];
                String password = parts[3];
                String role = parts[4];
                double balance = Double.parseDouble(parts[5]);

                User user = new User(id, username, password, role, balance);
                userService.updateUser(user);
                return "USER_UPDATED";
            } catch (Exception e) {
                return "ERROR: Invalid user data";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleDeleteUser(String[] parts) {
        if (parts.length == 2) {
            try {
                int id = Integer.parseInt(parts[1]);
                userService.deleteUser(id);
                return "USER_DELETED";
            } catch (Exception e) {
                return "ERROR: Invalid user ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleGetAllToursAdmin() {
        List<Tour> tours = tourService.getAllTours();
        StringBuilder response = new StringBuilder("TOURS_ADMIN ");
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

    private String handleAddTour(String[] parts) {
        try {
            // Объединяем все части после команды
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            // Разбиваем по разделителю (используем ~ или --)
            String delimiter = input.contains("--") ? "--" : "~";
            String[] combined = input.split(delimiter, -1);

            if (combined.length < 6) {
                return "ERROR: Неверное количество параметров. Ожидается: название" + delimiter +
                        "описание" + delimiter + "цена" + delimiter + "дата_начала" + delimiter +
                        "дата_окончания" + delimiter + "id_направления";
            }

            try {
                // Извлекаем параметры
                String name = combined[0].trim();
                String description = combined[1].trim();
                double price = Double.parseDouble(combined[2].trim());
                String startDate = combined[3].trim();
                String endDate = combined[4].trim();
                int destinationId = Integer.parseInt(combined[5].trim());

                // Валидация
                if (price <= 0) return "ERROR: Цена должна быть положительной";
                if (destinationId <= 0) return "ERROR: Неверный ID направления";
                if (name.isEmpty() || description.isEmpty()) return "ERROR: Название и описание не могут быть пустыми";

                Tour tour = new Tour(0, name, description, price, startDate, endDate, destinationId);
                tourService.addTour(tour);
                return "TOUR_ADDED";
            } catch (NumberFormatException e) {
                return "ERROR: Неверный формат числового значения (цена или ID направления)";
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleUpdateTour(String[] parts) {
        try {
            // Объединяем все части после команды
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            // Разбиваем по разделителю (используем ~ или --)
            String delimiter = input.contains("--") ? "--" : "~";
            String[] combined = input.split(delimiter, -1);

            if (combined.length < 7) {
                return "ERROR: Неверное количество параметров. Ожидается: id" + delimiter +
                        "название" + delimiter + "описание" + delimiter + "цена" + delimiter +
                        "дата_начала" + delimiter + "дата_окончания" + delimiter + "id_направления";
            }

            try {
                // Извлекаем параметры
                int id = Integer.parseInt(combined[0].trim());
                String name = combined[1].trim();
                String description = combined[2].trim();
                double price = Double.parseDouble(combined[3].trim());
                String startDate = combined[4].trim();
                String endDate = combined[5].trim();
                int destinationId = Integer.parseInt(combined[6].trim());

                // Валидация
                if (id <= 0) return "ERROR: Неверный ID тура";
                if (price <= 0) return "ERROR: Цена должна быть положительной";
                if (destinationId <= 0) return "ERROR: Неверный ID направления";
                if (name.isEmpty() || description.isEmpty()) {
                    return "ERROR: Название и описание не могут быть пустыми";
                }

                Tour tour = new Tour(id, name, description, price, startDate, endDate, destinationId);
                tourService.updateTour(tour);
                return "TOUR_UPDATED";
            } catch (NumberFormatException e) {
                return "ERROR: Неверный формат числового значения";
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String validateTourData(String name, String description, double price,
                                    String startDate, String endDate, int destinationId) {
        if (name == null || name.trim().isEmpty()) {
            return "Название тура не может быть пустым";
        }
        if (description == null || description.trim().isEmpty()) {
            return "Описание тура не может быть пустым";
        }
        if (price <= 0) {
            return "Стоимость тура должна быть положительным числом";
        }
        if (!isValidDate(startDate)) {
            return "Неверный формат даты начала (используйте YYYY-MM-DD)";
        }
        if (!isValidDate(endDate)) {
            return "Неверный формат даты окончания (используйте YYYY-MM-DD)";
        }
        if (!destinationService.exists(destinationId)) {
            return "Указанное направление не существует";
        }
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (end.isBefore(start)) {
                return "Дата окончания должна быть после даты начала";
            }
        } catch (Exception e) {
            return "Неверный формат даты";
        }
        return null;
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String handleDeleteTour(String[] parts) {
        if (parts.length == 2) {
            try {
                int id = Integer.parseInt(parts[1]);
                tourService.deleteTour(id);
                return "TOUR_DELETED";
            } catch (Exception e) {
                return "ERROR: Invalid tour ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleGetAllDestinations() {
        try {
            List<Destination> destinations = destinationService.getAllDestinations();
            if (destinations == null || destinations.isEmpty()) {
                return "DESTINATIONS EMPTY";
            }

            StringBuilder response = new StringBuilder("DESTINATIONS ");
            for (Destination destination : destinations) {
                // Экранируем запятые и вертикальные черты в данных
                String name = destination.getName().replace(",", "\\,").replace("|", "\\|");
                String country = destination.getCountry().replace(",", "\\,").replace("|", "\\|");
                String description = destination.getDescription().replace(",", "\\,").replace("|", "\\|");

                response.append(destination.getId()).append(",")
                        .append(name).append(",")
                        .append(country).append(",")
                        .append(description).append("|");
            }

            // Удаляем последний разделитель
            response.setLength(response.length() - 1);
            return response.toString();
        } catch (Exception e) {
            return "ERROR: Failed to get destinations - " + e.getMessage();
        }
    }

    private String handleAddDestination(String[] parts) {
        try {
            // Объединяем все части после команды
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            // Разбиваем по запятым, учитывая экранирование
            String[] data = input.split("(?<!\\\\),", -1);
            if (data.length != 3) {
                return "ERROR: Неверное количество параметров. Ожидается: название,страна,описание";
            }

            // Убираем экранирование
            String name = data[0].replace("\\,", ",").replace("\\|", "|");
            String country = data[1].replace("\\,", ",").replace("\\|", "|");
            String description = data[2].replace("\\,", ",").replace("\\|", "|");

            // Валидация
            if (name.isEmpty() || country.isEmpty() || description.isEmpty()) {
                return "ERROR: Все поля должны быть заполнены";
            }

            Destination destination = new Destination(0, name, country, description);
            destinationService.addDestination(destination);
            return "DESTINATION_ADDED";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleUpdateDestination(String[] parts) {
        try {
            // Объединяем все части после команды
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            // Разбиваем по запятым, учитывая экранирование
            String[] data = input.split("(?<!\\\\),", -1);
            if (data.length != 4) {
                return "ERROR: Неверное количество параметров. Ожидается: id,название,страна,описание";
            }

            int id = Integer.parseInt(data[0]);

            // Убираем экранирование
            String name = data[1].replace("\\,", ",").replace("\\|", "|");
            String country = data[2].replace("\\,", ",").replace("\\|", "|");
            String description = data[3].replace("\\,", ",").replace("\\|", "|");

            // Валидация
            if (id <= 0) return "ERROR: Неверный ID направления";
            if (name.isEmpty() || country.isEmpty() || description.isEmpty()) {
                return "ERROR: Все поля должны быть заполнены";
            }

            Destination destination = new Destination(id, name, country, description);
            destinationService.updateDestination(destination);
            return "DESTINATION_UPDATED";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleDeleteDestination(String[] parts) {
        if (parts.length == 2) {
            try {
                int id = Integer.parseInt(parts[1]);
                destinationService.deleteDestination(id);
                return "DESTINATION_DELETED";
            } catch (Exception e) {
                return "ERROR: Invalid destination ID";
            }
        }
        return "ERROR: Invalid request";
    }

    // server/ClientHandler.java
    private String handleSearchTours(String[] parts) {
        try {
            // Парсим параметры (теперь все параметры независимы)
            String searchTerm = parts.length > 1 && !parts[1].equals("null") ? parts[1] : null;
            Double maxPrice = parts.length > 2 && !parts[2].equals("null") ? Double.parseDouble(parts[2]) : null;
            Double minPrice = parts.length > 3 && !parts[3].equals("null") ? Double.parseDouble(parts[3]) : null;
            String startDate = parts.length > 4 && !parts[4].equals("null") ? parts[4] : null;
            String endDate = parts.length > 5 && !parts[5].equals("null") ? parts[5] : null;
            String sortBy = parts.length > 6 && !parts[6].equals("null") ? parts[6] : null;

            List<Tour> tours = tourService.searchTours(
                    searchTerm, minPrice, maxPrice, startDate, endDate, sortBy
            );

            // Формирование ответа остается без изменений
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
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleGetPopularTours(String[] parts) {
        int limit = parts.length > 1 ? Integer.parseInt(parts[1]) : 5;
        List<Tour> popularTours = tourService.getPopularTours(limit);

        StringBuilder response = new StringBuilder("TOURS ");
        for (Tour tour : popularTours) {
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

    private String handleAddReview(String[] parts) {
        if (parts.length == 6) {
            try {
                int userId = Integer.parseInt(parts[1]);
                int tourId = Integer.parseInt(parts[2]);
                int rating = Integer.parseInt(parts[3]);
                String comment = parts[4].replace("~", " ");
                String reviewDate = parts[5];

                Review review = new Review(0, userId, tourId, rating, comment, reviewDate);
                reviewService.addReview(review);
                return "REVIEW_ADDED";
            } catch (NumberFormatException e) {
                return "ERROR: Invalid parameters";
            }
        }
        return "ERROR: Invalid add review request";
    }

    private String handleGetReviews(String[] parts) {
        if (parts.length == 2) {
            try {
                int tourId = Integer.parseInt(parts[1]);
                List<Review> reviews = reviewService.getReviewsByTourId(tourId);
                StringBuilder response = new StringBuilder("REVIEWS ");
                for (Review review : reviews) {
                    response.append(review.getId()).append(",")
                            .append(review.getUserId()).append(",")
                            .append(review.getRating()).append(",")
                            .append(review.getComment().replace(",", "\\,").replace("|", "\\|")).append(",")
                            .append(review.getReviewDate()).append("|");
                }
                return response.toString();
            } catch (NumberFormatException e) {
                return "ERROR: Invalid tour ID";
            }
        }
        return "ERROR: Invalid get reviews request";
    }

    private String handleHasReviewed(String[] parts) {
        if (parts.length == 3) {
            try {
                int userId = Integer.parseInt(parts[1]);
                int tourId = Integer.parseInt(parts[2]);
                boolean hasReviewed = reviewService.hasUserReviewedTour(userId, tourId);
                return "HAS_REVIEWED " + hasReviewed;
            } catch (NumberFormatException e) {
                return "ERROR: Invalid parameters";
            }
        }
        return "ERROR: Invalid has reviewed request";
    }

    private String handleGetTourRating(String[] parts) {
        if (parts.length == 2) {
            try {
                int tourId = Integer.parseInt(parts[1]);
                TourRating rating = reviewService.getTourRating(tourId);
                if (rating != null) {
                    return String.format("TOUR_RATING %.2f %d",
                            rating.getAverageRating(),
                            rating.getReviewCount());
                }
                return "TOUR_RATING 0.00 0";
            } catch (NumberFormatException e) {
                return "ERROR: Invalid tour ID";
            }
        }
        return "ERROR: Invalid get tour rating request";
    }

    private String handleHasBooked(String[] parts) {
        if (parts.length == 3) {
            try {
                int userId = Integer.parseInt(parts[1]);
                int tourId = Integer.parseInt(parts[2]);
                boolean hasBooked = bookingService.hasUserBookedTour(userId, tourId);
                return "HAS_BOOKED " + hasBooked;
            } catch (NumberFormatException e) {
                return "ERROR: Invalid parameters";
            }
        }
        return "ERROR: Invalid has booked request";
    }
}