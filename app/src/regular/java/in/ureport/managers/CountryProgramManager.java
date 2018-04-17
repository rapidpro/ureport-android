package in.ureport.managers;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.ResourceUtil;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.models.CountryProgram;
import in.ureport.models.rapidpro.AgeGroup;

import static in.ureport.R.string.argentina_channel;
import static in.ureport.R.string.brazil_channel;
import static in.ureport.R.string.cotedivoire_channel;
import static in.ureport.R.string.elsalvador_channel;
import static in.ureport.R.string.france_channel;
import static in.ureport.R.string.guatemala_channel;
import static in.ureport.R.string.ireland_channel;
import static in.ureport.R.string.jamaica_channel;
import static in.ureport.R.string.moldova_channel;
import static in.ureport.R.string.new_zealand_channel;
import static in.ureport.R.string.rapidpro_host_address1;
import static in.ureport.R.string.rapidpro_host_address2;
import static in.ureport.R.string.syria_channel;
import static in.ureport.R.string.thailand_channel;
import static in.ureport.R.string.unitedkingdom_channel;
import static in.ureport.R.string.ureport_host_address1;
import static in.ureport.R.string.ureport_host_address2;
import static in.ureport.R.style.AppTheme;
import static in.ureport.R.style.AppTheme_Brasil;
import static in.ureport.R.style.AppTheme_Burundi;
import static in.ureport.R.style.AppTheme_Cameroun;
import static in.ureport.R.style.AppTheme_Chile;
import static in.ureport.R.style.AppTheme_CoteDIvoire;
import static in.ureport.R.style.AppTheme_ElSalvador;
import static in.ureport.R.style.AppTheme_Guatemala;
import static in.ureport.R.style.AppTheme_Indonesia;
import static in.ureport.R.style.AppTheme_Ireland;
import static in.ureport.R.style.AppTheme_Liberia;
import static in.ureport.R.style.AppTheme_Mali;
import static in.ureport.R.style.AppTheme_Mexico;
import static in.ureport.R.style.AppTheme_Nigeria;
import static in.ureport.R.style.AppTheme_Pakistan;
import static in.ureport.R.style.AppTheme_PapuaNewGuinea;
import static in.ureport.R.style.AppTheme_RepubliqueCentrafricaine;
import static in.ureport.R.style.AppTheme_Senegal;
import static in.ureport.R.style.AppTheme_SierraLeone;
import static in.ureport.R.style.AppTheme_Swaiziland;
import static in.ureport.R.style.AppTheme_Syria;
import static in.ureport.R.style.AppTheme_Thailand;
import static in.ureport.R.style.AppTheme_Uganda;
import static in.ureport.R.style.AppTheme_Ukraine;
import static in.ureport.R.style.AppTheme_UnitedKingdom;
import static in.ureport.R.style.AppTheme_Zimbabwe;

/**
 * Created by johncordeiro on 7/23/15.
 */
public class CountryProgramManager {

    public static final int INVALID_VALUE = -1;

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

    public static boolean isPollEnabledForCurrentCountry() {
        return getCurrentCountryProgram().getChannel() != INVALID_VALUE;
    }

    public static boolean isPollEnabledForCountry(CountryProgram countryProgram) {
        return countryProgram.getChannel() != INVALID_VALUE;
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
        if (countryPrograms == null) {
            countryPrograms = new ArrayList<>();
            countryPrograms.add(buildGlobal());
            countryPrograms.add(buildArgentina());
            countryPrograms.add(buildBrazil());
            countryPrograms.add(buildBurundi());
            countryPrograms.add(buildCameroun());
            countryPrograms.add(buildChile());
            countryPrograms.add(buildCoteDIvoire());
            countryPrograms.add(buildElSalvador());
            countryPrograms.add(buildFrance());
            countryPrograms.add(buildGtmCountry());
            /*countryPrograms.add(buildIndiaCountry());*/
            countryPrograms.add(buildIndonesia());
            countryPrograms.add(buildIrland());
            countryPrograms.add(buildJamaica());
            countryPrograms.add(buildLiberia());
            countryPrograms.add(buildMali());
            countryPrograms.add(buildMexico());
            countryPrograms.add(buildMoldova());
            countryPrograms.add(buildNewZealand());
            countryPrograms.add(buildNigeria());
            countryPrograms.add(buildPakistan());
            countryPrograms.add(buildPapuaNewGuinea());
            countryPrograms.add(buildRepubliqueCentrafricaine());
            countryPrograms.add(buildSenegal());
            countryPrograms.add(buildSierraLeone());
            countryPrograms.add(buildSyria());
            countryPrograms.add(buildSwaiziland());
            countryPrograms.add(buildThailand());
            countryPrograms.add(buildUganda());
            countryPrograms.add(buildGbrCountry());
            countryPrograms.add(buildUkraine());
            countryPrograms.add(buildZimbabwe());
        }
        return countryPrograms;
    }

