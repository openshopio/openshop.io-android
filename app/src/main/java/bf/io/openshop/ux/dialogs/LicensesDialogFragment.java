package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import bf.io.openshop.R;
import timber.log.Timber;

/**
 * Dialog shows open source libraries used in this project.
 */
public class LicensesDialogFragment extends DialogFragment {

    /**
     * Content view
     */
    private WebView licenseWebView;

    private ProgressBar progressBar;

    /**
     * Async loading of license data.
     */
    private AsyncTask<Void, Void, String> licenseAsyncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogNoTitle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            Window window = d.getWindow();
            if (window != null)
                window.setWindowAnimations(R.style.dialogFragmentAnimation);
            else
                Timber.e("Cannot set dialog animation");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_license, container, false);

        licenseWebView = (WebView) view.findViewById(R.id.license_web_view);
        progressBar = (ProgressBar) view.findViewById(R.id.license_progress);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLicenses();
    }

    /**
     * Load file with licenses. Using AsyncTask just in case of a big file.
     */
    private void loadLicenses() {
        licenseAsyncTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                InputStream rawResource = getActivity().getResources().openRawResource(R.raw.licenses);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(rawResource));

                String line;
                StringBuilder sb = new StringBuilder();

                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    bufferedReader.close();
                } catch (IOException e) {
                    Timber.e(e, "Load licenses failed.");
                }
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String licensesBody) {
                super.onPostExecute(licensesBody);
                if (getActivity() == null || isCancelled()) {
                    return;
                }
                progressBar.setVisibility(View.INVISIBLE);
                licenseWebView.setVisibility(View.VISIBLE);
                licenseWebView.loadDataWithBaseURL(null, licensesBody, "text/html", "utf-8", null);
                licenseAsyncTask = null;
            }
        }.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (licenseAsyncTask != null) {
            licenseAsyncTask.cancel(true);
        }
    }
}
