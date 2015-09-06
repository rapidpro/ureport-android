package in.ureport.models.rapidpro;

/**
 * Created by johncordeiro on 31/08/15.
 */
public class Response {

    private String channel;

    private String from;

    private String text;

    public Response(String channel, String from, String text) {
        this.channel = channel;
        this.from = from;
        this.text = text;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
