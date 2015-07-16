package in.ureport.listener;

/**
 * Created by johncordeiro on 7/15/15.
 */
public interface ItemSelectionListener<Model> {

    void onItemSelected(Model item);
    void onItemDeselected(Model item);

}
