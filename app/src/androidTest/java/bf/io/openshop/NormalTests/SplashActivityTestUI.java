package bf.io.openshop.NormalTests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
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

import bf.io.openshop.ListMatcher;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.testing.FakeRequestQueue;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.SplashActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTestUI {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<SplashActivity> mActivityTestRule = new IntentsTestRule<>(SplashActivity.class, false, true);

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

    @Test
    public void successfulStartCheck() {
        // Check if spinner is prepared
        onView(ViewMatchers.withId(R.id.splash_shop_selection_spinner)).check(matches(ListMatcher.withAdapterListSize(3))); // Two items from file "shops.txt" and header.
        onView(withId(R.id.splash_shop_selection_spinner)).check(matches(withSpinnerText(containsString("English"))));

        // Check if continue button is prepared
        onView(withId(R.id.splash_continue_to_shop_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void StartMainActivityCheck() {
        onView(withId(R.id.splash_continue_to_shop_btn)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //      instead of threadSleep  loopMainThreadForAtLeast(millis);

    // Click on the item and check for the toast
    //    @Test
    //    public void ensureListViewIsPresent() throws Exception {
    //        onData(hasToString(containsString("Done"))).perform(click());
    //        onView(withText(startsWith("Clicked:"))).
    //                inRoot(withDecorView(not(is(rule.getActivity().getWindow().getDecorView())))).
    //                check(matches(isDisplayed()));
    //    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(MyApplication.getInstance().getCountingIdlingResource());
    }
}
