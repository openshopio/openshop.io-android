package bf.io.openshop.entities;

public class SortItem {

    private String value;
    private String description;

    public SortItem() {

    }

    public SortItem(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SortItem sortItem = (SortItem) o;

        if (value != null ? !value.equals(sortItem.value) : sortItem.value != null) return false;
        return !(description != null ? !description.equals(sortItem.description) : sortItem.description != null);

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return description;
    }
}
