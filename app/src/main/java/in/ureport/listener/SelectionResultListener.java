package in.ureport.listener;

import java.util.List;

/**
 * Created by johncordeiro on 7/15/15.
 */
public interface SelectionResultListener<Model> {

    void onSelectionResult(List<Model> list);

}
