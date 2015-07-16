package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by johncordeiro on 7/15/15.
 */
@Table(name = "Marker")
public class Marker extends Model implements Parcelable {

    @Column(name = "name")
    private String name;

    public Marker(String name) {
        this.name = name;
    }

    public Marker() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Marker marker = (Marker) o;
        return getName().equals(marker.getName());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected Marker(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<Marker> CREATOR = new Creator<Marker>() {
        public Marker createFromParcel(Parcel source) {
            return new Marker(source);
        }

        public Marker[] newArray(int size) {
            return new Marker[size];
        }
    };

}
