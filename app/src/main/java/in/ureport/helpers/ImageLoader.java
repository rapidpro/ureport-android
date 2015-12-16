package in.ureport.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

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
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public static void loadPictureToImageView(ImageView imageView, Media media, Callback callback) {
        if(media != null) {
            Picasso.with(imageView.getContext()).load(media.getUrl())
                    .into(imageView, callback);
        }
    }

    public static void loadGenericPictureToImageView(ImageView imageView, Media media) {
        RequestCreator requestCreator = loadMedia(imageView, media, R.drawable.shape_loading_picture);
        if(requestCreator != null) {
            requestCreator.into(imageView);
        }
    }

    public static void loadGenericPictureToImageViewFit(ImageView imageView, Media media) {
        loadGenericPictureToImageViewFit(imageView, media.getUrl());
    }

    public static void loadGenericPictureToImageViewFit(ImageView imageView, String url) {
        RequestCreator requestCreator = getPicassoRequest(imageView, url, R.drawable.shape_loading_picture);
        if(requestCreator != null) {
            requestCreator.fit().centerCrop().into(imageView);
        }
    }

    public static void loadGroupPictureToImageView(ImageView imageView, Media media) {
        RequestCreator requestCreator = loadMedia(imageView, media, R.drawable.default_group);
        if(requestCreator != null) {
            requestCreator.fit().centerCrop().into(imageView);
        }
    }

    private static RequestCreator loadMedia(ImageView imageView, Media media, @DrawableRes int placeholderDrawableId) {
        if(media != null && media.getUrl() != null) {
            return getPicassoRequest(imageView, media.getUrl(), placeholderDrawableId);
        } else {
            imageView.setImageResource(placeholderDrawableId);
        }
        return null;
    }

    private static RequestCreator getPicassoRequest(ImageView imageView, String url, @DrawableRes int placeholderDrawableId) {
        Context context = imageView.getContext();
        Drawable placeholder = ContextCompat.getDrawable(context, placeholderDrawableId);

        return Picasso.with(imageView.getContext()).load(url)
                .placeholder(placeholder);
    }

}
