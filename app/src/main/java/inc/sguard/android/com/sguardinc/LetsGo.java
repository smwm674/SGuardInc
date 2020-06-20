package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LetsGo extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_go);

        Button choose_option = (Button) findViewById(R.id.choose_option);
        choose_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LetsGo.this, LoginOrSignup.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
