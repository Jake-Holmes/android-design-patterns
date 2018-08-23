package com.mad.exercise2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Provides an activity for the secondary screen displaying user input data.
 */
public class SecondaryActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        // Create a submit button object and set its action
        Button submitBtn = (Button) findViewById(R.id.secondary_submit_btn);
        submitBtn.setOnClickListener(this);

        // Retrieve the intent that started this activity
        Intent intent = getIntent();

        // Set text views based on provided intent data
        setTextFromExtra(intent, Constants.EXTRA_NAME, R.id.name_data);
        setTextFromExtra(intent, Constants.EXTRA_PHONE, R.id.phone_data);
        setTextFromExtra(intent, Constants.EXTRA_PHONE_TYPE, R.id.phone_type_data);
        setTextFromExtra(intent, Constants.EXTRA_EMAIL, R.id.email_data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.secondary_submit_btn:
                submitAction();
                break;

            default:
                break;
        }
    }

    /**
     * Set a text view string from a given intent string extra.
     * @param extra The string extra id to retrieve a string from.
     * @param id The reosurce id of the text view to be set.
     */
    private void setTextFromExtra(Intent intent, String extra, int id) {
        String text = intent.getStringExtra(extra);
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }

    /**
     * Determine if the checkbox is ticked, and set the activity results accordingly.
     * Then close the activity and return to the previous activity.
     */
    private void submitAction() {
        // Set result strong based on checkbox status
        CheckBox checkBox = (CheckBox) findViewById(R.id.agree_checkbox);
        boolean isAgree = checkBox.isChecked();
        String resultString =
                isAgree ? getString(R.string.agree_text) : getString(R.string.disagree_text);

        // Init and set the intent to store the response data
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_AGREE, resultString);
        setResult(Activity.RESULT_OK, intent);

        // Gracefully finish the activity lifecycle
        finish();
    }

}