    @NonNull
    private static CountryProgram buildZimbabwe() {
        return new CountryProgram(
                "ZWE",
                AppTheme_Zimbabwe,
                INVALID_VALUE,
                "Zimbabwe",
                2,
                rapidpro_host_address1,
                ureport_host_address1,
                "Ureportzim",
                "U-Report-Zimbabwe-1477396805878097",
                null
        );
    }

    @NonNull
    private static CountryProgram buildUkraine() {
        return new CountryProgram(
                "UKR",
                AppTheme_Ukraine,
                INVALID_VALUE,
                "Ukraine",
                19,
                rapidpro_host_address1,
                ureport_host_address1,
                "ureportukraine",
                "ureportukraine",
                "UReporters"
        );
    }

    @NonNull
    private static CountryProgram buildUganda() {
        return new CountryProgram(
                "UGA",
                AppTheme_Uganda,
                INVALID_VALUE,
                "Uganda",
                18,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportUganda",
                "UReportUganda",
                "U-Reporters"
        );
    }

    @NonNull
    private static CountryProgram buildSwaiziland() {
        return new CountryProgram(
                "SWZ",
                AppTheme_Swaiziland,
                INVALID_VALUE,
                "Swaziland",
                4,
                rapidpro_host_address1,
                ureport_host_address1,
                "Ureportszd",
                "Swaziland-U-Report-1407332376221373",
                null
        );
    }

    @NonNull
    private static CountryProgram buildSyria() {
        return new CountryProgram(
                "SYR",
                AppTheme_Syria,
                syria_channel,
                "Syria",
                6,
                rapidpro_host_address2,
                ureport_host_address2,
                "UReportSyria",
                "UReportSyria",
                "U-Reporters"
        );
    }

    @NonNull
    private static CountryProgram buildSierraLeone() {
        return new CountryProgram(
                "SLE",
                AppTheme_SierraLeone,
                INVALID_VALUE,
                "Sierra Leone",
                7,
                rapidpro_host_address1,
                ureport_host_address1,
                "UreportSL",
                "U-report-Sierra-Leone-361005830734231",
                null
        );
    }

    @NonNull
    private static CountryProgram buildSenegal() {
        return new CountryProgram(
                "SEN",
                AppTheme_Senegal,
                INVALID_VALUE,
                "Sénégal",
                14,
                rapidpro_host_address1,
                ureport_host_address1,
                "ureportsenegal",
                "UreportSenegal",
                null
        );
    }

    @NonNull
    private static CountryProgram buildRepubliqueCentrafricaine() {
        return new CountryProgram(
                "CAF",
                AppTheme_RepubliqueCentrafricaine,
                INVALID_VALUE,
                "République Centrafricaine",
                8,
                rapidpro_host_address1,
                ureport_host_address1,
                "Ureport_rca",
                "ureport.rca",
                null
        );
    }

    @NonNull
    private static CountryProgram buildPakistan() {
        return new CountryProgram(
                "PAK",
                AppTheme_Pakistan,
                INVALID_VALUE,
                "Pakistan",
                16,
                rapidpro_host_address1,
                ureport_host_address1,
                "PakAvaz",
                "ureportpakavaz",
                null
        );
    }

    @NonNull
    private static CountryProgram buildNigeria() {
        return new CountryProgram(
                "NGA",
                AppTheme_Nigeria,
                INVALID_VALUE,
                "Nigeria",
                1,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportNigeria",
                "U-report-Nigeria-1429673597287501",
                "UReporters"
        );
    }

    @NonNull
    private static CountryProgram buildMexico() {
        return new CountryProgram(
                "MEX",
                AppTheme_Mexico,
                INVALID_VALUE,
                "México",
                9,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportMexico",
                "UReportMexico",
                "UReporters"
        );
    }

    @NonNull
    private static CountryProgram buildMali() {
        return new CountryProgram(
                "MLI",
                AppTheme_Mali,
                INVALID_VALUE,
                "Mali",
                3,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportMali",
                "UreportMali",
                null
        );
    }

    @NonNull
    private static CountryProgram buildLiberia() {
        return new CountryProgram(
                "LBR",
                AppTheme_Liberia,
                INVALID_VALUE,
                "Liberia",
                6,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportLiberia",
                "ureport.liberia",
                null
        );
    }

    @NonNull
    private static CountryProgram buildIrland() {
        return new CountryProgram(
                "IRL",
                AppTheme_Ireland,
                ireland_channel,
                "Ireland",
                2,
                rapidpro_host_address2,
                ureport_host_address2,
                "UReportIRL",
                "ureportIRL",
                "U-Reporters"
        );
    }

