package sanggit999.example.admin_comic.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import sanggit999.example.admin_comic.R;
import sanggit999.example.admin_comic.interfaces.OnItemClickListenerComic;
import sanggit999.example.admin_comic.model.Comic;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ComicViewHolder> {

    private Context context;
    private List<Comic> comicList;

    private OnItemClickListenerComic onItemClickListenerComic;

    public ComicAdapter(Context context, List<Comic> comicList, OnItemClickListenerComic onItemClickListenerComic) {
        this.context = context;
        this.comicList = comicList;
        this.onItemClickListenerComic = onItemClickListenerComic;
    }

    public void setComicList(List<Comic> comicList) {
        this.comicList = comicList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComicAdapter.ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comic, parent, false);
        return new ComicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicAdapter.ComicViewHolder holder, int position) {
        Comic comic = comicList.get(position);

        Glide.with(context)
                .load(comic.getCoverImage()).diskCacheStrategy(DiskCacheStrategy.ALL) // URL của ảnh;
                .into(holder.imgComic);
        holder.titleTextView.setText(comic.getTitle());


        // Xử lý sự kiện khi bấm vào một item;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListenerComic != null) {
                    onItemClickListenerComic.onItemClicked(comic);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }


    public class ComicViewHolder extends RecyclerView.ViewHolder {
        ImageView imgComic;
        TextView titleTextView;


        public ComicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleComic);
            imgComic = itemView.findViewById(R.id.imgComic);

        }
    }
}


