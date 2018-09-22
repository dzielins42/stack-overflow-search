package pl.dzielins42.stackoverflow.api;

import io.reactivex.Single;
import pl.dzielins42.stackoverflow.api.model.generated.SearchResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StackOverflowService {

    @GET("2.2/search?site=stackoverflow&pagesize=50")
    Single<SearchResult> search(
            @Query("page") Integer page,
            @Query("intitle") String inTitle
    );
}
