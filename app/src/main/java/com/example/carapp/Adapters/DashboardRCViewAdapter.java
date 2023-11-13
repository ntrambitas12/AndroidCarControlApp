package com.example.carapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carapp.Model.DashboardLinkModel;
import com.example.carapp.R;

import java.util.List;


public class DashboardRCViewAdapter extends RecyclerView.Adapter<DashboardRCViewAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(NavDirections navAction); // Define any parameters you need
    }
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    private List<DashboardLinkModel> items;
    private Context context;
    public DashboardRCViewAdapter(Context context, List<DashboardLinkModel> items) {
        this.context = context;
        this.items = items;
    }
    public List<DashboardLinkModel> getItems() {
        return items;
    }
    public void setItems(List<DashboardLinkModel> items) {
        this.items = items;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the view that will represent where the link data is rendered
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_links_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current link to render
        DashboardLinkModel link = items.get(position);
        // Set the data in the view
        holder.itemImage.setImageResource(link.getIconRes());
        holder.itemText.setText(link.getLinkText());
        final NavDirections clickedItemNavDest = items.get(position).getNavAction();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(clickedItemNavDest);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemText = itemView.findViewById(R.id.itemText);
        }
    }
}
