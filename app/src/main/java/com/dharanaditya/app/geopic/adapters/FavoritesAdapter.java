package com.dharanaditya.app.geopic.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dharanaditya.app.geopic.FavoritesActivity;
import com.dharanaditya.app.geopic.R;
import com.dharanaditya.app.geopic.interfaces.FavoriteImageDeleteListener;
import com.dharanaditya.app.geopic.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private final Context mContext;
    private FavoriteImageDeleteListener favoriteImageDeleteListener;
    private boolean multiItemSelect = false;
    private ArrayList<String> selectedUris = new ArrayList<>();
    private Cursor mCursor;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            multiItemSelect = true;
            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (selectedUris.size() > 0) {
                favoriteImageDeleteListener.deletePhotos(selectedUris);
            }
            actionMode.finish();

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiItemSelect = false;
            selectedUris.clear();
            notifyDataSetChanged();
        }
    };


    public FavoritesAdapter(@NonNull Context context, FavoriteImageDeleteListener listener) {
        mContext = context;
        favoriteImageDeleteListener = listener;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_image, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder favoritesViewHolder, int position) {
        if (mCursor.moveToPosition(position)) {
            // Fetch favourite image data
            final String url_m = mCursor.getString(FavoritesActivity.INDEX_FAVORITE_URL_M);
            final String internal_url = mCursor.getString(FavoritesActivity.INDEX_FAVORITE_INTERNAL_URL);
            int width = mCursor.getInt(FavoritesActivity.INDEX_FAVORITE_MEDIA_WIDTH);
            int height = mCursor.getInt(FavoritesActivity.INDEX_FAVORITE_MEDIA_HEIGHT);
            favoritesViewHolder.imageViewPhoto.getLayoutParams().width = width;
            favoritesViewHolder.imageViewPhoto.getLayoutParams().height = height;
            favoritesViewHolder.imageViewFavorite.setVisibility(View.INVISIBLE);
            Uri uri = Uri.parse(url_m);
            String imageName = uri.getLastPathSegment();
            // Get Image from cache
            File internal_url_file = ImageUtils.getImageFileFromCache(mContext, imageName, mContext.getString(R.string.image_directory));
            Picasso.get().load(internal_url_file).into(favoritesViewHolder.imageViewPhoto);
            favoritesViewHolder.update(url_m);

            favoritesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteImageDeleteListener.onPhotoClick(internal_url, url_m);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        if (mCursor != null)
            notifyDataSetChanged();
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imv_item_photo)
        ImageView imageViewPhoto;

        @BindView(R.id.btn_item_favorite)
        ImageView imageViewFavorite;

        @BindView(R.id.select_view)
        View selectView;

        public FavoritesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void update(final String value) {
            if (selectedUris.contains(value)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    selectView.setBackgroundColor(Color.argb(0.5f, 211.0f / 255, 211.0f / 255, 211.0f / 255));
                }
            } else {
                selectView.setBackgroundColor(Color.TRANSPARENT);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectItem(value);
                }
            });
        }

        void selectItem(String item) {
            if (multiItemSelect) {
                selectView.setVisibility(View.VISIBLE);
                if (selectedUris.contains(item)) {
                    selectedUris.remove(item);
                    selectView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    item = "\'" + item + "\'";
                    selectedUris.add(item);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        selectView.setBackgroundColor(Color.argb(0.5f, 211.0f / 255, 211.0f / 255, 211.0f / 255));
                    }
                }
            } else {
                selectView.setVisibility(View.INVISIBLE);
            }
        }

    }

}
