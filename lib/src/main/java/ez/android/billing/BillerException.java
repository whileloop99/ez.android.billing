package ez.android.billing;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.BILLING_UNAVAILABLE;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.DEVELOPER_ERROR;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.ERROR;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.ITEM_NOT_OWNED;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.ITEM_UNAVAILABLE;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.SERVICE_DISCONNECTED;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE;
import static com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED;

public class BillerException extends Exception {

    private String action;
    private int responseCode;

    BillerException(String action, int responseCode) {
        this.action = action;
        this.responseCode = responseCode;
    }

    @Override
    public String getMessage() {
        return action + " unsuccessful - responseCode = " + billingCodeName(responseCode);
    }

    private String billingCodeName(int responseCode) {
        switch (responseCode) {
            case FEATURE_NOT_SUPPORTED:
                return "FEATURE_NOT_SUPPORTED";
            case SERVICE_DISCONNECTED:
                return "SERVICE_DISCONNECTED";
            case OK:
                return "OK";
            case USER_CANCELED:
                return "USER_CANCELED";
            case SERVICE_UNAVAILABLE:
                return "SERVICE_UNAVAILABLE";
            case BILLING_UNAVAILABLE:
                return "BILLING_UNAVAILABLE";
            case ITEM_UNAVAILABLE:
                return "ITEM_UNAVAILABLE";
            case DEVELOPER_ERROR:
                return "DEVELOPER_ERROR";
            case ERROR:
                return "ERROR";
            case ITEM_ALREADY_OWNED:
                return "ITEM_ALREADY_OWNED";
            case ITEM_NOT_OWNED:
                return "ITEM_NOT_OWNED";
            default:
                return "Not Know??";
        }
    }
}
