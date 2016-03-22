package bf.io.openshop.entities.drawerMenu;

public class DrawerItemPage {

    private long id;
    private String name;

    public DrawerItemPage(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public DrawerItemPage() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawerItemPage)) return false;

        DrawerItemPage that = (DrawerItemPage) o;

        if (getId() != that.getId()) return false;
        return !(getName() != null ? !getName().equals(that.getName()) : that.getName() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrawerItemPage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
