package bf.io.openshop.interfaces;

import android.view.View;

import bf.io.openshop.entities.order.Order;

public interface OrdersRecyclerInterface {

    void onOrderSelected(View v, Order order);

}
