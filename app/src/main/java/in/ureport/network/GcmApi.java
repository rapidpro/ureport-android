package in.ureport.network;

import com.google.gson.annotations.Expose;

import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by johncordeiro on 21/08/15.
 */
public interface GcmApi {

    @POST("/gcm/send")
    Response sendData(@Header("Authorization") String senderId, @Body Input input);

    class Response {
        String message_id;
    }

    class Input<T> {

        @Expose
        private String to;

        @Expose
        private T data;

        public Input(String to, T data) {
            this.to = to;
            this.data = data;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

}
