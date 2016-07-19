package bf.io.openshop.NoInternetTests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.android.volley.RequestQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.testing.FakeRequestQueue;
import bf.io.openshop.ux.SplashActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;


/**
 * Test class requires device with data turned off.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityNoConnectionTestUI {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @BeforeClass
    public static void fakeNetworkLayer() {
        RequestQueue requestQueue = new FakeRequestQueue(MyApplication.getInstance());
        MyApplication.getInstance().setRequestQueue(requestQueue);
    }

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
     * more reliable.
     */
    @Before
    public void registerIdlingResource() {
        Espresso.registerIdlingResources(MyApplication.getInstance().getCountingIdlingResource());
    }

    /**
     * Check if re-run button is displayed and that continue button isn't displayed.
     */
    @Test
    public void NoConnectionCheck() {
        onView(ViewMatchers.withId(R.id.splash_re_run_btn)).check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.No_network_connection)).check(matches(isDisplayed()));
        onView(withId(R.id.splash_continue_to_shop_btn)).check(matches(not(isDisplayed())));
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(MyApplication.getInstance().getCountingIdlingResource());
    }
}
