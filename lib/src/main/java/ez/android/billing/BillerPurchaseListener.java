package ez.android.billing;

import com.android.billingclient.api.Purchase;

public interface BillerPurchaseListener {
    void onPurchasedResult(Purchase purchase, boolean isAcknowledged);
}
