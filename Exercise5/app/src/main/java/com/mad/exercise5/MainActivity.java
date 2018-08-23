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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Activity the user launches to. Displays a series of train timetable info, with some user options
 * allowed to edit the data.
 */
public class MainActivity extends AppCompatActivity {

    // Globals for train data generation - Spoofs an outside data source in a real implementation
    private static final String DESTINATION_TIME = "%2d:%2d";
    private static final String[] PLATFORMS = {"Central Platform 21", "Macquarie Park Platform 1",
            "Wynyard Platform 3"};
    public static final String[] STATUS_ARRAY = {"On Time", "Late"};
    public static final int GOOD_STATUS_INDEX = 0;
    private static final String[] DESTINATIONS = {"Artarmon", "Town Hall", "Museum"};

    // Globals
    final static int INITIAL_TRAIN_COUNT = 5;
    // Index of the status array that is considered good
    public static final int SLEEP = 3000;
    public static final int ARRIVAL_MAX = 20;
    private static final int DEST_HOUR_MAX = 23;
    private static final int DEST_MINUTES_MAX = 59;

    // Instance variables
    private RecyclerView mTrainRecyclerView;
    private TrainAdapter mTrainAdapter;
    private ArrayList<Train> mTrainList = new ArrayList<Train>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.add_fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTrain();
            }
        });

        // Generate train data to populate the initial list
        for (int i = 0; i < INITIAL_TRAIN_COUNT; i++) {
            mTrainList.add(generateRandomTrain());
        }
        // Init the reference to the train items recycler view
        mTrainRecyclerView = (RecyclerView) findViewById(R.id.train_rv);
        // Set the layout for the recycler view to horizontal linear layout
        mTrainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create the train adapter and provide it initial fixed train data
        mTrainAdapter = new TrainAdapter(this, mTrainList);
        // Set the adapter for the recycler view
        mTrainRecyclerView.setAdapter(mTrainAdapter);
    }

    /**
     * Generates a random train and adds it to the list. Then updates the adapter to ensure the user
     * view is also updated.
     */
    private void addTrain() {
        mTrainList.add(generateRandomTrain());
        mTrainAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_refresh:
                actionRefreshAll();
                break;

            case R.id.action_delete_all:
                actionDeleteAll();
                return true;

            case R.id.action_quit:
                actionQuit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void actionRefreshAll() {
        // Bring the overlay up on a three second timer
        new RefreshAllTrains().execute();

        // While the timer is up, refresh the arrival time only of every train by iterating over
        // every train in the list.
        Random rand = new Random();
        for(Train train: mTrainList) {
            train.setArrivalTime(rand.nextInt(ARRIVAL_MAX));
        }

        // Finally let the adapter know that data has changed.
        mTrainAdapter.notifyDataSetChanged();

    }

    private void actionDeleteAll() {
        mTrainList.clear();
        mTrainAdapter.notifyDataSetChanged();
    }

    private void actionQuit() {
        finish();
    }

    /**
     * Generate a train from randomising the available dataset of train information.
     * @return A train with randomised data.
     */
    private Train generateRandomTrain() {

        Random rand = new Random();

        String platform = PLATFORMS[rand.nextInt(PLATFORMS.length)];

        int arrivalTime = rand.nextInt(ARRIVAL_MAX);

        String status = STATUS_ARRAY[rand.nextInt(STATUS_ARRAY.length)];

        String destination = DESTINATIONS[rand.nextInt(DESTINATIONS.length)];

        @SuppressLint("DefaultLocale") String destinationTime = String.format(DESTINATION_TIME,
                rand.nextInt(DEST_HOUR_MAX), rand.nextInt(DEST_MINUTES_MAX));

        return new Train(platform, arrivalTime, status, destination, destinationTime);
    }

    /**
     * Provides an ASync extension that upon execution will refresh the screen and current train
     * list data.
     */
    private class RefreshAllTrains extends AsyncTask<Void, Void, Void> {

        /**
         * Remove the recycler view and display progress bar in its place.
         * @param voids No parameters required.
         */
        @Override
        protected void onPreExecute() {
            mTrainRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.whole_screen_pb).setVisibility(View.VISIBLE);
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
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                // We don't actually care if this call is interrupted.
                Log.e("RefreshError", "Refresh sleep was interrupted.");
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Remove the progress bar and display recycler view in its place which by this point will
         * have been refreshed.
         * @param voids No parameters required.
         */
        @Override
        protected void onPostExecute(Void voids) {
            mTrainRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.whole_screen_pb).setVisibility(View.GONE);
        }
    }

}
