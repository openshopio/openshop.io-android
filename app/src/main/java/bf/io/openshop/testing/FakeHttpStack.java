package bf.io.openshop.testing;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Class simulates the network layer with instant answers - testing purpose.
 */
public class FakeHttpStack implements HttpStack {

    private final Context context;

    FakeHttpStack(Context context) {
        this.context = context;
    }

    /**
     * Copies all characters between the {@link Readable} and {@link Appendable} objects. Does not
     * close or flush either object.
     *
     * @param from the object to read from
     * @param to   the object to write to
     * @return the number of characters copied
     * @throws IOException if an I/O error occurs
     */
    public static long copy(Readable from, Appendable to) throws IOException {
        if (from == null) {
            throw new NullPointerException();
        }
        if (to == null) {
            throw new NullPointerException();
        }
        CharBuffer buf = CharBuffer.allocate(0x800); // 2K chars (4K bytes)
        long total = 0;
        while (from.read(buf) != -1) {
            buf.flip();
            to.append(buf);
            total += buf.remaining();
            buf.clear();
        }
        return total;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> stringStringMap) throws IOException, AuthFailureError {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        List<Header> headers = defaultHeaders();
        response.setHeaders(headers.toArray(new Header[headers.size()]));
        //response.setLocale(Locale.JAPAN);
        response.setEntity(createEntity(request));
        return response;
    }

    /**
     * Create default headers for server response.
     *
     * @return list with headers.
     */
    private List<Header> defaultHeaders() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd mmm yyyy HH:mm:ss zzz");
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Date", dateFormat.format(new Date())));
        return headers;
    }

    /**
     * Create request content for server responses.
     *
     * @param request request to server.
     * @return response data.
     * @throws UnsupportedEncodingException
     */
    private HttpEntity createEntity(Request request) throws IOException {
        String fileName;
        if (request.getUrl().endsWith("/shops")) {
            fileName = "shops.txt";
        } else if (request.getUrl().matches(".*/shops/\\d+$")){
            fileName = "shop_single.txt";
        } else {
            throw new NullPointerException("Unknown request for test class:" + request.getUrl());
        }

        return loadResponse(this, fileName);
    }

    /**
     * Loads request data from prepared files.
     *
     * @param fakeHttpStack object of the {@link FakeHttpStack} class.
     * @param fileName      name of the file from which to read the requests data.
     * @return testing data for requests response.
     * @throws IOException
     */
    private StringEntity loadResponse(FakeHttpStack fakeHttpStack, String fileName) throws IOException {
        InputStream stream = fakeHttpStack.getClass().getClassLoader().getResourceAsStream(fileName);

        StringBuilder sb = new StringBuilder();
        copy(new InputStreamReader(stream, "UTF-8"), sb);
        String string = sb.toString();

        return new StringEntity(string);
    }
}