package ez.android.billing;

import com.android.billingclient.api.SkuDetails;

import java.util.List;

public interface BillerQuerySkuDetailsListener {
    void onQuerySkuDetailsResults(List<SkuDetails> skuDetailsList);
}
