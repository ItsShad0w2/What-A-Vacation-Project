package com.example.what_a_vacation_project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder>
{
    private List<Trip> tripList;
    private onTripClickListener listener;

    public TripAdapter(List<Trip> tripList, onTripClickListener listener)
    {
        this.tripList = tripList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.TripViewHolder holder, int position)
    {
        // Setting the data of a trip inside of the item stored in the recycler view

        Trip trip = tripList.get(position);
        holder.name.setText(trip.getName());
        holder.country.setText(trip.getCountry());
        holder.dates.setText(trip.getStartDate() + " - " + trip.getEndDate());

        holder.itemView.setOnClickListener(view -> {
            try
            {
                listener.onTripClick(trip);
            }
            catch (Exception exception)
            {
                Log.e("Exception", exception.getMessage());
            }
        });

        holder.itemView.setOnLongClickListener(view -> {

            int currentPosition = holder.getBindingAdapterPosition();

            if(currentPosition != RecyclerView.NO_POSITION)
            {
                listener.onTripLongClick(trip, position);
            }

            return true;
        });

        if(isTripFinished(trip))
        {
           holder.finishedTrip.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.finishedTrip.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount()
    {
        return tripList.size();
    }

    public boolean isTripFinished(Trip trip)
    {
        // In case the trip's date has passed, the trip would be marked as finished

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date endDate = simpleDateFormat.parse(trip.getEndDate());

            return endDate.before(new Date());
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder
    {
        // Initializing the fields of an item stored in the recycler view
        TextView name, country, dates, finishedTrip;
        public TripViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textTripName);
            country = itemView.findViewById(R.id.textTripCountry);
            dates = itemView.findViewById(R.id.textTripDates);
            finishedTrip = itemView.findViewById(R.id.finishedTrip);
        }
    }


    public interface onTripClickListener
    {
        // Handle of the different actions that are able to be done on a trip inside of the recycler view
        void onTripClick(Trip trip) throws IllegalAccessException, InstantiationException;
        void onTripLongClick(Trip trip, int position);
    }
}
