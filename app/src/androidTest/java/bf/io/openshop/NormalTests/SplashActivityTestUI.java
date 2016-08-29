package bf.io.openshop.NormalTests;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import bf.io.openshop.CONST;
import bf.io.openshop.ListMatcher;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.WaitActivityIsResumedIdlingResource;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.testing.FakeRequestQueue;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.SplashActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTestUI {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     * Third parameter is set to false which means the activity is not started automatically
     */
    @Rule
    public IntentsTestRule<SplashActivity> mActivityTestRule = new IntentsTestRule<>(SplashActivity.class, true, false);

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
//        SettingsMy.setActualShop(null);  USE this and look how to start activity with different intent.
    }

    private void preparationFirstRun() {
        SettingsMy.setActualShop(null);
    }

    @Test
    public void successfulStartCheck() {
        preparationFirstRun();
        mActivityTestRule.launchActivity(null);

        // Check if spinner is prepared
        onView(ViewMatchers.withId(R.id.splash_shop_selection_spinner)).check(matches(ListMatcher.withAdapterListSize(3))); // Two items from file "shops.txt" and header.
        onView(withId(R.id.splash_shop_selection_spinner)).check(matches(withSpinnerText(containsString("English"))));

        // Check if continue button is prepared
        onView(withId(R.id.splash_continue_to_shop_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void startMainActivityCheck() {
        preparationFirstRun();
        mActivityTestRule.launchActivity(null);

        onView(withId(R.id.splash_continue_to_shop_btn)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void UtmCampaignCheck() {
        preparationFirstRun();
        Intent intent = new Intent();
        intent.setData(Uri.parse("openshop:?target_url=https%3A%2F%2Fdevelopers.facebook.com%2Fandroid"));
        Bundle appLinkBundle = new Bundle();
        String utmParams = "https://play.google.com/store/apps/details?id=bf.io.openshop&referrer=utm_source%3DtestSource" +
                "%26utm_medium%3DtestMedium%26utm_term%3Drunning%252Bshoes%26utm_content%3Dlogolink%26utm_campaign%3DtestCampaign";
        appLinkBundle.putString("target_url", utmParams);
        intent.putExtra("al_applink_data", appLinkBundle);
        mActivityTestRule.launchActivity(intent);

        assertEquals(Analytics.getCampaignUri(), utmParams);

        onView(withId(R.id.splash_continue_to_shop_btn)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

//    // TODO finish notificationTest. It show empty "Recorded intents" event when in debug is startActivity() called.
//    @Test
//    public void notificationTest() {
//        preparationFirstRun();
//
//        // Prepare test values
//        String testTarget = "21:detail:5214079";
//        String testTitle = "Instrumentation test";
//        String utmParams = "utm_source=API&utm_medium=notification&utm_campaign=InstrumentationTest";
//
//        // Prepare test intent and start activity for the test
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra(EndPoints.NOTIFICATION_LINK, testTarget);
//        intent.putExtra(EndPoints.NOTIFICATION_TITLE, testTitle);
//        intent.putExtra(EndPoints.NOTIFICATION_UTM, utmParams);
//        mActivityTestRule.launchActivity(intent);
//
//        // Check that campaign is setted
//        assertEquals(Analytics.getCampaignUri(), utmParams);
//        WaitActivityIsResumedIdlingResource waitingActivity = new WaitActivityIsResumedIdlingResource(MainActivity.class.getName());
//        Espresso.registerIdlingResources(waitingActivity);
//
//        intended(allOf(
//                hasComponent(MainActivity.class.getName()),
//                hasExtras(allOf(
//                        hasEntry(equalTo(CONST.BUNDLE_PASS_TARGET), equalTo(testTarget)),
//                        hasEntry(equalTo(CONST.BUNDLE_PASS_TITLE), equalTo(testTitle))))
//                )
//        );
//        Espresso.unregisterIdlingResources(waitingActivity);
//    }


    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(MyApplication.getInstance().getCountingIdlingResource());
    }
}
