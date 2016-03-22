package bf.io.openshop.interfaces;

import com.android.volley.VolleyError;

public interface RequestListener {

    void requestSuccess(long newId);

    void requestFailed(VolleyError error);
}

