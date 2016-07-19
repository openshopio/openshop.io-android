package bf.io.openshop.testing;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;

import timber.log.Timber;

/**
 * Class simulates the network layer request queue - testing purpose.
 */
public class FakeRequestQueue extends RequestQueue {
    public FakeRequestQueue(Context context) {
        super(new NoCache(), new BasicNetwork(new FakeHttpStack(context)));
        start();
    }

    @Override
    public void start() {
        Timber.d("Request start");
        super.start();
    }

    @Override
    public void stop() {
        Timber.d("Request stop");
        super.stop();
    }

    @Override
    public Cache getCache() {
        Timber.d("Request getCache");
        return super.getCache();
    }

    @Override
    public void cancelAll(RequestFilter filter) {
        Timber.d("Request cancel with filter %s", filter);
        super.cancelAll(filter);
    }

    @Override
    public void cancelAll(Object tag) {
        Timber.d("Request cancel with tag %s", tag);
        super.cancelAll(tag);
    }

    @Override
    public Request add(Request request) {
        Timber.d("FakeRequestQueue is used");
        Timber.d("New request %s is added with priority %s", request.getUrl(), request.getPriority());
        try {
            if (request.getBody() == null) {
                Timber.d("Body is null");
            } else {
                Timber.d("Body: %s", new String(request.getBody()));
            }
        } catch (AuthFailureError e) {
            // cannot do anything
        }
        request.setShouldCache(false);
        return super.add(request);
    }
}
