package bf.io.openshop.interfaces;


import bf.io.openshop.entities.delivery.Payment;

public interface PaymentDialogInterface {
    void onPaymentSelected(Payment payment);
}
