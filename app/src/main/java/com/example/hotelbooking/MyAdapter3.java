package com.example.hotelbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.ViewHolder> {
    private OnReservationClickListner listener;
    private ArrayList<Reservation> reservations;
    private Context context;

    public MyAdapter3(Context context, ArrayList<Reservation> reservations, OnReservationClickListner listener) {
        this.context = context;
        this.reservations = reservations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_broni, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation currentRoom = getRoom(position);
        holder.textViewCity.setText(currentRoom.getCity());
        holder.imageView.setImageBitmap(currentRoom.getImgBitmap());
        holder.textViewGuests.setText("Гости: " + currentRoom.getPlaces()); // Updated to include prefix as per your layout
        holder.textViewIn.setText("C " + currentRoom.getInDayFormatted());
        holder.textViewOut.setText("По " + currentRoom.getOutDayFormatted());
        holder.buttonBook.setOnClickListener(v -> {
            if (listener != null) {
                if (listener.onReservationClick(currentRoom)) {
                    reservations.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, reservations.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public Reservation getRoom(int position) {
        return reservations.get(position % reservations.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCity;
        public TextView textViewOut;
        public TextView textViewIn;
        public ImageView imageView; // Assuming the ImageView to display image from your XML
        public TextView textViewGuests; // The TextView for guests info
        public AppCompatButton buttonBook; // Button for booking or other actions

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCity = itemView.findViewById(R.id.textViewCity);
            textViewOut = itemView.findViewById(R.id.textViewOut);
            textViewIn = itemView.findViewById(R.id.textViewIn);
            imageView = itemView.findViewById(R.id.imageView); // Adjusted ID to match your layout
            textViewGuests = itemView.findViewById(R.id.textViewGuests); // Adjusted ID to match your layout
            buttonBook = itemView.findViewById(R.id.buttonUnBook);
        }
    }
}
