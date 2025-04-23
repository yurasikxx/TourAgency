package client.models;

public class DestinationModel {
    private int id;
    private String name;
    private String country;
    private String description;

    public DestinationModel() {
    }

    public DestinationModel(int id, String name, String country, String description) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
    }

    public static DestinationModel fromServerModel(server.models.Destination serverDestination) {
        return new DestinationModel(
                serverDestination.getId(),
                serverDestination.getName(),
                serverDestination.getCountry(),
                serverDestination.getDescription()
        );
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return '"' + description + '"' + " – " + name + ", " + country;
    }
}