package systems.omnic.shareboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomRecyclerviewAdapter extends RecyclerView.Adapter<CustomRecyclerviewAdapter.ViewHolder> {

    private final String TAG = CustomRecyclerviewAdapter.class.getSimpleName();
    private List<Note> content;
    private Context context;
    private View.OnClickListener onClickListener;

    public CustomRecyclerviewAdapter(List<Note> content, Context context, View.OnClickListener onClickListener){
        this.content = content;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CustomRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Method entered");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View contactView = inflater.inflate(R.layout.recycler_view_listitem, parent, false);
        contactView.setOnClickListener(onClickListener);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRecyclerviewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Method entered");
        TextView text = holder.viewContent;
        text.setText(content.get(position).getContent());
        ImageView iconView = holder.iconView;
        iconView.setVisibility(content.get(position).isSynced() ? View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView viewContent;
        public ImageView iconView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewContent = itemView.findViewById(R.id.viewContent);
            iconView = itemView.findViewById(R.id.imgViewIcon);
        }
    }
}
