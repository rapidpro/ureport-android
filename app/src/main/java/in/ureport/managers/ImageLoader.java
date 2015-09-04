package in.ureport.managers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
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

    public static void loadPictureToImageView(ImageView imageView, Media media) {
        if(media != null) {
            Picasso.with(imageView.getContext()).load(media.getUrl())
                    .into(imageView);
        }
    }

    public static void loadGenericPictureToImageView(ImageView imageView, Media media) {
        loadMedia(imageView, media, R.drawable.shape_loading_picture);
    }

    public static void loadGroupPictureToImageView(ImageView imageView, Media media) {
        loadMedia(imageView, media, R.drawable.default_group);
    }

    private static void loadMedia(ImageView imageView, Media media, @DrawableRes int placeholderDrawableId) {
        if(media != null && media.getUrl() != null) {
            Context context = imageView.getContext();
            Drawable placeholder = ContextCompat.getDrawable(context, placeholderDrawableId);

            Picasso.with(imageView.getContext()).load(media.getUrl())
                    .placeholder(placeholder)
                    .into(imageView);
        } else {
            imageView.setImageResource(placeholderDrawableId);
        }
    }

}
