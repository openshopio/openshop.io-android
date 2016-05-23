package bf.io.openshop.ux.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.Page;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import timber.log.Timber;

/**
 * Fragment allow displaying useful information content like web page.
 * Requires input argument - id of selected page. Pages are created in OpenShop server administration.
 */
public class PageFragment extends Fragment {

    /**
     * Name for input argument.
     */
    private static final String PAGE_ID = "page_id";

    private static final long TERMS_AND_CONDITIONS = -131;

    private ProgressDialog progressDialog;

    /**
     * Reference of empty layout
     */
    private View layoutEmpty;
    /**
     * Reference of content layout
     */
    private View layoutContent;

    // Content view elements
    private TextView pageTitle;
    private WebView pageContent;

    /**
     * Create fragment instance which allow displaying useful information content like web page.
     *
     * @param pageId id of page for download and display. (Define in OpenShop server administration)
     * @return new fragment instance.
     */
    public static PageFragment newInstance(long pageId) {
        Bundle args = new Bundle();
        args.putLong(PageFragment.PAGE_ID, pageId);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create fragment instance which displays Terms and Conditions defined on server.
     *
     * @return fragment instance for display.
     */
    public static PageFragment newInstance() {
        Bundle args = new Bundle();
        args.putLong(PageFragment.PAGE_ID, TERMS_AND_CONDITIONS);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        MainActivity.setActionBarTitle(getString(R.string.app_name));

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        layoutEmpty = view.findViewById(R.id.page_empty);
        layoutContent = view.findViewById(R.id.page_content_layout);

        pageTitle = (TextView) view.findViewById(R.id.page_title);
        pageContent = (WebView) view.findViewById(R.id.page_content);

        // Check if fragment received some arguments.
        if (getArguments() != null && getArguments().getLong(PAGE_ID) != 0L) {
            getPage(getArguments().getLong(PAGE_ID));
        } else {
            Timber.e(new RuntimeException(), "Created fragment with null arguments.");
            setContentVisible(false);
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "", MsgUtils.ToastLength.LONG);
        }
        return view;
    }

    /**
     * Load page content by pageID.
     *
     * @param pageId define page to load.
     */
    private void getPage(long pageId) {
        String url;
        if (pageId == TERMS_AND_CONDITIONS) {
            url = String.format(EndPoints.PAGES_TERMS_AND_COND, SettingsMy.getActualNonNullShop(getActivity()).getId());
        } else {
            url = String.format(EndPoints.PAGES_SINGLE, SettingsMy.getActualNonNullShop(getActivity()).getId(), pageId);
        }

        progressDialog.show();

        GsonRequest<Page> getPage = new GsonRequest<>(Request.Method.GET, url, null, Page.class,
                new Response.Listener<Page>() {
                    @Override
                    public void onResponse(@NonNull Page response) {
                        handleResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                setContentVisible(false);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getPage.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getPage.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getPage, CONST.PAGE_REQUESTS_TAG);
    }

    /**
     * Method hides progress dialog and show received content.
     *
     * @param page page data received from server.
     */
    private void handleResponse(Page page) {
        if (page != null && page.getText() != null && !page.getText().isEmpty()) {
            setContentVisible(true);
            pageTitle.setText(page.getTitle());
            String data = page.getText();
            String header = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
                    + "<html>  <head>  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"
                    + "</head>  <body>";
            String footer = "</body></html>";

            pageContent.loadData(header + data + footer, "text/html; charset=UTF-8", null);
        } else {
            setContentVisible(false);
        }
        // Slow disappearing of progressDialog due to slow page content processing.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) progressDialog.cancel();
            }
        }, 200);
    }

    /**
     * Display content layout or empty layout.
     *
     * @param visible true for visible content.
     */
    private void setContentVisible(boolean visible) {
        if (layoutEmpty != null && layoutContent != null) {
            if (visible) {
                layoutEmpty.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            } else {
                layoutEmpty.setVisibility(View.VISIBLE);
                layoutContent.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.PAGE_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
