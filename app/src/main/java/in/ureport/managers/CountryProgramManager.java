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
            countryPrograms.add(new CountryProgram("GLOBAL", R.style.AppTheme, "U-Report Global"
                    , R.string.global_api_token, R.string.global_external_channel, "UReportGlobal"));
            countryPrograms.add(new CountryProgram("BDI", R.style.AppTheme_Burundi, "Burundi"
                    , R.string.burundi_api_token, R.string.burundi_external_channel, "UReportGlobal"));
            countryPrograms.add(new CountryProgram("CMR", R.style.AppTheme_Cameroun, "Cameroun"
                    , R.string.cameroun_api_token, R.string.cameroun_external_channel, "UReportCameroon"));
            countryPrograms.add(new CountryProgram("CHL", R.style.AppTheme_Chile, "Chile"
                    , R.string.chile_api_token, R.string.chile_external_channel, "UReportChile"));
            countryPrograms.add(new CountryProgram("COD", R.style.AppTheme_Drc, "DRC"
                    , R.string.drc_api_token, R.string.drc_external_channel, "UReportDRC"));
            countryPrograms.add(new CountryProgram("IDN", R.style.AppTheme_Indonesia, "Indonesia"
                    , R.string.indonesia_api_token, R.string.indonesia_external_channel, "UReport_id"));
            countryPrograms.add(new CountryProgram("LBR", R.style.AppTheme_Liberia, "Liberia"
                    , R.string.liberia_api_token, R.string.liberia_external_channel, "UReportLiberia"));
            countryPrograms.add(new CountryProgram("MLI", R.style.AppTheme_Mali, "Mali"
                    , R.string.mali_api_token, R.string.mali_external_channel, "UReportMali"));
            countryPrograms.add(new CountryProgram("MEX", R.style.AppTheme_Mexico, "México"
                    , R.string.mexico_api_token, R.string.mexico_external_channel, "UreportMexico"));
            countryPrograms.add(new CountryProgram("NGA", R.style.AppTheme_Nigeria, "Nigeria"
                    , R.string.nigeria_api_token, R.string.nigeria_external_channel, "UReportNigeria"));
            countryPrograms.add(new CountryProgram("CAF", R.style.AppTheme_RepubliqueCentrafricaine, "République Centrafricaine"
                    , R.string.republique_centrafricaine_api_token, R.string.republique_centrafricaine_external_channel, "Ureport_rca"));
            countryPrograms.add(new CountryProgram("SEN", R.style.AppTheme_Senegal, "Sénégal"
                    , R.string.senegal_api_token, R.string.senegal_external_channel, "ureportsenegal"));
            countryPrograms.add(new CountryProgram("SLE", R.style.AppTheme_SierraLeone, "Sierra Leone"
                    , R.string.sierra_leone_api_token, R.string.sierra_leone_external_channel, "UreportSL"));
            countryPrograms.add(new CountryProgram("SWZ", R.style.AppTheme_Swaiziland, "Swaziland"
                    , R.string.swaziland_api_token, R.string.swaziland_external_channel, "Ureportszd"));
            countryPrograms.add(new CountryProgram("UGA", R.style.AppTheme_Uganda, "Uganda"
                    , R.string.uganda_api_token, R.string.uganda_external_channel, "UReportUganda"));
            countryPrograms.add(new CountryProgram("ZMB", R.style.AppTheme_Zambia, "Zambia"
                    , R.string.zambia_api_token, R.string.zambia_external_channel, "ZambiaUReport"));
            countryPrograms.add(new CountryProgram("ZWE", R.style.AppTheme_Zimbabwe, "Zimbabwe"
                    , R.string.zimbabwe_api_token, R.string.zimbabwe_external_channel, "Ureportzim"));
        }
        return countryPrograms;
    }

}
