package com.brennasoft.facebookdashclockextension.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.brennasoft.facebookdashclockextension.FacebookDashService;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ClearActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		new ClearAsyncTask().execute();
		
		try {
			Intent intent = Intent.parseUri(getIntent().getStringExtra(FacebookDashService.LAUNCH_INTENT), Intent.URI_INTENT_SCHEME);
			startActivity(intent);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ClearAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			finish();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Request request = new Request();
			Session session = Session.openActiveSessionFromCache(getApplicationContext());
			if(session.isClosed()) {
				return null;
			}
			request.setSession(session);
			request.setGraphPath("me/notifications");
			Bundle parameters = new Bundle();
			parameters.putString("fields", "id");
			request.setParameters(parameters);
			Response resp = request.executeAndWait();
			List<String> toClear = new ArrayList<>();
			try {
				if(resp.getError() == null && resp.getConnection().getResponseCode() == 200) {
					GraphObject object = resp.getGraphObject();
					JSONArray jsonArray = object.getInnerJSONObject().getJSONArray("data");
					for(int i=0, j=jsonArray.length(); i<j; i++) {
						JSONObject notification = jsonArray.getJSONObject(i);
						String[] splitIds = notification.getString("id").split("_");
						toClear.add(splitIds[splitIds.length-1]);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(toClear.size() > 0) {
				Bundle args = new Bundle();
				StringBuilder sb = new StringBuilder();
				for(int i=0, j = toClear.size(); i<j; i++) {
					sb.append(toClear.get(i));
					if(i < (j-1)) {
						sb.append(",");
					}
				}
				args.putString("notification_ids", sb.toString());
				Request req = new Request();
				req.setSession(session);
				req.setRestMethod("notifications.markRead");
				req.setHttpMethod(HttpMethod.POST);
				req.setParameters(args);
				req.executeAndWait();
			}
			return null;
		}
	}
}
