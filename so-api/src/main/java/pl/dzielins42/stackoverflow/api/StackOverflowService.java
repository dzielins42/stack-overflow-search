package pl.dzielins42.stackoverflow.api;

import io.reactivex.Single;
import pl.dzielins42.stackoverflow.api.model.Order;
import pl.dzielins42.stackoverflow.api.model.Sort;
import pl.dzielins42.stackoverflow.api.model.generated.SearchResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StackOverflowService {

    @GET("2.2/search?site=stackoverflow")
    Single<SearchResult> search(
            @Query("page") Integer page,
            @Query("pagesize") Integer pageSize,
            @Query("fromdate") Long fromDate,
            @Query("todate") Long toDate,
            @Query("order") Order order,
            @Query("min") Long min,
            @Query("max") Long max,
            @Query("sort") Sort sort,
            @Query("tagged") String tagged, // TODO semi-colon delimited list is expected, provide Converter to make conversion from List<String>
            @Query("nottagged") String notTagged, // TODO semi-colon delimited list is expected, provide Converter to make conversion from List<String>
            @Query("intitle") String inTitle
    );
}
