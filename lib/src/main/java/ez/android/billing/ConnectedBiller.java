package ez.android.billing;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.Collections;
import java.util.List;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED;

public class ConnectedBiller implements PurchasesUpdatedListener {
    private static final String TAG = ConnectedBiller.class.getSimpleName();

    private BillingClient mBillingClient;
    private Context mContext;
    private BillerPurchaseListener mPurchaseListener;
    private SkuDetails mSkuDetails;

    ConnectedBiller() {}
    ConnectedBiller(Context context) {
        mContext = context;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    void setBillingClient(BillingClient client) {
        this.mBillingClient = client;
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult == null) return;
        int responseCode = billingResult.getResponseCode();
        switch (responseCode) {
            case OK:
                if (purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (!purchase.isAcknowledged()) {
                            acknowledgePurchase(purchase, success -> {
                                mPurchaseListener.onPurchasedResult(purchase, success);
                            });
                        } else {
                            mPurchaseListener.onPurchasedResult(purchase, true);
                        }
                    }
                }
                break;

            case USER_CANCELED:
                Log.d(TAG, "onPurchasesUpdated() user canceled");
                break;

            case ITEM_ALREADY_OWNED:
                Purchase purchase = queryPurchase(mSkuDetails.getSku(), mSkuDetails.getType());
                if (!purchase.isAcknowledged()) {
                    acknowledgePurchase(purchase, success -> {
                        mPurchaseListener.onPurchasedResult(purchase, success);
                    });
                } else {
                    mPurchaseListener.onPurchasedResult(purchase, true);
                }
                break;

            default:
                Log.e(TAG, "onPurchasesUpdated() ERROR" + responseCode);

        }
    }

    public boolean isPurchased(@NonNull String sku, String type) {
        Purchase purchase = queryPurchase(sku, type);
        if(purchase != null) {
            return purchase.isAcknowledged();
        }
        return false;
    }

    public Purchase queryPurchase(@NonNull String sku, String type) {
        Purchase.PurchasesResult queryPurchases = mBillingClient.queryPurchases(type);
        if (queryPurchases.getResponseCode() == OK) {
            List<Purchase> purchasesList = queryPurchases.getPurchasesList();
            if (purchasesList != null) {
                for (Purchase purchase : purchasesList) {
                    if(purchase.getSku().equals(sku)) {
                        return purchase;
                    }
                }
            }
        }
        return null;
    }

    public void querySkuDetail(String sku, String type, BillerQuerySkuDetailsListener listener) {
        querySkuDetails(Collections.singletonList(sku), type, listener);
    }

    public void querySkuDetails(@NonNull List<String> skus, String type, BillerQuerySkuDetailsListener listener) {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder()
                .setSkusList(skus)
                .setType(type);

        mBillingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            if (billingResult != null
                    && billingResult.getResponseCode() == OK
                    && skuDetailsList != null) {
                listener.onQuerySkuDetailsResults(skuDetailsList);
            }
        });
    }

    public void makePurchase(SkuDetails skuDetails, BillerPurchaseListener listener) {
        mPurchaseListener = listener;
        mSkuDetails = skuDetails;
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        if(mContext instanceof AppCompatActivity) {
            mBillingClient.launchBillingFlow((AppCompatActivity) mContext, flowParams);
        } else {
            Log.e(TAG, "makePurchase method require activity");
        }
    }

    private void acknowledgePurchase(Purchase purchase, BillerAcknowledgeListener listener) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        mBillingClient.acknowledgePurchase(params, billingResult -> {
            if (billingResult.getResponseCode() == OK) {
                listener.onAcknowledged(true);
            } else {
                listener.onAcknowledged(false);
                Log.e(TAG, "ERROR",  new BillerException("acknowledgePurchase()", billingResult.getResponseCode()));
            }
        });
    }
}
