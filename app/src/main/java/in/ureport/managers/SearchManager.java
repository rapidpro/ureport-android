package in.ureport.managers;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import in.ureport.R;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class SearchManager {

    private Context context;

    public SearchManager(Context context) {
        this.context = context;
    }

    public void addSearchView(Menu menu, @StringRes int hint, SearchView.OnQueryTextListener onQueryTextListener, SearchView.OnCloseListener onCloseListener) {
        MenuItem searchItem = menu.add(R.string.search_title);
        searchItem.setIcon(R.drawable.ic_search_white_24dp);
        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = new SearchView(context);
        searchView.setQueryHint(context.getString(hint));
        searchView.setOnQueryTextListener(onQueryTextListener);
        searchView.setOnCloseListener(onCloseListener);

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);

        MenuItemCompat.setActionView(searchItem, searchView);
    }

}
