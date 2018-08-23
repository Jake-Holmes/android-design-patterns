/*
 * Copyright 2018 Jake Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mad.exercise5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Adapter to generate views for train items on demand as the user scrolls through the recycler
 * view.
 */
public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Train> mTrains;

    /**
     * ViewHolder for the train item layout to store the layout and data.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout arrivalTimeFrame;
        public TextView platformTv;
        public TextView arrivalTimeTv;
        public TextView statusTv;
        public TextView destinationTv;
        public TextView destinationTimeTv;

        /**
         * Constructor to set the public views that will later be set by the adapter.
         * @param itemView The current view.
         */
        public ViewHolder(View itemView) {
            super(itemView);

            this.arrivalTimeFrame = itemView.findViewById(R.id.arrival_time_frame);
            this.platformTv = itemView.findViewById(R.id.platform_tv);
            this.arrivalTimeTv = itemView.findViewById(R.id.arrival_time_tv);
            this.statusTv = itemView.findViewById(R.id.status_tv);
            this.destinationTv = itemView.findViewById(R.id.destination_tv);
            this.destinationTimeTv = itemView.findViewById(R.id.destination_time_tv);
        }
    }

    /**
     * Constructor for the train adapater to set instance variables.
     * @param context The context that the adapter exists within.
     * @param trains The list of trains that will populate the views.
     */
    public TrainAdapter(Context context, ArrayList<Train> trains) {
        this.mContext = context;
        this.mTrains = trains;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.train_item, parent, false);

        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Set the fields for the recycled view, including the on click listener for refreshing a
        // single instance of a train arrival time.
        final Train train = mTrains.get(position);

        holder.arrivalTimeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RefreshSingleTrain(view).execute();
                Random rand = new Random();
                train.setArrivalTime(rand.nextInt(MainActivity.ARRIVAL_MAX));
                notifyDataSetChanged();
            }
        });

        holder.platformTv.setText(train.getPlatform());
        holder.arrivalTimeTv.setText(Integer.toString(train.getArrivalTime()));

        String status = train.getStatus();
        holder.statusTv.setText(status);

        if (!status.equals(MainActivity.STATUS_ARRAY[MainActivity.GOOD_STATUS_INDEX])) {
            holder.statusTv.setTextColor(ContextCompat.getColor(mContext, R.color.colourRed));
        } else {
            holder.statusTv.setTextColor(ContextCompat.getColor(mContext, R.color.colourGreen));
        }

        holder.destinationTv.setText(train.getDestination());
        holder.destinationTimeTv.setText(train.getDestinationTime());
    }

    @Override
    public int getItemCount() {
        return mTrains.size();
    }

    /**
     * Provides an ASync extension that upon execution will refresh a single train arrive time
     * layout and display a progress bar while it is refreshed.
     */
    private class RefreshSingleTrain extends AsyncTask<Void, Void, Void> {

        private View mView;

        /**
         * Constructor to set the view that is being acted upon and refreshed.
         * @param view The view that was clicked - this will be the green arrival time layout box.
         */
        public RefreshSingleTrain(View view) {
            this.mView = view;
        }

        /**
         * Remove the arrival time and display the progress bar in its place.
         */
        @Override
        protected void onPreExecute() {
            mView.findViewById(R.id.time_remaining_ll).setVisibility(View.GONE);
            mView.findViewById(R.id.train_refresh_pb).setVisibility(View.VISIBLE);
        }

        /**
         * Wait three seconds while the view below the progress bar is updated. Arbitrary waiting
         * period based on exercise requirements.
         * @param voids No parameters required.
         * @return null
         */
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(MainActivity.SLEEP);
            } catch (InterruptedException e) {
                // We don't actually care if this call is interrupted.
                Log.e("RefreshError", "Single train refresh sleep was interrupted.");
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Remove the progress bar and display arrival time  in its place which by this point will
         * have been refreshed.
         * @param voids No parameters required.
         */
        @Override
        protected void onPostExecute(Void voids) {
            mView.findViewById(R.id.time_remaining_ll).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.train_refresh_pb).setVisibility(View.GONE);
        }
    }
}
