package bf.io.openshop;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;

import org.junit.Test;

import bf.io.openshop.utils.Utils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}

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