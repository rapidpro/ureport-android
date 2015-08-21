package in.ureport.managers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import in.ureport.R;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 12/08/15.
 */
public class ImageLoader {

    public static void loadPersonPictureToImageView(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.face);

        Picasso.with(imageView.getContext()).load(url)
                .placeholder(placeholder)
                .into(imageView);
    }

    public static void loadMediaToImageView(ImageView imageView, Media media) {
        if(media != null) {
            Context context = imageView.getContext();
            Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.face);

            Picasso.with(imageView.getContext()).load(media.getUrl())
                    .placeholder(placeholder)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.face);
        }
    }

}
