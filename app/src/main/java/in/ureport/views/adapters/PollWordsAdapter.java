package in.ureport.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.List;

import in.ureport.R;
import in.ureport.models.ItemKeyword;

/**
 * Created by john-mac on 5/10/16.
 */
public class PollWordsAdapter extends TagsAdapter {

    private final List<ItemKeyword> keywords;

    public PollWordsAdapter(List<ItemKeyword> keywords) {
        this.keywords = keywords.subList(0, 15);
    }

    @Override
    public int getCount() {
        return keywords.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word_result, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.word = (TextView) view.findViewById(R.id.word);
        holder.word.setText(getItem(position).getKeyword());
        view.setTag(holder);

        return view;
    }

    @Override
    public ItemKeyword getItem(int position) {
        return keywords.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return (1/(position+1)) - position;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.word.setTextColor(generateColorForTheme(themeColor, holder.word.getCurrentTextColor()));

        GradientDrawable colorDrawable = (GradientDrawable) view.getBackground();
        colorDrawable.setColor(themeColor);
    }

    private int generateColorForTheme(int themeColor, int currentColor) {
        int alphaFromTheme = Color.alpha(themeColor);
        alphaFromTheme = alphaFromTheme >= 190 ? 0xff : alphaFromTheme;

        return Color.argb(alphaFromTheme, Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor));
    }

    private class ViewHolder {
        TextView word;
    }
}