    @NonNull
    private static CountryProgram buildIndonesia() {
        return new CountryProgram(
                "IDN",
                AppTheme_Indonesia,
                INVALID_VALUE,
                "Indonesia",
                15,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReport_id",
                "UNICEFIndonesia",
                "UReporters_Indonesia"
        );
    }

    @NonNull
    private static CountryProgram buildChile() {
        return new CountryProgram(
                "CHL",
                AppTheme_Chile,
                INVALID_VALUE,
                "Chile",
                12,
                rapidpro_host_address1,
                ureport_host_address1,
                "ureportchile",
                "ureportchile",
                "UReporters"
        );
    }

    @NonNull
    private static CountryProgram buildCameroun() {
        return new CountryProgram(
                "CMR",
                AppTheme_Cameroun,
                INVALID_VALUE,
                "Cameroun",
                10,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportCameroon",
                "ureportcameroon",
                null
        );
    }

    @NonNull
    private static CountryProgram buildBurundi() {
        return new CountryProgram(
                "BDI",
                AppTheme_Burundi,
                INVALID_VALUE,
                "Burundi",
                5,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportGlobal",
                "U-report-Burundi-213297045697711",
                null
        );
    }

    @NonNull
    private static CountryProgram buildBrazil() {
        return new CountryProgram(
                "BRA",
                AppTheme_Brasil,
                brazil_channel,
                "Brasil",
                1,
                rapidpro_host_address2,
                ureport_host_address2,
                "ureportbrasil",
                "ureport.brasil",
                "UReport Brasil"
        );
    }

    @NonNull
    private static CountryProgram buildArgentina() {
        return new CountryProgram(
                "ARG",
                AppTheme,
                argentina_channel,
                "Argentina",
                8,
                rapidpro_host_address2,
                ureport_host_address2,
                null,
                null,
                null
        );
    }

    @NonNull
    private static CountryProgram buildFrance() {
        return new CountryProgram(
                "FRA",
                AppTheme,
                france_channel,
                "États-Unis",
                4,
                rapidpro_host_address2,
                ureport_host_address2,
                null,
                null,
                null
        );
    }

    @NonNull
    private static CountryProgram buildJamaica() {
        return new CountryProgram(
                "JAM",
                AppTheme,
                jamaica_channel,
                "Jamaica",
                11,
                rapidpro_host_address2,
                ureport_host_address2,
                null,
                null,
                null
        );
    }

    @NonNull
    private static CountryProgram buildMoldova() {
        return new CountryProgram(
                "MDA",
                AppTheme,
                moldova_channel,
                "Moldova",
                12,
                rapidpro_host_address2,
                ureport_host_address2,
                null,
                null,
                null
        );
    }

    @NonNull
    private static CountryProgram buildNewZealand() {
        return new CountryProgram(
                "NZL",
                AppTheme,
                new_zealand_channel,
                "New Zealand",
                9,
                rapidpro_host_address2,
                ureport_host_address2,
                null,
                null,
                null
        );
    }

    @NonNull
    private static CountryProgram buildGlobal() {
        return new CountryProgram(
                "GLOBAL",
                AppTheme,
                INVALID_VALUE,
                "U-Report Global",
                13,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReportGlobal",
                "ureportglobal",
                "U-Reporters"
        );
    }

    @NonNull
    private static CountryProgram buildElSalvador() {
        CountryProgram elSalvadorCountry = new CountryProgram(
                "SLV",
                AppTheme_ElSalvador,
                elsalvador_channel,
                "El Salvador",
                10,
                rapidpro_host_address2,
                ureport_host_address2,
                "ureportsv",
                "UReportElSalvador",
                "U-Reporters"
        );
        elSalvadorCountry.setMaleGroup("U-Reporters Male");
        elSalvadorCountry.setFemaleGroup("U-Reporters Female");

        List<AgeGroup> ageGroups = new ArrayList<>();
        ageGroups.add(new AgeGroup("13-18 adolescents", 13, 18));
        ageGroups.add(new AgeGroup("18 youth", 19));
        elSalvadorCountry.setAgeGroups(ageGroups);
        elSalvadorCountry.setStateField("departament");
        return elSalvadorCountry;
    }

    @NonNull
    private static CountryProgram buildGtmCountry() {
        CountryProgram gtmCountry = new CountryProgram(
                "GTM",
                AppTheme_Guatemala,
                guatemala_channel,
                "Guatemala",
                7,
                rapidpro_host_address2,
                ureport_host_address2,
                "UReportGua",
                "ureportglobal",
                "U-Reporters"
        );
        gtmCountry.setMaleGroup("UReport Males");
        gtmCountry.setFemaleGroup("UReport Female");
        gtmCountry.setStateField("department");
        gtmCountry.setDistrictField("district");
        return gtmCountry;
    }

