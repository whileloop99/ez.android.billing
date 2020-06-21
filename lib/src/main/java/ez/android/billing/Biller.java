package ez.android.billing;

import android.content.Context;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;

import static com.android.billingclient.api.BillingClient.BillingResponseCode.OK;


public class Biller implements BillingClientStateListener {

    private BillingClient mBillingClient;
    private Context mContext;
    BillerConnectionListener mListener;

    private static Biller _instance;
    private ConnectedBiller mConnectedBiller;

    public static Biller getInstance(Context context) {
        if(_instance == null) {
            _instance = new Biller();
        }
        _instance.setup(context);
        return _instance;
    }

    private void setup(Context context) {
        mContext = context;
        if(mConnectedBiller == null) {
            mConnectedBiller = new ConnectedBiller();
        }
        mConnectedBiller.setContext(context);
        mBillingClient = BillingClient
                .newBuilder(mContext)
                .enablePendingPurchases()
                .setListener(mConnectedBiller)
                .build();

        mConnectedBiller.setBillingClient(mBillingClient);

    }

    private Biller() {

    }

    public void startConnection(BillerConnectionListener listener) {
        mListener = listener;
        if(!mBillingClient.isReady()) {
            mBillingClient.startConnection(this);
        }
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult != null && billingResult.getResponseCode() == OK) {
            if(mListener != null) {
                mListener.onBillerConnected(mConnectedBiller);
            }
        } else {
            mListener.onBillerConnected(null);
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
//        startConnection();
    }

}
