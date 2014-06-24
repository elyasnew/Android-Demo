package eles.clientoauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.contextdata.ContextData;

public class ResourceActivity extends Activity implements ContextData.Listener {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		ContextData cd = new ContextData(
				"http://api-dev.learning-context.de/", 0, MainActivity.userToken);

			cd.registerGETListener(this);
			cd.registerPOSTListener(this);
			
			cd.get("entities/values", "{\"type\":\"APPSTART\", \"key\":\"ELES App\"}");
			//cd.get("user/test", "");
	}
	
	@Override
	public void onGETResult(String result) {
		Log.d("GET works: ", result);
		Intent returnData = new Intent();
		returnData.putExtra("result", result);
		setResult(Activity.RESULT_OK, returnData);
		
		finish();
	}

	@Override
	public void onPOSTResult(String result) {
		Log.d("POST works: ", result);
	}
	
}
