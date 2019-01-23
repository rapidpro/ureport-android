package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by johncordeiro on 16/08/15.
 */
public class ChatMembers implements Parcelable {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(users);
    }

    public ChatMembers() {
    }

    protected ChatMembers(Parcel in) {
        this.users = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<ChatMembers> CREATOR = new Creator<ChatMembers>() {
        public ChatMembers createFromParcel(Parcel source) {
            return new ChatMembers(source);
        }

        public ChatMembers[] newArray(int size) {
            return new ChatMembers[size];
        }
    };

    @Override
    public String toString() {
        return "ChatMembers{" +
                "users=" + users +
                '}';
    }
}
