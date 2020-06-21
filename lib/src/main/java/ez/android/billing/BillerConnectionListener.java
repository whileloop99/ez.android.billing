package ez.android.billing;

import androidx.annotation.Nullable;

public interface BillerConnectionListener {
    void onBillerConnected(@Nullable ConnectedBiller biller);
}
