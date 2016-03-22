package bf.io.openshop.entities.drawerMenu;

import java.util.List;

public class DrawerResponse {

    private List<DrawerItemCategory> navigation;
    private List<DrawerItemPage> pages;

    public DrawerResponse() {
    }

    public List<DrawerItemCategory> getNavigation() {
        return navigation;
    }

    public void setNavigation(List<DrawerItemCategory> navigation) {
        this.navigation = navigation;
    }

    public List<DrawerItemPage> getPages() {
        return pages;
    }

    public void setPages(List<DrawerItemPage> pages) {
        this.pages = pages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawerResponse)) return false;

        DrawerResponse that = (DrawerResponse) o;

        if (getNavigation() != null ? !getNavigation().equals(that.getNavigation()) : that.getNavigation() != null) return false;
        return !(getPages() != null ? !getPages().equals(that.getPages()) : that.getPages() != null);

    }

    @Override
    public int hashCode() {
        int result = getNavigation() != null ? getNavigation().hashCode() : 0;
        result = 31 * result + (getPages() != null ? getPages().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrawerResponse{" +
                "navigation=" + navigation +
                ", pages=" + pages +
                '}';
    }
}
