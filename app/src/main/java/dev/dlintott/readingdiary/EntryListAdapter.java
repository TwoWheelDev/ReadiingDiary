package dev.dlintott.readingdiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.ViewHolder> {
    private final List<ReadingEntry> data;
    private final Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView bookTitle;
        private final TextView pagesRead;
        private final TextView date;
        private final ImageView bookImage;

        public ViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.entryCardView);
            bookTitle = view.findViewById(R.id.entryBookTitle);
            pagesRead = view.findViewById(R.id.entryBookPages);
            date = view.findViewById(R.id.entryBookDate);
            bookImage = view.findViewById(R.id.entryBookImage);
        }

        public CardView getCardView() {
            return  cardView;
        }

        public TextView getBookTitle() {
            return bookTitle;
        }

        public TextView getPagesRead() {
            return pagesRead;
        }

        public TextView getDate() {
            return date;
        }

        public ImageView getBookImage() {
            return bookImage;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet ReadingEntry[] containing the data to populate views to be used
     * by RecyclerView
     */
    public EntryListAdapter(Context context, List<ReadingEntry> dataSet) {
        this.data = dataSet;
        this.context = context;
    }

    public void addEntry(int position, ReadingEntry entry) {
        this.data.add(position, entry);
    }

    public void deleteEntry(int position) {
        this.data.remove(position);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reading_entry_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        Log.d("EntryListAdapter", "Payload onBindViewHolder position: " + position);
        if(!payloads.isEmpty() && payloads.get(0) instanceof ReadingEntry) {
            ReadingEntry entry = (ReadingEntry) payloads.get(0);
            setItem(viewHolder, entry);
        } else {
            super.onBindViewHolder(viewHolder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d("EntryListAdapter", "Regular onBindViewHolder position: " + position);
        ReadingEntry entry = this.data.get(position);
        setItem(viewHolder, entry);
    }

    private void setItem(ViewHolder viewHolder, ReadingEntry entry) {
        DateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH);
        Context curContext = viewHolder.itemView.getContext();
        viewHolder.getCardView().setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewEntry.class);
            intent.putExtra("entryId", entry.getId());
            intent.putExtra("position", viewHolder.getAdapterPosition());

            ((Activity) context).startActivityForResult(intent, 0);
        });

        viewHolder.getBookTitle().setText(entry.getTitle());
        viewHolder.getPagesRead().setText(String.format(
                Locale.ENGLISH,
                "Pages read: %d - %d",
                entry.getPageFrom(), entry.getPageTo()));
        viewHolder.getDate().setText(sdf.format(entry.getDate()));

        Glide.with(curContext)
                .load(entry.getBookImage())
                .error(R.drawable.image_error)
                .into(viewHolder.getBookImage());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }


}


