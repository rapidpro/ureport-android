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

        enum Priority {
            normal,
            high,
        }

        @Expose
        private String to;

        @Expose
        private T data;

        @Expose
        private Priority priority = Priority.high;

        @Expose
        private Notification notification;

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

        public Priority getPriority() {
            return priority;
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public Notification getNotification() {
            return notification;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }
    }

    class Notification {

        @Expose
        private String title;

        @Expose
        private String body;

        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
