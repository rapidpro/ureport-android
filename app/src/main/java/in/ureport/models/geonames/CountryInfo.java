package in.ureport.models.geonames;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class CountryInfo implements Parcelable {

    private Long geonameId;

    private String countryName;

    private String countryCode;

    private String isoAlpha3;

    public Long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Long geonameId) {
        this.geonameId = geonameId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsoAlpha3() {
        return isoAlpha3;
    }

    public void setIsoAlpha3(String isoAlpha3) {
        this.isoAlpha3 = isoAlpha3;
    }

    public CountryInfo() {
    }

    public CountryInfo(Long geonameId, String countryName, String countryCode, String isoAlpha3) {
        this.geonameId = geonameId;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.isoAlpha3 = isoAlpha3;
    }

    public CountryInfo(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return countryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryInfo that = (CountryInfo) o;
        return countryCode.equals(that.countryCode);
    }

    @Override
    public int hashCode() {
        return countryCode.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.geonameId);
        dest.writeString(this.countryName);
        dest.writeString(this.countryCode);
        dest.writeString(this.isoAlpha3);
    }

    protected CountryInfo(Parcel in) {
        this.geonameId = (Long) in.readValue(Long.class.getClassLoader());
        this.countryName = in.readString();
        this.countryCode = in.readString();
        this.isoAlpha3 = in.readString();
    }

    public static final Creator<CountryInfo> CREATOR = new Creator<CountryInfo>() {
        public CountryInfo createFromParcel(Parcel source) {
            return new CountryInfo(source);
        }

        public CountryInfo[] newArray(int size) {
            return new CountryInfo[size];
        }
    };
}
