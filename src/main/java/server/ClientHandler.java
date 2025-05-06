package server;

import server.models.Booking;
import server.models.Destination;
import server.models.Payment;
import server.models.Review;
import server.models.Tour;
import server.models.TourRating;
import server.models.User;
import server.services.BookingService;
import server.services.DestinationService;
import server.services.PaymentService;
import server.services.ReviewService;
import server.services.TourService;
import server.services.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private final UserService userService;
    private final TourService tourService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final DestinationService destinationService;
    private final ReviewService reviewService;

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

                String response = processRequest(inputLine);

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
            case "GET_ALL_DESTINATIONS":
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
            case "GET_BOOKING_STATUS":
                return handleGetBookingStatus(parts);
            case "GET_ALL_PAYMENTS_ADMIN":
                return handleGetAllPaymentsAdmin();
            case "HAS_BOOKINGS":
                return handleHasBookings(parts);
            case "GET_ALL_BOOKINGS_ADMIN":
                return handleGetAllBookingsAdmin();
            case "HAS_TOURS_FOR_DESTINATION":
                return handleHasToursForDestination(parts);
            case "UPDATE_PROFILE":
                return handleUpdateProfile(parts);
            default:
                return "ERROR: Unknown command";
        }
    }

    private String handleLogin(String[] parts) {
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            User user = userService.authenticate(username, password);
            if (user != null) {
                return String.format("LOGIN_SUCCESS %s %d %.2f %s %d %s %s",
                        user.getRole(),
                        user.getId(),
                        user.getBalance(),
                        user.getFullName(),
                        user.getAge(),
                        user.getEmail(),
                        user.getPhone());
            }
        }
        return "LOGIN_FAILURE";
    }

    private String handleRegister(String[] parts) {
        if (parts.length == 9) {
            String username = parts[1];
            String password = parts[2];
            String fullName = parts[3] + " " + parts[4] + " " + parts[5];
            int age = Integer.parseInt(parts[6]);
            String email = parts[7];
            String phone = parts[8];

            if (userService.getUserByUsername(username) != null) {
                return "REGISTER_FAILURE: Пользователь с таким логином уже существует.";
            }

            User newUser = new User(username, password, "USER", 1000, fullName, age, email, phone);
            userService.register(newUser);
            return "REGISTER_SUCCESS";
        }
        return "REGISTER_FAILURE: Неверный запрос.";
    }

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

    private String handleBookTour(String[] parts) {
        if (parts.length == 9) {
            try {
                int userId = Integer.parseInt(parts[1]);
                int tourId = Integer.parseInt(parts[2]);
                String bookingDate = parts[3];
                int adults = Integer.parseInt(parts[4]);
                int children = Integer.parseInt(parts[5]);
                String mealOption = parts[6];
                String additionalServices = parts[7];
                double totalPrice = Double.parseDouble(parts[8]);

                String currentStatus = bookingService.getBookingStatus(userId, tourId);

                if (currentStatus != null) {
                    switch (currentStatus) {
                        case "pending":
                            return "BOOKING_FAILURE: У вас уже есть бронь на этот тур (статус: в ожидании)";
                        case "confirmed":
                            return "BOOKING_FAILURE: Этот тур уже оплачен и не может быть забронирован повторно";
                        case "cancelled":
                            System.out.println("Можно создать новую бронь, если предыдущая была отменена");
                            break;
                    }
                }

                Booking booking = new Booking(0, userId, tourId, bookingDate, "pending",
                        adults, children, mealOption, additionalServices, totalPrice);
                bookingService.addBooking(booking);
                return "BOOKING_SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR: Invalid parameters";
            }
        }
        return "ERROR: Invalid booking request";
    }

    private String handleGetBookings(String[] parts) {
        if (parts.length == 2) {
            try {
                int userId = Integer.parseInt(parts[1]);
                List<Booking> bookings = bookingService.getBookingsByUserId(userId);
                StringBuilder response = new StringBuilder("BOOKINGS ");

                for (Booking booking : bookings) {
                    Tour tour = tourService.getTourById(booking.getTourId());

                    if (tour != null) {
                        String status;
                        if (booking.getStatus().equals("confirmed")) {
                            status = "Подтверждено";
                        } else if (booking.getStatus().equals("cancelled")) {
                            status = "Отменено";
                        } else {
                            status = "В ожидании";
                        }

                        response.append(booking.getId()).append(",")
                                .append(tour.getName()).append(",")
                                .append(booking.getBookingDate()).append(",")
                                .append(tour.getPrice()).append(",")
                                .append(status).append(",")
                                .append(booking.getAdults()).append(",")
                                .append(booking.getChildren()).append(",")
                                .append(booking.getMealOption()).append(",")
                                .append(booking.getAdditionalServices()).append(",")
                                .append(booking.getTotalPrice()).append("|");
                    } else {
                        response.append(booking.getId()).append(",")
                                .append("Тур ").append(booking.getTourId()).append(",")
                                .append(booking.getBookingDate()).append(",")
                                .append(0.0).append(",")
                                .append("В ожидании").append("|")
                                .append(booking.getAdults()).append(",")
                                .append(booking.getChildren()).append(",")
                                .append(booking.getMealOption()).append(",")
                                .append(booking.getAdditionalServices()).append(",")
                                .append(booking.getTotalPrice()).append("|");
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

    private String handleGetPayments(String[] parts) {
        if (parts.length == 2) {
            try {
                int bookingId = Integer.parseInt(parts[1]);
                List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
                StringBuilder response = new StringBuilder("PAYMENTS ");

                for (Payment payment : payments) {
                    String status = payment.getStatus().equals("paid") ? "Оплачено" : "В ожидании";

                    response.append(payment.getId()).append(",")
                            .append(payment.getBookingId()).append(",")
                            .append(payment.getAmount()).append(",")
                            .append(payment.getPaymentDate()).append(",")
                            .append(status).append("|");
                }

                return response.toString();
            } catch (NumberFormatException e) {
                return "ERROR: Invalid booking ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleCancelBooking(String[] parts) {
        if (parts.length == 3) {
            try {
                int bookingId = Integer.parseInt(parts[1]);
                int userId = Integer.parseInt(parts[2]);

                Booking booking = bookingService.getBookingById(bookingId);
                if (booking == null) {
                    return "ERROR: Бронирование не найдено.";
                }

                if (booking.getUserId() != userId) {
                    return "ERROR: Бронирование не принадлежит текущему пользователю.";
                }

                User user = userService.getUserById(userId);
                if (user == null) {
                    return "ERROR: Пользователь не найден.";
                }

                double penalty = booking.getTotalPrice() * 0.05;

                if (user.getBalance() < penalty) {
                    return "ERROR: Недостаточно средств для оплаты штрафа.";
                }

                double newBalance = user.getBalance() - penalty;
                userService.updateBalance(userId, newBalance);

                booking.setStatus("cancelled");
                bookingService.updateBooking(booking);

                return "CANCEL_SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR: Некорректный ID бронирования.";
            }
        }
        return "ERROR: Неверный запрос.";
    }

    private String handleMakePayment(String[] parts) {
        if (parts.length == 5) {
            try {
                int bookingId = Integer.parseInt(parts[1]);
                double totalPrice = Double.parseDouble(parts[2]);
                String paymentDate = parts[3];
                int userId = Integer.parseInt(parts[4]);

                Booking booking = bookingService.getBookingById(bookingId);
                if (booking == null) {
                    return "ERROR: Бронирование не найдено.";
                }

                if (booking.getUserId() != userId) {
                    return "ERROR: Бронирование не принадлежит текущему пользователю.";
                }

                User user = userService.getUserById(userId);
                if (user == null) {
                    return "ERROR: Пользователь не найден.";
                }

                if (user.getBalance() < totalPrice) {
                    return "ERROR: Недостаточно средств на балансе.";
                }

                double newBalance = user.getBalance() - totalPrice;
                userService.updateBalance(userId, newBalance);

                booking.setStatus("confirmed");
                bookingService.updateBooking(booking);

                Payment payment = new Payment(0, bookingId, totalPrice, paymentDate, "paid");
                paymentService.addPayment(payment);

                return "PAYMENT_SUCCESS";
            } catch (NumberFormatException e) {
                return "ERROR: Некорректные данные.";
            }
        }
        return "ERROR: Неверный запрос.";
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
                    .append(user.getBalance()).append(",")
                    .append(user.getFullName()).append(",")
                    .append(user.getAge()).append(",")
                    .append(user.getEmail()).append(",")
                    .append(user.getPhone()).append("|");
        }

        return response.toString();
    }

    private String handleAddUser(String[] parts) {
        if (parts.length == 11) {
            try {
                String username = parts[1];
                String password = parts[2];
                String role = parts[3];
                double balance = Double.parseDouble(parts[4]);
                String fullName = parts[5] + " " + parts[6] + " " + parts[7];
                int age = Integer.parseInt(parts[8]);
                String email = parts[9];
                String phone = parts[10];

                User user = new User(username, password, role, balance, fullName, age, email, phone);
                userService.register(user);

                return "USER_ADDED";
            } catch (Exception e) {
                return "ERROR: Invalid user data";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleUpdateUser(String[] parts) {
        if (parts.length == 12) {
            try {
                int id = Integer.parseInt(parts[1]);
                String username = parts[2];
                String password = parts[3];
                String role = parts[4];
                double balance = Double.parseDouble(parts[5]);
                String fullName = parts[6] + " " + parts[7] + " " + parts[8];
                int age = Integer.parseInt(parts[9]);
                String email = parts[10];
                String phone = parts[11];

                User existingUser = userService.getUserByUsername(username);
                if (existingUser != null && existingUser.getId() != id) {
                    return "ERROR: Пользователь с таким именем уже существует";
                }

                User user = new User(id, username, password, role, balance, fullName, age, email, phone);
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
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            String delimiter = input.contains("--") ? "--" : "~";
            String[] combined = input.split(delimiter, -1);

            if (combined.length < 6) {
                return "ERROR: Неверное количество параметров. Ожидается: название" + delimiter +
                        "описание" + delimiter + "цена" + delimiter + "дата_начала" + delimiter +
                        "дата_окончания" + delimiter + "id_направления";
            }

            try {
                String name = combined[0].trim();
                String description = combined[1].trim();
                double price = Double.parseDouble(combined[2].trim());
                String startDate = combined[3].trim();
                String endDate = combined[4].trim();
                int destinationId = Integer.parseInt(combined[5].trim());

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
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            String delimiter = input.contains("--") ? "--" : "~";
            String[] combined = input.split(delimiter, -1);

            if (combined.length < 7) {
                return "ERROR: Неверное количество параметров. Ожидается: id" + delimiter +
                        "название" + delimiter + "описание" + delimiter + "цена" + delimiter +
                        "дата_начала" + delimiter + "дата_окончания" + delimiter + "id_направления";
            }

            try {
                int id = Integer.parseInt(combined[0].trim());
                String name = combined[1].trim();
                String description = combined[2].trim();
                double price = Double.parseDouble(combined[3].trim());
                String startDate = combined[4].trim();
                String endDate = combined[5].trim();
                int destinationId = Integer.parseInt(combined[6].trim());

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
                String name = destination.getName().replace(",", "\\,").replace("|", "\\|");
                String country = destination.getCountry().replace(",", "\\,").replace("|", "\\|");
                String description = destination.getDescription().replace(",", "\\,").replace("|", "\\|");

                response.append(destination.getId()).append(",")
                        .append(name).append(",")
                        .append(country).append(",")
                        .append(description).append("|");
            }

            response.setLength(response.length() - 1);
            return response.toString();
        } catch (Exception e) {
            return "ERROR: Failed to get destinations - " + e.getMessage();
        }
    }

    private String handleAddDestination(String[] parts) {
        try {
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            String[] data = input.split("(?<!\\\\),", -1);
            if (data.length != 3) {
                return "ERROR: Неверное количество параметров. Ожидается: название,страна,описание";
            }

            String name = data[0].replace("\\,", ",").replace("\\|", "|");
            String country = data[1].replace("\\,", ",").replace("\\|", "|");
            String description = data[2].replace("\\,", ",").replace("\\|", "|");

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
            String input = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            String[] data = input.split("(?<!\\\\),", -1);
            if (data.length != 4) {
                return "ERROR: Неверное количество параметров. Ожидается: id,название,страна,описание";
            }

            int id = Integer.parseInt(data[0]);

            String name = data[1].replace("\\,", ",").replace("\\|", "|");
            String country = data[2].replace("\\,", ",").replace("\\|", "|");
            String description = data[3].replace("\\,", ",").replace("\\|", "|");

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

    private String handleSearchTours(String[] parts) {
        try {
            String searchTerm = parts.length > 1 && !parts[1].equals("null") ? parts[1] : null;
            Double maxPrice = parts.length > 2 && !parts[2].equals("null") ? Double.parseDouble(parts[2]) : null;
            Double minPrice = parts.length > 3 && !parts[3].equals("null") ? Double.parseDouble(parts[3]) : null;
            String startDate = parts.length > 4 && !parts[4].equals("null") ? parts[4] : null;
            String endDate = parts.length > 5 && !parts[5].equals("null") ? parts[5] : null;
            String sortBy = parts.length > 6 && !parts[6].equals("null") ? parts[6] : null;

            List<Tour> tours = tourService.searchTours(
                    searchTerm, minPrice, maxPrice, startDate, endDate, sortBy
            );

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

    private String handleGetBookingStatus(String[] parts) {
        if (parts.length == 3) {
            try {
                int userId = Integer.parseInt(parts[1]);
                int tourId = Integer.parseInt(parts[2]);
                String status = bookingService.getBookingStatus(userId, tourId);

                return "BOOKING_STATUS " + (status != null ? status : "none");
            } catch (NumberFormatException e) {
                return "ERROR: Invalid parameters";
            }
        }
        return "ERROR: Invalid get booking status request";
    }

    private String handleGetAllPaymentsAdmin() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            StringBuilder response = new StringBuilder("PAYMENTS_ADMIN ");

            for (Payment payment : payments) {
                Booking booking = bookingService.getBookingById(payment.getBookingId());
                Tour tour = tourService.getTourById(booking.getTourId());

                response.append(payment.getId()).append(",")
                        .append(payment.getBookingId()).append(",")
                        .append(booking.getUserId()).append(",")
                        .append(payment.getAmount()).append(",")
                        .append(payment.getPaymentDate()).append(",")
                        .append(payment.getStatus()).append(",")
                        .append(tour != null ? tour.getName() : "N/A").append("|");
            }

            return response.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleHasBookings(String[] parts) {
        if (parts.length == 2) {
            try {
                int tourId = Integer.parseInt(parts[1]);
                boolean hasBookings = bookingService.hasBookingsForTour(tourId);
                return "HAS_BOOKINGS " + hasBookings;
            } catch (NumberFormatException e) {
                return "ERROR: Invalid tour ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleGetAllBookingsAdmin() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            StringBuilder response = new StringBuilder("BOOKINGS_ADMIN ");

            for (Booking booking : bookings) {
                User user = userService.getUserById(booking.getUserId());
                Tour tour = tourService.getTourById(booking.getTourId());

                response.append(booking.getId()).append(",")
                        .append(user != null ? user.getUsername() : "N/A").append(",")
                        .append(tour != null ? tour.getName() : "N/A").append(",")
                        .append(booking.getBookingDate()).append(",")
                        .append(booking.getStatus()).append(",")
                        .append(tour != null ? tour.getPrice() : 0.0).append("|");
            }

            return response.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleHasToursForDestination(String[] parts) {
        if (parts.length == 2) {
            try {
                int destinationId = Integer.parseInt(parts[1]);
                boolean hasTours = tourService.hasToursForDestination(destinationId);
                return "HAS_TOURS " + hasTours;
            } catch (NumberFormatException e) {
                return "ERROR: Invalid destination ID";
            }
        }
        return "ERROR: Invalid request";
    }

    private String handleUpdateProfile(String[] parts) {
        if (parts.length == 9) {
            try {
                int userId = Integer.parseInt(parts[1]);
                String fullName = parts[2].replace("~", " ");
                int age = Integer.parseInt(parts[3]);
                String email = parts[4];
                String phone = parts[5];
                String oldPassword = parts[6];
                String newPassword = parts[7];

                User user = userService.getUserById(userId);
                if (user == null) {
                    return "ERROR: Пользователь не найден";
                }

                if (!newPassword.isEmpty() && !user.getPassword().equals(oldPassword)) {
                    return "ERROR: Неверный старый пароль";
                }

                user.setFullName(fullName);
                user.setAge(age);
                user.setEmail(email);
                user.setPhone(phone);

                if (!newPassword.isEmpty()) {
                    user.setPassword(newPassword);
                }

                userService.updateUser(user);
                return "PROFILE_UPDATED";
            } catch (NumberFormatException e) {
                return "ERROR: Неверный формат данных";
            }
        }
        return "ERROR: Неверный запрос";
    }
}