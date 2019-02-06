package in.ureport.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.loader.CountryListLoader;
import in.ureport.loader.LocationInfoLoader;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.LocationInfo;

public class EditProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_COUNTRY_INFO = "countryInfo";

    private static final int LOAD_COUNTRY_LIST_ID = 3000;
    private static final int LOAD_STATES_ID = 3001;

    private User user;
    private CountryInfo countryInfo;

    private ImageView photo;
    private TextInputEditText name;
    private Spinner country;
    private Spinner state;
    private TextInputEditText email;
    private Spinner gender;

    private TextView changePhoto;
    private TextView privateInformation;

    public static EditProfileFragment newInstance(User user) {
        final EditProfileFragment fragment = new EditProfileFragment();
        final Bundle args = new Bundle();

        args.putParcelable(EXTRA_USER, user);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getUserFromArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        loadCountryList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_profile, menu);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle bundle) {
        if (id == LOAD_COUNTRY_LIST_ID) {
            return new CountryListLoader(requireContext());
        } else if (id == LOAD_STATES_ID && bundle != null) {
            CountryInfo countryInfo = bundle.getParcelable(EXTRA_COUNTRY_INFO);
            return new LocationInfoLoader(getActivity(), countryInfo);
        }
        return null;
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case LOAD_COUNTRY_LIST_ID:
                updateCountries((List<CountryInfo>) data);
                break;
            case LOAD_STATES_ID:
                LocationInfo locationInfo = (LocationInfo) data;
                updateStateSpinner(locationInfo.getStates());

//                districts = locationInfo.getDistricts();
//                if (state.getSelectedItem() instanceof Location)
//                    updateDistrictSpinnerForState((Location) state.getSelectedItem());
        }
    }

    private void getUserFromArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(EXTRA_USER)) {
            user = args.getParcelable(EXTRA_USER);
        }
    }

    private void loadCountryList() {
        LoaderManager.getInstance(this).initLoader(LOAD_COUNTRY_LIST_ID, null, this);
    }

    private void setupView(View view) {
        final AppCompatActivity activity = ((AppCompatActivity) requireActivity());
        final ActionBar actionBar = activity.getSupportActionBar();
        final Toolbar toolbar = view.findViewById(R.id.toolbar);

        activity.setSupportActionBar(toolbar);
        if (actionBar != null) {
            final Drawable indicator = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(indicator);
        }

        photo = view.findViewById(R.id.photo);
        name = view.findViewById(R.id.name);
        country = view.findViewById(R.id.country);
        state = view.findViewById(R.id.state);
        email = view.findViewById(R.id.email);
        gender = view.findViewById(R.id.gender);

        ImageLoader.loadPersonPictureToImageView(photo, user.getPicture());
        name.setText(user.getNickname());
        name.clearFocus();
        email.setText(user.getEmail());
        email.setEnabled(false);

        resetLocationSpinner(country, R.array.spinner_loading);
        resetLocationSpinner(state, R.array.spinner_loading);
    }

    private void resetLocationSpinner(Spinner spinner, @ArrayRes int messsageArray) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.view_spinner_dropdown, getResources().getStringArray(messsageArray));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
    }

    private void updateCountries(List<CountryInfo> data) {
        final CountryInfo countryInfo = getUserCountry(data);
        this.countryInfo = countryInfo;
        if (countryInfo == null) {
            return;
        }
        final ArrayAdapter<CountryInfo> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.view_spinner_dropdown, new CountryInfo[]{countryInfo});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);

        loadStatesForCountry(countryInfo);
    }

    @Nullable
    private CountryInfo getUserCountry(List<CountryInfo> data) {
        for (CountryInfo countryInfo : data) {
            try {
                if (hasUserISOCode(countryInfo.getIsoAlpha3())) {
                    return countryInfo;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean hasUserISOCode(String countryCode) {
        return countryCode != null && countryCode.equals(user.getCountry());
    }

    private void loadStatesForCountry(CountryInfo countryInfo) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_COUNTRY_INFO, countryInfo);

        final LoaderManager loaderManager = LoaderManager.getInstance(this);
        final Loader loader = loaderManager.getLoader(LOAD_STATES_ID);

        if (loader != null && ((LocationInfoLoader) loader).getCountryInfo().equals(countryInfo)) {
            loaderManager.initLoader(LOAD_STATES_ID, bundle, this);
        } else {
            loaderManager.restartLoader(LOAD_STATES_ID, bundle, this);
        }
    }

    private void updateStateSpinner(List<Location> locations) {
        updateSpinnerLocation(state, locations);
        selectUserState(locations, getUserState(locations));
    }

    private Location getUserState(List<Location> locations) {
        for (Location location : locations) {
            if (hasUserState(location)) {
                return location;
            }
        }
        return null;
    }

    private boolean hasUserState(Location location) {
        return location.getName().equals(user.getState()) || location.getToponymName().equals(user.getState());
    }

    private void selectUserState(List<Location> locations, Location userLocation) {
        int userStatePosition = locations.indexOf(userLocation);
        if (userStatePosition >= 0) {
            state.setSelection(userStatePosition);
        }
    }

    private void updateDistrictSpinnerForState(Location state) {
//        List<Location> districtsFromState = getDistrictsFromState(state);
//        containsDistrict = !districtsFromState.isEmpty();
//
//        if(containsDistrict) {
//            district.setVisibility(View.VISIBLE);
//            updateSpinnerLocation(district, districtsFromState);
//        } else {
//            district.setVisibility(View.GONE);
//        }
    }

    private void updateSpinnerLocation(Spinner spinner, List<Location> locations) {
        if (locations != null) {
            ArrayAdapter<Location> adapter = new ArrayAdapter<>(requireContext(), R.layout.view_spinner_dropdown, locations);
            Collator collator = getLocalizedComparator();
            adapter.sort((lhs, rhs) -> collator.compare(lhs.toString(), rhs.toString()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setEnabled(true);
            spinner.setAdapter(adapter);
        } else {
            resetLocationSpinner(spinner, R.array.spinner_error_loading_states);
            Toast.makeText(getActivity(), R.string.error_no_internet, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Collator getLocalizedComparator() {
        final Locale locale;
        if (countryInfo != null) {
            locale = new Locale(countryInfo.getCountryCode());
        } else {
            locale = Locale.getDefault();
        }
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.PRIMARY);
        return collator;
    }

}
