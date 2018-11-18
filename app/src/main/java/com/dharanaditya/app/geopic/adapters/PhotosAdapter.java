package com.dharanaditya.app.geopic.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dharanaditya.app.geopic.R;
import com.dharanaditya.app.geopic.interfaces.ImageItemClickListener;
import com.dharanaditya.app.geopic.model.FlickrPhoto;
import com.dharanaditya.app.geopic.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ImageViewHolder> {

    private Context mContext;
    private List<FlickrPhoto> mFlickrPhotosList;
    private ImageItemClickListener imageItemClickListener;

    public PhotosAdapter(Context context, ImageItemClickListener photoClickListener) {
        mContext = context;
        imageItemClickListener = photoClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int position) {

        FlickrPhoto flickrPhoto = mFlickrPhotosList.get(position);

        imageViewHolder.imageViewPhoto.getLayoutParams().width = flickrPhoto.getWidth_m();
        imageViewHolder.imageViewPhoto.getLayoutParams().height = flickrPhoto.getHeight_m();
        Picasso.get().load(flickrPhoto.getUrl_m()).into(imageViewHolder.imageViewPhoto);

        // Toggle Favourite Image
        PhotoAdapterHelper photoAdapterHelper = new PhotoAdapterHelper(mContext);
        boolean isFavorite = photoAdapterHelper.isFavorite(flickrPhoto.getUrl_m());
        changeFavoriteImage(imageViewHolder, isFavorite);
        photoAdapterHelper.closeDB();
    }

    private void changeFavoriteImage(@NonNull ImageViewHolder imageViewHolder, boolean isFavorite) {
        int favoriteResource;
        if (isFavorite) {
            favoriteResource = R.drawable.ic_like;
        } else {
            favoriteResource = R.drawable.ic_unlike;
        }
        imageViewHolder.imageViewFavorite.setImageResource(favoriteResource);
    }

    @Override
    public int getItemCount() {
        return mFlickrPhotosList.size();
    }

    public void swapData(List<FlickrPhoto> photos) {
        mFlickrPhotosList = photos;
        if (mFlickrPhotosList != null)
            notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imv_item_photo)
        ImageView imageViewPhoto;

        @BindView(R.id.btn_item_favorite)
        ImageView imageViewFavorite;

        @BindView(R.id.card_view)
        CardView cardView;

        public ImageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FlickrPhoto flickrPhoto = mFlickrPhotosList.get(getAdapterPosition());

                    PhotoAdapterHelper photoAdapterHelper = new PhotoAdapterHelper(mContext);
                    boolean isFavorite = photoAdapterHelper.isFavorite(flickrPhoto.getUrl_m());
                    ((ImageView) view).setImageResource(!isFavorite ? R.drawable.ic_like : R.drawable.ic_unlike);

                    String imageFile = "";
                    if (!isFavorite) {
                        // Store image into cache
                        Uri uri = Uri.parse(flickrPhoto.getUrl_m());
                        String imageName = uri.getLastPathSegment();

                        ContextWrapper contextWrapper = new ContextWrapper(mContext);
                        File directory = contextWrapper.getDir(mContext.getString(R.string.image_directory), Context.MODE_PRIVATE);
                        imageFile = new File(directory, imageName).getAbsolutePath();

                        Picasso.get().load(flickrPhoto.getUrl_m()).into(ImageUtils.storePicassoImageTarget(mContext, mContext.getString(R.string.image_directory), imageName));
                    } else {
                        ImageUtils.deleteFileFromCache(mContext, flickrPhoto.getUrl_m(), mContext.getString(R.string.image_directory));
                    }


                    imageItemClickListener.onFavoriteClick(flickrPhoto, imageFile, isFavorite);
                    photoAdapterHelper.closeDB();
                }
            });
        }

        @Override
        public void onClick(View v) {
            imageItemClickListener.onPhotoClick(mFlickrPhotosList.get(getAdapterPosition()));
        }
    }

}
