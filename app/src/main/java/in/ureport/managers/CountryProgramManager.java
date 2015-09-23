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

    public static final int INVALID_CHANNEL = -1;

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

    public static boolean allowsPollParticipation() {
        return getCurrentCountryProgram().getChannel() != INVALID_CHANNEL;
    }

    public static List<CountryProgram> getAvailableCountryPrograms() {
        if(countryPrograms == null) {
            countryPrograms = new ArrayList<>();
            countryPrograms.add(new CountryProgram("GLOBAL", R.style.AppTheme, "U-Report Global"
                    , R.string.global_api_token, R.string.global_external_channel, "UReportGlobal"));
            countryPrograms.add(new CountryProgram("BDI", R.style.AppTheme_Burundi, "Burundi"
                    , R.string.burundi_api_token, INVALID_CHANNEL, "UReportGlobal"));
            countryPrograms.add(new CountryProgram("CMR", R.style.AppTheme_Cameroun, "Cameroun"
                    , R.string.cameroun_api_token, INVALID_CHANNEL, "UReportCameroon"));
            countryPrograms.add(new CountryProgram("CHL", R.style.AppTheme_Chile, "Chile"
                    , R.string.chile_api_token, R.string.chile_external_channel, "UReportChile"));
            countryPrograms.add(new CountryProgram("COD", R.style.AppTheme_Drc, "DRC"
                    , R.string.drc_api_token, INVALID_CHANNEL, "UReportDRC"));
            countryPrograms.add(new CountryProgram("IDN", R.style.AppTheme_Indonesia, "Indonesia"
                    , R.string.indonesia_api_token, R.string.indonesia_external_channel, "UReport_id"));
            countryPrograms.add(new CountryProgram("LBR", R.style.AppTheme_Liberia, "Liberia"
                    , R.string.liberia_api_token, INVALID_CHANNEL, "UReportLiberia"));
            countryPrograms.add(new CountryProgram("MLI", R.style.AppTheme_Mali, "Mali"
                    , R.string.mali_api_token, INVALID_CHANNEL, "UReportMali"));
            countryPrograms.add(new CountryProgram("MEX", R.style.AppTheme_Mexico, "México"
                    , R.string.mexico_api_token, INVALID_CHANNEL, "UreportMexico"));
            countryPrograms.add(new CountryProgram("NGA", R.style.AppTheme_Nigeria, "Nigeria"
                    , R.string.nigeria_api_token, R.string.nigeria_external_channel, "UReportNigeria"));
            countryPrograms.add(new CountryProgram("PAK", R.style.AppTheme_Pakistan, "Pakistan"
                    , R.string.pakistan_api_token, INVALID_CHANNEL, "PakAvaz"));
            countryPrograms.add(new CountryProgram("CAF", R.style.AppTheme_RepubliqueCentrafricaine, "République Centrafricaine"
                    , R.string.republique_centrafricaine_api_token, INVALID_CHANNEL, "Ureport_rca"));
            countryPrograms.add(new CountryProgram("SEN", R.style.AppTheme_Senegal, "Sénégal"
                    , R.string.senegal_api_token, INVALID_CHANNEL, "ureportsenegal"));
            countryPrograms.add(new CountryProgram("SLE", R.style.AppTheme_SierraLeone, "Sierra Leone"
                    , R.string.sierra_leone_api_token, INVALID_CHANNEL, "UreportSL"));
            countryPrograms.add(new CountryProgram("SWZ", R.style.AppTheme_Swaiziland, "Swaziland"
                    , R.string.swaziland_api_token, INVALID_CHANNEL, "Ureportszd"));
            countryPrograms.add(new CountryProgram("UGA", R.style.AppTheme_Uganda, "Uganda"
                    , R.string.uganda_api_token, INVALID_CHANNEL, "UReportUganda"));
            countryPrograms.add(new CountryProgram("ZMB", R.style.AppTheme_Zambia, "Zambia"
                    , R.string.zambia_api_token, INVALID_CHANNEL, "ZambiaUReport"));
            countryPrograms.add(new CountryProgram("ZWE", R.style.AppTheme_Zimbabwe, "Zimbabwe"
                    , R.string.zimbabwe_api_token, INVALID_CHANNEL, "Ureportzim"));
        }
        return countryPrograms;
    }

}
