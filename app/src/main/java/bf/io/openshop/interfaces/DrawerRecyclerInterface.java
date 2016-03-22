package bf.io.openshop.interfaces;

import android.view.View;

import bf.io.openshop.entities.drawerMenu.DrawerItemCategory;
import bf.io.openshop.entities.drawerMenu.DrawerItemPage;

public interface DrawerRecyclerInterface {

    void onCategorySelected(View v, DrawerItemCategory drawerItemCategory);

    void onPageSelected(View v, DrawerItemPage drawerItemPage);

    void onHeaderSelected();
}
