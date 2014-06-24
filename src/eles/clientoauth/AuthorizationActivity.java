package eles.clientoauth;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import de.contextdata.RandomString;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthorizationActivity extends Activity {

	private WebView webview;
	
	private static final String uri = "http://oauth.learning-context.de/";
	private static final String CLIENT_ID = ""; //your App ID
	private static final String CLIENT_SECRET = ""; //your App key
	private static final String RESPONSE_TYPE = "code";	
    private static final String REDIRECT_URI = "context-learning://client-auth";
    private static String state = "";
    
    private String responseURL = "";
    private String responseData = "";
    
    private String refresh_token = "";
    private String token = "";
    private String expiresIn = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authorization_view);
		setUpViews();
	}
	
	private void setUpViews(){
		webview = (WebView)findViewById(R.id.web_view);
		state = RandomString.randomString(55);
		
		StringBuilder str = new StringBuilder();
		str.append(uri);
		str.append("authorize?client_id=");
		str.append(CLIENT_ID);
		str.append("&response_type=");
		str.append(RESPONSE_TYPE);
		str.append("&redirect_uri=");
		str.append(REDIRECT_URI);
		str.append("&state=");
		str.append(state);
		
		webview.setWebViewClient(new WebViewClient(){
			 @Override
		     public boolean shouldOverrideUrlLoading(WebView view, String url){
		       view.loadUrl(url);
		       return true;
		     }
			 @Override
			 public void onPageFinished(WebView view, String url) {
				 
		        if(url.contains("code=")){
		        	String txt = url.split("code=")[1];
		        	String code = txt.split("&")[0];
		        	
		        	if(!code.equals("")){
		        		JSONObject jsonObj = null;
		        		try {
		        			responseURL = new PostDataTask().execute(new String[] {code}).get();
		        			
		        			jsonObj = new JSONObject(responseURL);
		        			
		        			token = jsonObj.getString("access_token");
		        			
		        			expiresIn = jsonObj.getString("expires_in");
		        			
		        			refresh_token = jsonObj.getString("refresh_token");
		        			
							responseData = new GetDataTask().execute(new String[] {token}).get();
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		
		        		Intent returnIntent = new Intent();

		        		returnIntent.putExtra("response", responseData);
		        		returnIntent.putExtra("access_token", token);
		        		returnIntent.putExtra("refresh_token", refresh_token);
		        		returnIntent.putExtra("expiresIn", expiresIn);
		        		setResult(Activity.RESULT_OK, returnIntent);
		        		
			        	finish();
		        	}
		        }
		        
		        else if(url.contains("error")){
		        	setResult(Activity.RESULT_CANCELED);
		        	finish();
		        }
		    }
		});
		
		webview.loadUrl(str.toString());
	}
	
	private class PostDataTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... data) {
			ArrayList<NameValuePair> nvp = getPostData(data[0]);

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(uri + "token");
				httppost.setEntity(new UrlEncodedFormEntity(nvp));
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					BufferedInputStream bis = new BufferedInputStream(instream);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}
					String html = new String(baf.toByteArray());

					return html;
				}

			} catch (ClientProtocolException e) {
				Log.e("error", "There was a protocol based error");
			} catch (IOException e) {
				Log.e("error", "There was an IO Stream related error");
			}
			return "";
		}
	}
	
	private ArrayList<NameValuePair> getPostData(String data) {
		ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();

		nvp.add(new BasicNameValuePair("client_id", CLIENT_ID));
		nvp.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
		nvp.add(new BasicNameValuePair("grant_type", "authorization_code"));
		nvp.add(new BasicNameValuePair("code", data));
		nvp.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
		return nvp;
	}
	
	private class GetDataTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... data) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(uri + "api/me");
				httpget.addHeader("Authorization", "Bearer " + data[0]);
				HttpResponse response = httpclient.execute(httpget);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					BufferedInputStream bis = new BufferedInputStream(instream);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}
					String html = new String(baf.toByteArray());
					Log.d("response", html);

					return html;
				}

			} catch (ClientProtocolException e) {
				Log.e("error", "There was a protocol based error");
			} catch (IOException e) {
				Log.e("error", "There was an IO Stream related error");
			}

			return "";
		}
	}
}
