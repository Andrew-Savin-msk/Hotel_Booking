package com.example.hotelbooking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelbooking.Item;
import com.example.hotelbooking.R;

import java.io.File;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private OnRoomClickListener listener;
    private ArrayList<HotelRoom> rooms;
    private Context context;


    public MyAdapter(Context context, ArrayList<HotelRoom> rooms, OnRoomClickListener listner) {
        this.context = context;
        this.rooms = rooms;
        this.listener = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotelRoom currentRoom = getRoom(position);
        holder.textViewCity.setText(currentRoom.getCity());
        holder.imageView.setImageBitmap(currentRoom.getImgBitmap());
        holder.textViewGuests.setText(currentRoom.getPlaces() + "");
        holder.textDescription.setText(currentRoom.getDescription());
        holder.buttonBook.setOnClickListener(v -> {
            if (listener != null) {
                if (listener.onRoomClick(currentRoom)) {
                    rooms.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, rooms.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


    public HotelRoom getRoom(int position) {
        return rooms.get(position % rooms.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCity;
        public ImageView imageView;
        public TextView textViewGuests;
        public TextView textDescription;
        public AppCompatButton buttonBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCity = itemView.findViewById(R.id.textViewCity);
            imageView = itemView.findViewById(R.id.imageView);
            textViewGuests = itemView.findViewById(R.id.textView);
            textDescription = itemView.findViewById(R.id.textDescription);
            buttonBook = itemView.findViewById(R.id.buttonBook);
        }
    }
}
