package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bf.io.openshop.CONST;
import bf.io.openshop.R;
import bf.io.openshop.entities.delivery.Payment;

/**
 * Simple arrayAdapter for payment selection.
 */
public class PaymentSpinnerAdapter extends ArrayAdapter<Payment> {

    private List<Payment> payments;
    private LayoutInflater layoutInflater;
    private Context context;

    private long selectedId = CONST.DEFAULT_EMPTY_ID;

    /**
     * Creates an adapter for payment selection.
     *
     * @param context  activity context.
     * @param payments list of items.
     */
    public PaymentSpinnerAdapter(Context context, List<Payment> payments) {
        super(context, android.R.layout.simple_spinner_item, payments);
        this.context = context;
        this.payments = payments;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    /**
     * If in data exist equals payment, it will be highlighted.
     *
     * @param selectedPaymentType selected payment.
     */
    public void preselectPayment(Payment selectedPaymentType) {
        if (selectedPaymentType != null) {
            selectedId = selectedPaymentType.getId();
        }
    }


    public int getCount() {
        return payments.size();
    }

    public Payment getItem(int position) {
        return payments.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item_payment, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.payment_title);
            holder.description = (TextView) convertView.findViewById(R.id.payment_description);
            holder.isSelected = convertView.findViewById(R.id.payment_is_selected);
            holder.separator = convertView.findViewById(R.id.payment_separator);
            holder.price = (TextView) convertView.findViewById(R.id.payment_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Payment payment = getItem(position);

        // First item without top line separator
        if (position == 0) {
            holder.separator.setVisibility(View.GONE);
        } else {
            holder.separator.setVisibility(View.VISIBLE);
        }

        // Highlight selected item
        if (selectedId != CONST.DEFAULT_EMPTY_ID && payment.getId() == selectedId) {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.isSelected.setVisibility(View.VISIBLE);
        } else {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
            holder.isSelected.setVisibility(View.INVISIBLE);
        }

        holder.title.setText(payment.getName());

        if (payment.getDescription() != null && !payment.getDescription().isEmpty()) {
            holder.description.setText(payment.getDescription());
            holder.description.setVisibility(View.VISIBLE);
        } else
            holder.description.setVisibility(View.INVISIBLE);
        if (payment.getPrice() == 0) {
            holder.price.setText(R.string.free);
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            holder.price.setText(context.getResources().getString(R.string.format_plus, payment.getPriceFormatted()));
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView title;
        public View isSelected;
        public View separator;
        public TextView description;
        public TextView price;
    }

}

