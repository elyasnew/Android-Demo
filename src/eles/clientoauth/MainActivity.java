package eles.clientoauth;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button btn_auth;
	private Button btn_req;
	public static String userToken;
	public static String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		btn_auth = (Button) findViewById(R.id.btn_auth);
		btn_req = (Button) findViewById(R.id.btn_request);
		
		btn_auth.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					beginAuthorization();
				} catch (Exception e) {
				}
			}
		});
		
		btn_req.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					makeRequest();
				} catch (Exception e) {
				}
			}
		});
	}
	
	private void beginAuthorization(){
		Intent intent = new Intent(this, AuthorizationActivity.class);
		startActivityForResult(intent, 0);
		
	}
	
	private void makeRequest(){
		Intent intent = new Intent(this, ResourceActivity.class);
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED && requestCode == 0) {
        	TextView txtview = (TextView)findViewById(R.id.txt1);
        	txtview.setText("Access Denied!!!");
		}
		
		else if (resultCode == Activity.RESULT_OK && requestCode == 0){
        	TextView txtview = (TextView)findViewById(R.id.txt1);
        	
        	userToken = data.getStringExtra("access_token");
        	
        	String responseData = data.getStringExtra("response");
        	JSONObject jsonObj = null;
        	try {
				jsonObj = new JSONObject(responseData);
				txtview.setText("You logged in as " + jsonObj.getString("username"));
				String newline = System.getProperty("line.separator");
				txtview.append(newline);
				txtview.append("Now you can make a request");
				
				ViewGroup.MarginLayoutParams llp = (ViewGroup.MarginLayoutParams)txtview.getLayoutParams();
			    llp.setMargins(0, 0, 0, 120); // llp.setMargins(left, top, right, bottom);
			    txtview.setLayoutParams(llp);
			    
				userId = jsonObj.getString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	Button btn_auth = (Button)findViewById(R.id.btn_auth);
        	btn_auth.setVisibility(View.INVISIBLE);
        	
        	Button btn_req = (Button)findViewById(R.id.btn_request);
        	btn_req.setVisibility(View.VISIBLE);
		}
		
		else if (resultCode == Activity.RESULT_OK && requestCode == 1){
        	TextView txtview = (TextView)findViewById(R.id.txt1);
        	txtview.setText("Action Completed successfully! ");
        	String newline = System.getProperty("line.separator");
			txtview.append(newline); 
			txtview.append(data.getStringExtra("result"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
