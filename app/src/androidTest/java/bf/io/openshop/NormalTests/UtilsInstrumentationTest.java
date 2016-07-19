package bf.io.openshop.NormalTests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import bf.io.openshop.ux.SplashActivity;


import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

//import android.support.test.runner.AndroidJUnit4;
//import android.support.test.runner.AndroidJUnitRunner;
//import android.test.ActivityInstrumentationTestCase2;
//import bf.io.openshop.utils.Utils;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UtilsInstrumentationTest extends ActivityInstrumentationTestCase2<SplashActivity> {

// If you have used Android TestCases (like ActivityInstrumentationTestCase2 or ServiceTestCase) in the past, these have been deprecated.
// Instead you should switch to the new ActivityTestRule and ServiceTestRule.


    private static final String TAG = UtilsInstrumentationTest.class.getSimpleName();
    private SplashActivity mActivity;

    public UtilsInstrumentationTest() {
        super(SplashActivity.class);
    }


    @Before // Run this code before test. Only once (simple Before annotation run this code before every test execution).
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }


//    @Test
//    /**
//     * Test if your test fixture has been set up correctly.
//     */
//    public void testPreconditions() {
//        // Try to add a message to add context to your assertions.
//        // These messages will be shown if
//        // a tests fails and make it easy to
//        // understand why a test failed
//        assertNotNull("mTestActivity is null", mActivity);
//    }
//
//    @Test
//    /**
//     *
//     */
//    public void testUtilsTextInputMethods() {
////        try {
////            Thread.sleep(2000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        getInstrumentation().waitForIdleSync();
////        onView(withId(R.id.splash_continue_to_shop_btn)).perform(click());
////        onData(hasEntry());
////        onData(withText(R.string.Continue)).perform(click());
////        onView(withId(R.id.splash_continue_to_shop_btn)).perform(click());
//
////        TextInputLayout textInputLayout = new TextInputLayout(getInstrumentation().getContext(), null, R.style.MaterialTheme);
////        textInputLayout.addView(new EditText(getInstrumentation().getContext()));
////        String errorMsg = "Failed";
////        boolean result = Utils.checkTextInputLayoutValueRequirement(textInputLayout, errorMsg);
////        assertEquals(result, false);
////        assertTrue(errorMsg.equals(textInputLayout.getError()));
////        assertTrue(textInputLayout.isErrorEnabled());
//    }
//
//    @Test
//    /**
//     *
//     */
//    public void anotherTest() {
//
//    }
}


///**
// * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
//    @Test
//    public void sayHello() {
//        Log.e(TAG, "SayHello test start");
//
//
//        TextInputLayout textInputLayout = new TextInputLayout( getInstrumentation().getContext());
//        textInputLayout.addView(new EditText(getInstrumentation().getContext()));
//        String errorMsg = "Failed";
//        boolean result = Utils.checkTextInputLayoutValueRequirement(textInputLayout, errorMsg);
//        assertEquals(result, false);
//        assertTrue(errorMsg.equals(textInputLayout.getError()));
//        assertTrue(textInputLayout.isErrorEnabled());
//    }


// TODO test url spans
//    @Test
//    public void safeUrlSpanLinks() {
//        Spanned inputSpannable = Html.fromHtml("<p><em><strong>\"Lorem ipsum ipsum dolor </strong> </em><span> Lorem ipsum dolor sit amet. </span>" +
//                "<span> Consectetur adipiscing elit. </span><a class=\"extlink\" href=\"http://www.pinkbubble.cz/\">" +
//                "<strong> Nullam rhoncus venenatis felis </strong></a><span> je </span><strong>Nullam rhoncus venenatis felis</strong>" +
//                "<span> Proin lacinia lorem vitae arcu congue, in iaculis risus dignissim </span>" +
//                "<a class=\"extlink\" href=\"http://www.pinkbubble.cz/\">Pink Bubble</a>" +
//                "<span>Donec sed dolor sed libero tempor pharetra in vel ligula.</span>" +
//                "<p><strong>Curabitur vitae</strong>:  " +
//                "Curabitur  - 64 cm " +
//                "Maecenas  - 37 cm </p> " +
//                "<p><strong>eros </strong>: a mi</p>)");
//        Spanned inputSpannable = Html.fromHtml("<p><em><strong>Lorem ipsum ipsum dolor </strong> </em><span> Lorem ipsum dolor sit amet. </span>" +
//                "<span> Consectetur adipiscing elit. </span><a class=\"extlink\" href=\"http://www.pinkbubble.cz/\">");
//        SpannableString result = Utils.safeURLSpanLinks(inputSpannable, mMockActivity);
//        result.
//    }

