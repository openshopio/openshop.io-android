package bf.io.openshop.entities.filtr;

public class FilterValueColor {

    private long id = 0;
    private String value;
    private String code;
    private String img;

    public FilterValueColor() {
    }

    public FilterValueColor(long id, String value) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterValueColor that = (FilterValueColor) o;

        if (id != that.id) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return !(img != null ? !img.equals(that.img) : that.img != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (img != null ? img.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FilterValueColor{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", code='" + code + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
