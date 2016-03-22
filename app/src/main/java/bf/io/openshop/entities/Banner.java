package bf.io.openshop.entities;

import com.google.gson.annotations.SerializedName;

public class Banner {

    private long id;
    private String name;
    private String target;

    @SerializedName("image_url")
    private String imageUrl;

    public Banner() {

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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Banner banner = (Banner) o;

        if (id != banner.id) return false;
        if (name != null ? !name.equals(banner.name) : banner.name != null) return false;
        if (target != null ? !target.equals(banner.target) : banner.target != null) return false;
        return !(imageUrl != null ? !imageUrl.equals(banner.imageUrl) : banner.imageUrl != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", target='" + target + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
