package com.mad.exercise2;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Provides an activity for the application to launch into where a user may enter data.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SECONDARY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName(), "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise the mobile phone input type spinner
        Spinner spinner = (Spinner) findViewById(R.id.phone_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phone_types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Create a submit button object and set its action
        Button submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAction(v);
            }
        });

        // Create a clear all button object and set its action
        Button clearBtn = (Button) findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllAction();
            }
        });

        // Create an exit button object and set its action
        Button exitBtn = (Button) findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAction();
            }
        });
    }

    /**
     * Retrieve and submit the user defined data to the secondary activity for processing.
     */
    private void submitAction(View view) {
        // Display the Snackbar acknowledgement.
        // This requirement is superseded by opening a second activity.
        // Code left in for reference.
        Snackbar.make(view, R.string.submit_snackbar_text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        // Init the intent to start the secondary activity
        Intent intent = new Intent(this, SecondaryActivity.class);

        // Populate the intent with extras for data processing in the second activity
        intent.putExtra(Constants.EXTRA_NAME, getEtString(R.id.your_name_et));
        intent.putExtra(Constants.EXTRA_EMAIL, getEtString(R.id.your_email_et));
        intent.putExtra(Constants.EXTRA_PHONE, getEtString(R.id.your_phone_et));

        Spinner spinner = (Spinner)findViewById(R.id.phone_spinner);
        String spinnerText = spinner.getSelectedItem().toString();
        intent.putExtra(Constants.EXTRA_PHONE_TYPE, spinnerText);

        // Begin the activity and use the known request code for retrieving results
        startActivityForResult(intent, REQUEST_SECONDARY);
    }

    /**
     * Get a string from a given edit tect field.
     * @param id The resource id for the chosen edit text view.
     * @return The string of the given edit text
     */
    private String getEtString(int id) {
        EditText editText = (EditText) findViewById(id);
        return editText.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Determine which request we're responding to
        switch (requestCode) {
            case REQUEST_SECONDARY:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    // Get the result strong form secondary activity
                    String text = data.getStringExtra(Constants.EXTRA_AGREE);
                    // Output a Snackbar message corresponding to the user selection
                    Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                            .show();
                }
        }
    }

    /**
     * Reset the activity state to all default values.
     */
    private void clearAllAction() {
        // Clear all editable text fields
        clearEditText(R.id.your_name_et);
        clearEditText(R.id.your_email_et);
        clearEditText(R.id.your_phone_et);

        // Set the phone spinner to the default item
        Spinner spinner = (Spinner) findViewById(R.id.phone_spinner);
        spinner.setSelection(0);
    }

    /**
     * Clears the value of an edit text field using a provided view id.
     */
    private void clearEditText(int id) {
        EditText editText = (EditText) findViewById(id);
        editText.getText().clear();
    }

    /**
     * Exit the application gracefully.
     */
    private void exitAction() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(this.getClass().getName(), "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.getClass().getName(), "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(this.getClass().getName(), "onRestart");
    }

    @Override
    protected void onPause() {
        Log.d(this.getClass().getName(), "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(this.getClass().getName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(this.getClass().getName(), "onDestroy");
        super.onDestroy();
    }
}
