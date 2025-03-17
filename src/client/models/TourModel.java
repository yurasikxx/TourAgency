package client.models;

/**
 * Модель тура для клиентской части.
 */
public class TourModel {
    private int id;
    private String name;
    private String description;
    private double price;
    private String startDate;
    private String endDate;
    private String destination;

    // Конструкторы
    public TourModel() {}

    public TourModel(int id, String name, String description, double price, String startDate, String endDate, String destination) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destination = destination;
    }

    // Метод преобразования серверной модели в клиентскую
    public static TourModel fromServerModel(server.models.Tour serverTour) {
        return new TourModel(
                serverTour.getId(),
                serverTour.getName(),
                serverTour.getDescription(),
                serverTour.getPrice(),
                serverTour.getStartDate(),
                serverTour.getEndDate(),
                "Направление " + serverTour.getDestinationId() // Пример: можно загрузить название направления из базы данных
        );
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "TourModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}