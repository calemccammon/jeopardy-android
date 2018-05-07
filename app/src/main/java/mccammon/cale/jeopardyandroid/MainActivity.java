package mccammon.cale.jeopardyandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

//Main activity, which just provides the application's landing page.
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView myTextButton = (TextView) findViewById(R.id.fullscreen_content);
        myTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ClueActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }
}
