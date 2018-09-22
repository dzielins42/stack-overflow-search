package pl.dzielins42.stackoverflow.api;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import pl.dzielins42.stackoverflow.api.model.generated.SearchResult;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.HttpException;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertEquals;

public class StackOverflowServiceTest {

    private MockWebServer mMockServer;
    private Retrofit mRetrofit;
    private StackOverflowService mService;

    @Before
    public void setUp() throws Exception {
        mMockServer = new MockWebServer();
        mMockServer.start();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mMockServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mService = mRetrofit.create(StackOverflowService.class);
    }

    @After
    public void tearDown() throws Exception {
        mMockServer.shutdown();

        mService = null;
        mRetrofit = null;
        mMockServer = null;
    }

    @Test
    public void search_valid_request_path() throws Exception {
        final String response = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("response_valid_search.json")
        );

        mMockServer.enqueue(new MockResponse().setBody(response));

        // Query parameters are not important as mocked response will be returned
        SearchResult result = mService.search(null, null).blockingGet();

        assertEquals(
                "/2.2/search?site=stackoverflow&pagesize=50",
                mMockServer.takeRequest().getPath()
        );
    }

    @Test
    public void search_valid_response() throws Exception {
        final String response = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("response_valid_search.json")
        );

        mMockServer.enqueue(new MockResponse().setBody(response));

        // Query parameters are not important as mocked response will be returned
        SearchResult result = mService.search(null, null).blockingGet();

        assertEquals(6, result.getQuestions().size());
    }

    @Test(expected = HttpException.class)
    public void search_404_server_error() throws Exception {
        mMockServer.enqueue(new MockResponse().setResponseCode(404));

        // Query parameters are not important as mocked response will be returned
        mService.search(null, null).blockingGet();

        // Exception should be thrown
    }
}