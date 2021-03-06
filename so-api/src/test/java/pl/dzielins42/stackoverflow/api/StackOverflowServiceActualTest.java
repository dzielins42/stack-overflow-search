package pl.dzielins42.stackoverflow.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.dzielins42.stackoverflow.api.model.generated.SearchResult;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertTrue;

public class StackOverflowServiceActualTest {

    private Retrofit mRetrofit;
    private StackOverflowService mService;

    @Before
    public void setUp() throws Exception {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        mService = mRetrofit.create(StackOverflowService.class);
    }

    @After
    public void tearDown() throws Exception {
        mService = null;
        mRetrofit = null;
    }

    @Test
    public void search_valid_response() throws Exception {
        SearchResult result = mService.search(
                null, "degeneration"
        ).blockingGet();

        assertTrue(result.getQuestions().size() >= 5);
    }
}