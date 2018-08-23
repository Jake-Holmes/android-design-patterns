package com.mad.exercise4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.AdapterListUpdateCallback;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // Constants
    private static final String JOKE_URL = "https://www.ryanheise.com/sarcastic.cgi";
    private static final String EXCEPTION_TAG = "Exception";
    private static final String DL_TAG = "DownloadProgress";

    // Globals variables
    private static boolean firstOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the context for all on-click listeners to the main activity class
        Button oneJokeBtn = (Button) findViewById(R.id.one_joke_btn);
        oneJokeBtn.setOnClickListener(this);
        Button threeJokesBtn = (Button) findViewById(R.id.three_jokes_btn);
        threeJokesBtn.setOnClickListener(this);

        // Initialise the mobile phone input type spinner
        Spinner spinner = (Spinner) findViewById(R.id.joke_length_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.joke_lengths_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Finally set the listener for the toast message upon changing joke length
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View v) {
        // On-click listener for all buttons
        // Get the id for the button that was just clicked and call its function
        switch (v.getId()) {
            case R.id.one_joke_btn:
                new Download1JokeAsyncTask().execute();
                break;
            case R.id.three_jokes_btn:
                new DownloadNJokesAsyncTask(3).execute();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the selected joke type ans Display a toast message of the selection any time it
        // changes unless the activity is starting for the first time.
        if (!firstOpen) {
            Spinner spinner = (Spinner)findViewById(R.id.joke_length_spinner);
            String jokeType =spinner.getSelectedItem().toString();
            String toastText = String.format(getString(R.string.new_joke_selected), jokeType);
            Toast.makeText(parent.getContext(), toastText, Toast.LENGTH_SHORT).show();
        } else {
            firstOpen = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing to do here - Included to satisfy java implements declaration
    }


    /**
     * Download a single joke and return it as a string. Enforces the chosen length request type.
     * @return A string of the joke downloaded, or null if an error occurred.
     */
    private String getJokeFromServer() {
        // Init joke text. Set as null to fall back on if download fails
        String jokeText = null;

        // Set the joke URL based on the current spinner state
        Spinner spinner = (Spinner)findViewById(R.id.joke_length_spinner);
        String httpParam = "?len=" + spinner.getSelectedItem().toString();
        String jokeURL = JOKE_URL + httpParam;

        try {
            // Init the connection to the joke server
            URL url = new URL(jokeURL);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            jokeText = in.readLine();
            in.close();
        }
        // Catch any potential exceptions and log it. We do not need any additional logic as the
        // joke will default to null in an error scenario and is handled by the ASync task logic.
        catch (MalformedURLException e) {
            Log.e(EXCEPTION_TAG, "A MalformedURLException has occurred.");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(EXCEPTION_TAG, "An IOException has occurred.");
            e.printStackTrace();
        }

        return jokeText;
    }

    /**
     * Display or remove the indeterminate progress bar and joke text.
     * @param show True if displaying the progress bar, false if removing it.
     */
    private void showIndeterminatePB(final boolean show) {
        // Bring up the overlay
        showOverlay(show);

        // And then load the correct spinner
        final View progressView = findViewById(R.id.single_joke_downloading);
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    /**
     * Display or remove the determinate progress bar and joke text.
     * @param show True if displaying the progress bar, false if removing it.
     */
    private void showDeterminatePB(final boolean show) {
        // Bring up the overlay
        showOverlay(show);

        // And then load the correct spinner
        final View progressView = findViewById(R.id.multiple_jokes_downloading);
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    /**
     * Display or remove the loading overlay.
     * @param show True if displaying the overlay, false if removing it.
     */
    private void showOverlay(final boolean show) {
        // Init the text and loading screen views for processing
        final TextView jokeTv = (TextView) findViewById(R.id.joke_tv);
        final View loadingView = findViewById(R.id.loading_view);

        // Make the joke text invisible or visible and make it fade in upon becoming visible
        jokeTv.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        jokeTv.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                jokeTv.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        // Make the download visible or invisible over the screen.
        loadingView.bringToFront();
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Set the progress text views for download progress.
     * @param numJokes The total number of jokes being downloaded
     * @param numDl The number of jokes downlaoded so far
     */
    private void setDownloadProgress(final int numJokes, final int numDl) {
        int percent = (int)(((numDl+1) * 100.0f) / numJokes);
        Log.i(DL_TAG, String.format("%d%%", percent));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.determinate_download_pb);
        progressBar.setProgress(percent);

        // Init the text views to be updated
        TextView progressTv = (TextView) findViewById(R.id.joke_progress_text);
        TextView percentageTv = (TextView) findViewById(R.id.percentage_tv);
        TextView outOfTv = (TextView) findViewById(R.id.out_of_tv);

        // Build the updated strings
        String progressString = String.format(getString(R.string.downloading_joke_d), numDl+1);
        String percentageString = String.format(getString(R.string.percentage), percent);
        String outOfString = String.format(getString(R.string.num_out_of_num), numDl+1, numJokes);

        // Set the updated strings
        progressTv.setText(progressString);
        percentageTv.setText(percentageString);
        outOfTv.setText(outOfString);
    }

    /**
     * Provides an ASync extension that upon execution will download a single joke from the joke
     * server and update the user view with that joke.
     */
    private class Download1JokeAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showIndeterminatePB(true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            String jokeText = getJokeFromServer();

            if (jokeText==null) {
                jokeText = getString(R.string.single_dl_failed);
            }

            return jokeText;
        }

        @Override
        protected void onPostExecute(String jokeText) {
            // First remove the indeterminate progress spinner
            showIndeterminatePB(false);

            // Then display the joke text from the server or a failed message if an error occurred
            TextView jokeTv = (TextView) findViewById(R.id.joke_tv);
            jokeTv.setText(jokeText);
        }
    }

    /**
     * Provides an ASync extension that upon execution will download a user defined (n) number of
     * jokes from the joke server and update the user view with those jokes, or gracefully fail
     * if an error occurs.
     */
    private class DownloadNJokesAsyncTask extends AsyncTask<Void, Integer, String[]> {

        private int numJokes;

        private DownloadNJokesAsyncTask(int numJokes) {
            this.numJokes = numJokes;
        }

        @Override
        protected void onPreExecute() {
            // Set initial download progress
            setDownloadProgress(numJokes, 0);
            showDeterminatePB(true);
        }

        @Override
        protected String[] doInBackground(Void... voids) {

            String[] jokeArray = {"","",""};

            // Download the specified number of jokes. Return early with null if any fail, otherwise
            // update the progress bar with the current percentage as an int.
            for (int i = 0; i < this.numJokes; i++) {
                jokeArray[i] = getJokeFromServer();
                if (jokeArray[i] == null) return null;
                publishProgress(i);
            }

            return jokeArray;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.i(DL_TAG, String.format("%d jokes downloaded.", progress[0]+1));
            setDownloadProgress(numJokes, progress[0]);
        }

        @Override
        protected void onPostExecute(String[] jokeArray) {
            StringBuilder jokeText = new StringBuilder();

            // First remove the determinate progress spinner
            showDeterminatePB(false);

            // Then display the joke text from the server by iterating over the returned results
            // or a failed message if an error occurred.
            if (jokeArray == null) {
                jokeText = new StringBuilder(getString(R.string.multiple_dl_failed));
            } else {
                for (int i = 0; i< this.numJokes; i++) {
                    jokeText.append(jokeArray[i]).append("\n\n");
                }
            }

            // Finally set the new text
            TextView jokeTv = (TextView) findViewById(R.id.joke_tv);
            jokeTv.setText(jokeText.toString());
        }
    }

}
