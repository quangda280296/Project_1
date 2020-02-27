package quang.project2.view.playmusic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import quang.project2.R;
import quang.project2.model.Song;
import static quang.project2.view.home.MainActivity.mode;

/**
 * Created by keban on 12/23/2016.
 */
public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Song> items;

    public class BookViewHolder extends SwipeToAction.ViewHolder<Song> {
        public TextView titleView;
        public TextView artistView;

        public BookViewHolder(View v) {
            super(v);

            titleView = (TextView) v.findViewById(R.id.title);
            artistView = (TextView) v.findViewById(R.id.artist);
        }
    }

    /** Constructor **/
    public MusicAdapter(List<Song> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);

        RelativeLayout root = (RelativeLayout)view.findViewById(R.id.home);
        TextView label = (TextView) view.findViewById(R.id.title);
        TextView artist = (TextView) view.findViewById(R.id.artist);
        View divider = view.findViewById(R.id.divider);

        if(mode.compareTo("ni") == 0)
        {
            root.setBackgroundResource(R.drawable.night_selector);
            divider.setBackgroundColor(view.getResources().getColor(R.color.dark_gray_pressed));
            label.setTextColor(view.getResources().getColor(R.color.white));
            artist.setTextColor(view.getResources().getColor(R.color.light_gray));
        }

        return ( new BookViewHolder(view) );

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Song item = items.get(position);
        BookViewHolder vh = (BookViewHolder) holder;
        vh.titleView.setText(item.getTitle());
        vh.artistView.setText(item.getArtist());
        vh.data = item;

    }

}