    @NonNull
    private static CountryProgram buildCoteDIvoire() {
        CountryProgram cviCountry = new CountryProgram(
                "CIV",
                AppTheme_CoteDIvoire,
                cotedivoire_channel,
                "Côte d'Ivoire",
                26,
                rapidpro_host_address1,
                ureport_host_address1,
                "UReport_CIV",
                "U-Report-Côte-dIvoire-1218965818134275",
                "U-Reporters Cote d'Ivoire"
        );
        cviCountry.setMaleGroup("U-Reporters Homme");
        cviCountry.setFemaleGroup("U-Reporters Femmes");

        List<AgeGroup> ageGroups = new ArrayList<>();
        ageGroups.add(new AgeGroup("Adolescents", 14, 19));
        ageGroups.add(new AgeGroup("Jeunes", 20, 24));
        ageGroups.add(new AgeGroup("Adults Jeunes", 25, 35));
        ageGroups.add(new AgeGroup("Adulte", 36));
        cviCountry.setAgeGroups(ageGroups);
        cviCountry.setStateField("state");

        return cviCountry;
    }

    @NonNull
    private static CountryProgram buildPapuaNewGuinea() {
        CountryProgram pngCountry = new CountryProgram(
                "PNG",
                AppTheme_PapuaNewGuinea,
                INVALID_VALUE,
                "Papua New Guinea",
                28,
                rapidpro_host_address1,
                ureport_host_address1,
                null,
                "UReportPNG",
                "U-Reporters"
        );
        pngCountry.setMaleGroup("U-Reporters Male");
        pngCountry.setFemaleGroup("U-Reporters Female");
        pngCountry.setStateField("region");

        return pngCountry;
    }

    @NonNull
    private static CountryProgram buildThailand() {
        CountryProgram thaCountry = new CountryProgram(
                "THA",
                AppTheme_Thailand,
                thailand_channel,
                "Thailand",
                5,
                rapidpro_host_address2,
                ureport_host_address2,
                "UReportThai",
                "ureportglobal",
                "U-Reporters"
        );
        thaCountry.setMaleGroup("U-Reporters Male");
        thaCountry.setFemaleGroup("U-Reporters Female");

        List<AgeGroup> ageGroups = new ArrayList<>();
        ageGroups.add(new AgeGroup("6 - 9", 6, 9));
        ageGroups.add(new AgeGroup("10 - 13", 10, 13));
        ageGroups.add(new AgeGroup("14 - 18", 14, 18));
        ageGroups.add(new AgeGroup("19 - 23", 19, 23));
        ageGroups.add(new AgeGroup("24 - 28", 24, 28));
        ageGroups.add(new AgeGroup("29 - 33", 29, 33));
        ageGroups.add(new AgeGroup("> 34", 34));
        thaCountry.setAgeGroups(ageGroups);
        thaCountry.setStateField("province");

        return thaCountry;
    }

    @NonNull
    private static CountryProgram buildGbrCountry() {
        CountryProgram gbrCountry = new CountryProgram(
                "GBR",
                AppTheme_UnitedKingdom,
                unitedkingdom_channel,
                "United Kingdom",
                3,
                rapidpro_host_address2,
                ureport_host_address2,
                "UReportUK",
                "ureportglobal",
                "U-Reporters"
        );
        gbrCountry.setMaleGroup("U-Reporters Male");
        gbrCountry.setFemaleGroup("U-Reporters Female");

        List<AgeGroup> ageGroups = new ArrayList<>();
        ageGroups.add(new AgeGroup("U-Reporters 13-18", 13, 18));
        ageGroups.add(new AgeGroup("U-Reporters 18-25", 18, 25));
        ageGroups.add(new AgeGroup("U-Reporters 26+", 26));
        gbrCountry.setAgeGroups(ageGroups);

        return gbrCountry;
    }

    /*@NonNull
    private static CountryProgram buildIndiaCountry() {
        CountryProgram indiaCountry = buildCountryProgram("IND", AppTheme_India, india_channel, "India", 25
                , rapidpro_host_address1, ureport_host_address1, "UReportIndia", "UReport.India", "UReporters");
        indiaCountry.setMaleGroup("UReporters Male");
        indiaCountry.setFemaleGroup("UReporters Female");

        List<AgeGroup> ageGroups = new ArrayList<>();
        ageGroups.add(new AgeGroup("UReporter Too Young", 0, 13));
        ageGroups.add(new AgeGroup("UReport Youth", 13, 25));
        ageGroups.add(new AgeGroup("Ureport Adults", 25));
        indiaCountry.setAgeGroups(ageGroups);

        return indiaCountry;
    }*/

}
