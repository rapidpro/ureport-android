package in.ureport.managers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.ResourceUtil;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.models.CountryProgram;

/**
 * Created by johncordeiro on 7/23/15.
 */
public class CountryProgramManager {

    private static CountryProgram countryProgram;
    private static List<CountryProgram> countryPrograms;

    public static void switchCountryProgram(CountryProgram countryProgram) {
        CountryProgramManager.countryProgram = countryProgram;
    }

    public static void switchCountryProgram(String countryCode) {
        CountryProgramManager.countryProgram = getCountryProgramForCode(countryCode);
    }

    public static void setThemeIfNeeded(Activity activity) {
        CountryProgram countryProgram = getCurrentCountryProgram();
        if(countryProgram != null) {
            activity.setTheme(countryProgram.getTheme());

            ResourceUtil resourceUtil = new ResourceUtil(activity);
            StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
            statusBarDesigner.setStatusBarColor(activity, resourceUtil.getColorByAttr(R.attr.colorPrimaryDark));
        }
    }

    @NonNull
    public static CountryProgram getCurrentCountryProgram() {
        return countryProgram != null ? countryProgram : getAvailableCountryPrograms().get(0);
    }

    public static CountryProgram getCountryProgramForCode(String countryCode) {
        CountryProgram countryProgram = new CountryProgram(countryCode);
        int indexOfCountryProgram = getAvailableCountryPrograms().indexOf(countryProgram);
        indexOfCountryProgram = indexOfCountryProgram > 0 ? indexOfCountryProgram : 0;

        return getAvailableCountryPrograms().get(indexOfCountryProgram);
    }

    public static List<CountryProgram> getAvailableCountryPrograms() {
        if(countryPrograms == null) {
            countryPrograms = new ArrayList<>();
            countryPrograms.add(new CountryProgram("GLOBAL", R.style.AppTheme, "U-Report Global"));
            countryPrograms.add(new CountryProgram("BDI", R.style.AppTheme_Burundi, "Burundi"));
            countryPrograms.add(new CountryProgram("CMR", R.style.AppTheme_Cameroun, "Cameroun"));
            countryPrograms.add(new CountryProgram("CHL", R.style.AppTheme_Chile, "Chile"));
            countryPrograms.add(new CountryProgram("COD", R.style.AppTheme_Drc, "DRC"));
            countryPrograms.add(new CountryProgram("IDN", R.style.AppTheme_Indonesia, "Indonesia"));
            countryPrograms.add(new CountryProgram("LBR", R.style.AppTheme_Liberia, "Liberia"));
            countryPrograms.add(new CountryProgram("MLI", R.style.AppTheme_Mali, "Mali"));
            countryPrograms.add(new CountryProgram("MEX", R.style.AppTheme_Mexico, "México"));
            countryPrograms.add(new CountryProgram("NGA", R.style.AppTheme_Nigeria, "Nigeria"));
            countryPrograms.add(new CountryProgram("CAF", R.style.AppTheme_RepubliqueCentrafricaine, "République Centrafricaine"));
            countryPrograms.add(new CountryProgram("SEN", R.style.AppTheme_Senegal, "Sénégal"));
            countryPrograms.add(new CountryProgram("SLE", R.style.AppTheme_SierraLeone, "Sierra Leone"));
            countryPrograms.add(new CountryProgram("SWZ", R.style.AppTheme_Swaiziland, "Swaziland"));
            countryPrograms.add(new CountryProgram("UGA", R.style.AppTheme_Uganda, "Uganda"));
            countryPrograms.add(new CountryProgram("ZMB", R.style.AppTheme_Zambia, "Zambia"));
            countryPrograms.add(new CountryProgram("ZWE", R.style.AppTheme_Zimbabwe, "Zimbabwe"));
        }
        return countryPrograms;
    }

}
