package in.ureport.models.rapidpro;

/**
 * Created by johncordeiro on 31/08/15.
 */
public class Response {

    private String from;

    private String text;

    public Response(String from, String text) {
        this.from = from;
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
