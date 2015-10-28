package in.ureport.managers;

import android.app.Activity;
import android.support.annotation.NonNull;

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

    public static final int INVALID_ORGANIZATION = -1;

    private static CountryProgram countryProgram;
    private static List<CountryProgram> countryPrograms;

    public static void switchCountryProgram(CountryProgram countryProgram) {
        CountryProgramManager.countryProgram = countryProgram;
    }

    public static void switchCountryProgram(String countryCode) {
        CountryProgramManager.countryProgram = getCountryProgramForCode(countryCode);
    }

    public static void switchToUserCountryProgram() {
        CountryProgramManager.countryProgram = getCountryProgramForCode(UserManager.getCountryCode());
    }

    public static void setThemeIfNeeded(Activity activity) {
        CountryProgram countryProgram = getCurrentCountryProgram();
        activity.setTheme(countryProgram.getTheme());

        ResourceUtil resourceUtil = new ResourceUtil(activity);
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColor(activity, resourceUtil.getColorByAttr(R.attr.colorPrimaryDark));
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
            countryPrograms.add(buildCountryProgram("GLOBAL", R.style.AppTheme, "U-Report Global", 13, "UReportGlobal", "U-Reporters"));
            countryPrograms.add(buildCountryProgram("BDI", R.style.AppTheme_Burundi, "Burundi", 5, "UReportGlobal", null));
            countryPrograms.add(buildCountryProgram("CMR", R.style.AppTheme_Cameroun, "Cameroun", 10, "UReportCameroon", null));
            countryPrograms.add(buildCountryProgram("CHL", R.style.AppTheme_Chile, "Chile", 12, "UReportChile", "UReporters"));
            countryPrograms.add(buildCountryProgram("COD", R.style.AppTheme_Drc, "DRC", INVALID_ORGANIZATION, "UReportDRC", null));
            countryPrograms.add(buildCountryProgram("IDN", R.style.AppTheme_Indonesia, "Indonesia", 15, "UReport_id", "UReporters_Indonesia"));
            countryPrograms.add(buildCountryProgram("LBR", R.style.AppTheme_Liberia, "Liberia", 6, "UReportLiberia", null));
            countryPrograms.add(buildCountryProgram("MLI", R.style.AppTheme_Mali, "Mali", 3, "UReportMali", null));
            countryPrograms.add(buildCountryProgram("MEX", R.style.AppTheme_Mexico, "México",  9, "UreportMexico", null));
            countryPrograms.add(buildCountryProgram("NGA", R.style.AppTheme_Nigeria, "Nigeria", 1, "UReportNigeria", "UReporters"));
            countryPrograms.add(buildCountryProgram("PAK", R.style.AppTheme_Pakistan, "Pakistan", 16, "PakAvaz", null));
            countryPrograms.add(buildCountryProgram("CAF", R.style.AppTheme_RepubliqueCentrafricaine, "République Centrafricaine", 8, "Ureport_rca", null));
            countryPrograms.add(buildCountryProgram("SEN", R.style.AppTheme_Senegal, "Sénégal", 14, "ureportsenegal", null));
            countryPrograms.add(buildCountryProgram("SLE", R.style.AppTheme_SierraLeone, "Sierra Leone", 7, "UreportSL", null));
            countryPrograms.add(buildCountryProgram("SWZ", R.style.AppTheme_Swaiziland, "Swaziland", 4, "Ureportszd", null));
            countryPrograms.add(buildCountryProgram("UGA", R.style.AppTheme_Uganda, "Uganda", INVALID_ORGANIZATION, "UReportUganda", null));
            countryPrograms.add(buildCountryProgram("ZMB", R.style.AppTheme_Zambia, "Zambia", INVALID_ORGANIZATION, "ZambiaUReport", null));
            countryPrograms.add(buildCountryProgram("ZWE", R.style.AppTheme_Zimbabwe, "Zimbabwe", 2, "Ureportzim", null));
        }
        return countryPrograms;
    }

    @NonNull
    private static CountryProgram buildCountryProgram(String global, int appTheme, String name, int organization
            , String twitter, String group) {
        return new CountryProgram(global, appTheme, name, organization, twitter, group);
    }

}
