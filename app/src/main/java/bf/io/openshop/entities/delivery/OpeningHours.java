package bf.io.openshop.entities.delivery;

public class OpeningHours {

    private String day;
    private String opening;

    public OpeningHours() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpeningHours that = (OpeningHours) o;

        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        return !(opening != null ? !opening.equals(that.opening) : that.opening != null);

    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + (opening != null ? opening.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OpeningHours{" +
                "day='" + day + '\'' +
                ", opening='" + opening + '\'' +
                '}';
    }
}
