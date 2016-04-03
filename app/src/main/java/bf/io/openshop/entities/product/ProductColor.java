package bf.io.openshop.entities.product;

import com.google.gson.annotations.SerializedName;

public class ProductColor {

    private long id = 0;

    @SerializedName("remote_id")
    private long remoteId;
    private String value;
    private String code;
    private String img;

    public ProductColor() {
    }

    public ProductColor(long id, String value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
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

        ProductColor that = (ProductColor) o;

        if (id != that.id) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return !(img != null ? !img.equals(that.img) : that.img != null);
    }

    public boolean equalsColors(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductColor that = (ProductColor) o;
        if (id != that.id) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (remoteId ^ (remoteId >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (img != null ? img.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductColor{" +
                "id=" + id +
                ", remoteId=" + remoteId +
                ", value='" + value + '\'' +
                ", code='" + code + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
