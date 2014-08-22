package eu.iescities.pilot.rovereto.roveretoexplorer.log;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire.RemoteConnector;

public class LogHelper {

//	Modify an existing event (prosumer)
//
//	• Add new information category (super-prosumer?)
//
//	• Follow/participate an event (consumer).
//
//	• Rate/comment an event (prosumer)
	private static SharedPreferences sp;
	private static Context ctx;
	private static LogHelper instance;

	protected LogHelper(Context ctx) {
		this.ctx = ctx;
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);

	}
	public static void init(final Context mContext) {
		if (instance == null)
			instance = new LogHelper(mContext);

	
	}
	
	
	public static void sendStartLog() {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0]=ctx.getString(R.string.log_appstart);
		params[1]="";
		ast.execute(params);
	}
	
	private static class SendLogTask extends AsyncTask<Object, Void, Boolean> {
		private long timestamp;
		private String appid;
		private int session;
		private String type = null;
		private String message = null;

		
		
		@Override
		protected Boolean doInBackground(Object... params) {
//			/event/{timestamp}/{appid}/{session}/{type}/{message}

			timestamp=System.currentTimeMillis();
			appid=LogConstants.APP_ID;
			//get sessionId from SharedPreferences
			session = getSessionId(ctx);
			//getType
			type = (String) params[0];

			//getMessage
			message = (String) params[1];

			String url = LogConstants.SERVICE_LOG_RESPONSE + "/"+ timestamp+"/"+LogConstants.APP_ID + "/"+ session + "/"+ type+"/"+ message;
			try {
				RemoteConnector.postJSON(LogConstants.HOST, url, "", null);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	public static void createSessionId() {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		Random r=new Random();
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(LogConstants.SESSION_ID, r.nextInt());
		editor.commit();
		
	}

	public static int getSessionId(Context ctx ) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		Random r=new Random();
		int sessionid=sp.getInt(LogConstants.SESSION_ID, r.nextInt());
		return sessionid;
		
	}
	public static void deleteSessionId() {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(LogConstants.SESSION_ID);
		editor.commit();		
	}
}
