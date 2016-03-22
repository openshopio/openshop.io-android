package bf.io.openshop.entities.delivery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Branch {

    private long id;
    private String name;
    private String address;
    private Coordinates coordinates;

    @SerializedName("opening_hours")
    private List<OpeningHours> openingHoursList;
    private String note;
    private List<Transport> transports;

    public Branch() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public List<OpeningHours> getOpeningHoursList() {
        return openingHoursList;
    }

    public void setOpeningHoursList(List<OpeningHours> openingHoursList) {
        this.openingHoursList = openingHoursList;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Transport> getTransports() {
        return transports;
    }

    public void setTransports(List<Transport> transports) {
        this.transports = transports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;

        if (id != branch.id) return false;
        if (name != null ? !name.equals(branch.name) : branch.name != null) return false;
        if (address != null ? !address.equals(branch.address) : branch.address != null)
            return false;
        if (coordinates != null ? !coordinates.equals(branch.coordinates) : branch.coordinates != null)
            return false;
        if (openingHoursList != null ? !openingHoursList.equals(branch.openingHoursList) : branch.openingHoursList != null)
            return false;
        if (note != null ? !note.equals(branch.note) : branch.note != null) return false;
        return !(transports != null ? !transports.equals(branch.transports) : branch.transports != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        result = 31 * result + (openingHoursList != null ? openingHoursList.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (transports != null ? transports.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", coordinates=" + coordinates +
                ", openingHoursList=" + openingHoursList +
                ", note='" + note + '\'' +
                ", transports=" + transports +
                '}';
    }
}
