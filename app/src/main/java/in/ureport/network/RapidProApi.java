package in.ureport.network;

import java.util.List;

import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.Group;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by johncordeiro on 18/08/15.
 */
interface RapidProApi {

    @GET("/groups.json")
    Response<Group> listGroups(@Header("Authorization") String apiKey);

    @POST("/contacts.json")
    Contact saveContact(@Header("Authorization") String apiKey, @Body Contact contact);

    class Response<T> {

        private Integer count;

        private String next;

        private String previous;

        private List<T> results;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public List<T> getResults() {
            return results;
        }

        public void setResults(List<T> results) {
            this.results = results;
        }
    }
}
