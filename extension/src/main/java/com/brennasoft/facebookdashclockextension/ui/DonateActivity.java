package com.brennasoft.facebookdashclockextension.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.brennasoft.facebookdashclockextension.R;
import com.brennasoft.facebookdashclockextension.billing.IabHelper;
import com.brennasoft.facebookdashclockextension.billing.IabResult;
import com.brennasoft.facebookdashclockextension.billing.Purchase;
import com.brennasoft.facebookdashclockextension.util.HelpUtils;

public class DonateActivity extends ListActivity {

	private static final String TAG = "DashClockFacebookExtension";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donate);
		TextView tv = (TextView) findViewById(R.id.donate_header);
		tv.setText(Html.fromHtml(getString(R.string.donate_header)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		initBilling();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.removeItem(R.id.action_donate);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			finish();
            return true;
		case R.id.action_about:
			HelpUtils.showAboutDialog(this);
			return true;
		case R.id.action_changelog:
			HelpUtils.showChangelog(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// The helper object
    private IabHelper mHelper;
    private static final String[] mSkus = {"donate_1", "donate_3", "donate_5"};
    // (arbitrary) request code for the purchase flow
    private static final int RC_REQUEST = 10001;
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(position >= 0 && position < 3) {
			Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
	        setWaitScreen(true);
	        
	        /* TODO: for security, generate your payload here for verification. See the comments on 
	         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use 
	         *        an empty string, but on a production app you should carefully generate this. */
	        String payload = ""; 

	        mHelper.launchPurchaseFlow(this, mSkus[position], RC_REQUEST, 
	                mPurchaseFinishedListener, payload);
		}
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
	
	// Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }
    
    private void initBilling() {
		/* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = getString(R.string.public_key);
        
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.example")) {
            throw new RuntimeException("Please change the sample's package name! See README.");
        }
        
        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
            }
        });
	}
    
 // Callback for when a purchase is finished
 private final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            setWaitScreen(false);
            
            Log.d(TAG, purchase.getSku() + " purchased successfully.");
        }
    };
    
    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
}
