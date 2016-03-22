package bf.io.openshop.entities.filtr;


public class FilterValueSelect {

    private long id = 0;
    private String value;

    public FilterValueSelect() {
    }

    public FilterValueSelect(long id, String value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterValueSelect that = (FilterValueSelect) o;

        if (id != that.id) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FilterValueSelect{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
