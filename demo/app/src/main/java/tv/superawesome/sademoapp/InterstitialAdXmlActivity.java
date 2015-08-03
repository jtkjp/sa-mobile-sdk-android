package tv.superawesome.sademoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import tv.superawesome.superawesomesdk.fragments.SAInterstitialFragment;

public class InterstitialAdXmlActivity extends AppCompatActivity {

    private SAInterstitialFragment interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad_xml);
        setResult(RESULT_OK);

        this.interstitial = (SAInterstitialFragment)getSupportFragmentManager().findFragmentById(R.id.sa_interstitial);
    }

    public void showInterstitial(View view) {
        this.interstitial.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interstitial_ad_xml, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
