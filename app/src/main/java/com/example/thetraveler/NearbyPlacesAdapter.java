package com.example.thetraveler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;
    JSONArray data;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public NearbyPlacesAdapter(Context context, JSONArray data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject place = (JSONObject) this.data.get(position);

            if (place.has("name")) {
                String name = place.getString("name");
                holder.textName.setText(name);
            }
            if (place.has("icon")) {
                String iconURL = place.getString("icon");
                Picasso.get().load(iconURL).into(holder.imageIcon);
            }
            if (place.has("rating")) {
                holder.textRating.setVisibility(View.VISIBLE);
                String rating = place.getString("rating");
                holder.textRating.setText("Rating: " + rating);
            } else {
                holder.textRating.setVisibility(View.INVISIBLE);
            }
            if (place.has("opening_hours")) {
                JSONObject opening_hours = place.getJSONObject("opening_hours");
                if (opening_hours.has("open_now")) {
                    Boolean open = opening_hours.getBoolean("open_now");
                    holder.textOpen.setVisibility(View.VISIBLE);
                    if (open) {
                        holder.textOpen.setText("Open");
                        holder.textOpen.setTextColor(Color.GREEN);
                    } else {
                        holder.textOpen.setText("Closed");
                        holder.textOpen.setTextColor(Color.RED);
                    }
                } else {
                    holder.textOpen.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.textOpen.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.data.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textRating, textOpen;
        ImageView imageIcon;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textName = itemView.findViewById(R.id.tvName);
            textRating = itemView.findViewById(R.id.tvRating);
            textOpen = itemView.findViewById(R.id.tvOpen);
            imageIcon = itemView.findViewById(R.id.ivIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
