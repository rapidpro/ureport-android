package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.models.holders.Tutorial;

/**
 * Created by johncordeiro on 05/10/15.
 */
public class TutorialItemFragment extends Fragment {

    private static final String EXTRA_TUTORIAL = "tutorial";

    private Tutorial tutorial;

    public static TutorialItemFragment newInstance(Tutorial tutorial) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_TUTORIAL, tutorial);

        TutorialItemFragment fragment = new TutorialItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_tutorial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(EXTRA_TUTORIAL)) {
            tutorial = getArguments().getParcelable(EXTRA_TUTORIAL);
            setupView(view);
        }
    }

    private void setupView(View view) {
        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(tutorial.getImage());

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(tutorial.getTitle());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(tutorial.getDescription());
    }
}
