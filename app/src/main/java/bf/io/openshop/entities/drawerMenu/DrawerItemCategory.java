package bf.io.openshop.entities.drawerMenu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DrawerItemCategory {

    private long id;

    @SerializedName("original_id")
    private long originalId;
    private String name;
    private List<DrawerItemCategory> children;
    private String type;

    public DrawerItemCategory() {
    }

    public DrawerItemCategory(long id, long originalId, String name) {
        this.id = id;
        this.originalId = originalId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(long originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DrawerItemCategory> getChildren() {
        return children;
    }

    public void setChildren(List<DrawerItemCategory> children) {
        this.children = children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawerItemCategory that = (DrawerItemCategory) o;

        if (id != that.id) return false;
        if (originalId != that.originalId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (children != null ? !children.equals(that.children) : that.children != null)
            return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (originalId ^ (originalId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrawerItemCategory{" +
                "id=" + id +
                ", originalId=" + originalId +
                ", name='" + name + '\'' +
                ", children=" + children +
                ", type='" + type + '\'' +
                '}';
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}
