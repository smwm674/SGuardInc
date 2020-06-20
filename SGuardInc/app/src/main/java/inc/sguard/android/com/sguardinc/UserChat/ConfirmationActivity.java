package inc.sguard.android.com.sguardinc.UserChat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import inc.sguard.android.com.sguardinc.R;

public class ConfirmationActivity extends AppCompatActivity {
    TextView textViewId, textViewStatus, textViewAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        //Views
        textViewId = (TextView) findViewById(R.id.paymentId);
        textViewStatus = (TextView) findViewById(R.id.paymentStatus);
        textViewAmount = (TextView) findViewById(R.id.paymentAmount);

        //Getting Intent
        Intent intent = getIntent();


        try {
            JSONObject jsonDetails = new JSONObject(intent.getStringExtra("PaymentDetails"));

            //Displaying payment details
            showDetails(jsonDetails.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetails(JSONObject jsonDetails, String paymentAmount) throws JSONException {


        //Showing the details from json object
        textViewId.setText(jsonDetails.getString("id"));
        textViewStatus.setText(jsonDetails.getString("state"));
        textViewAmount.setText(paymentAmount + " USD");
    }
}