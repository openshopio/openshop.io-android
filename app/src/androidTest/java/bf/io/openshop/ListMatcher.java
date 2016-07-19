package bf.io.openshop;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Class with simple list matchers.
 */
public class ListMatcher {

    /**
     * Simple {@link AdapterView} matcher.
     *
     * @param size expected list size.
     * @return true if sizes match.
     */
    public static Matcher<View> withAdapterListSize(final int size) {
        return new TypeSafeMatcher<View>() {
            int length;

            @Override
            public boolean matchesSafely(final View view) {
                length = ((AdapterView) view).getAdapter().getCount();
                return length == size;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("AdapterList should have " + size + " items, the actual size is " + length);
            }
        };
    }

    /**
     * Simple {@link ListView} matcher.
     *
     * @param size expected list size.
     * @return true if sizes match.
     */
    public static Matcher<View> withListSize(final int size) {
        return new TypeSafeMatcher<View>() {
            int length;

            @Override
            public boolean matchesSafely(final View view) {
                length = ((ListView) view).getCount();
                return length == size;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("ListView should have " + size + " items, the actual size is " + length);
            }
        };
    }
}
