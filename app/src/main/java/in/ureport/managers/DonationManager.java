package in.ureport.managers;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 25/09/15.
 */
public class DonationManager {

    private enum CountryDonation {
        USA("https://www.unicefusa.org/donate/donate-now-help-save-childrens-lives/20281?ms=ref_dig_2015_web_header_donate"),
        GBR("http://www.unicef.org.uk/Donate/donate-now/");

        private String donationUrl;

        CountryDonation(String donationUrl) {
            this.donationUrl = donationUrl;
        }

        public String getDonationUrl() {
            return donationUrl;
        }
    }

    public static boolean isDonationAllowed(User user) {
        try {
            CountryDonation.valueOf(user.getCountry());
            return true;
        } catch(IllegalArgumentException ignored) {}
        return false;
    }

    public static String getDonationUrl(User user) {
        try {
            CountryDonation countryDonation = CountryDonation.valueOf(user.getCountry());
            return countryDonation.getDonationUrl();
        } catch(IllegalArgumentException ignored) {}
        return null;
    }

}
