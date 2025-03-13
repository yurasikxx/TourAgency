package main.server.models;

/**
 * Класс, представляющий сущность "Тур".
 */
public class Tour {
    private int id;              // Уникальный идентификатор тура
    private String name;         // Название тура
    private String description;  // Описание тура
    private double price;        // Цена тура
    private String startDate;    // Дата начала тура
    private String endDate;      // Дата окончания тура
    private int destinationId;   // Идентификатор направления

    // Конструкторы
    public Tour() {}

    public Tour(int id, String name, String description, double price, String startDate, String endDate, int destinationId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinationId = destinationId;
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

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", destinationId=" + destinationId +
                '}';
    }
}