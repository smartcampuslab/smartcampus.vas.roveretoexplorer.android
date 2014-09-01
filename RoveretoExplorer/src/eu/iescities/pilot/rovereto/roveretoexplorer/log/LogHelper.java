package eu.iescities.pilot.rovereto.roveretoexplorer.log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire.RemoteConnector;

public class LogHelper {

	// Modify an existing event (prosumer)
	//
	// • Add new information category (super-prosumer?)
	//
	// • Follow/participate an event (consumer).
	//
	// • Rate/comment an event (prosumer)
	private static SharedPreferences sp;
	private static Context ctx;
	private static LogHelper instance;


	private static long MAX_SESSION_ID = 2147483648L;

	protected LogHelper(Context ctx) {
		this.ctx = ctx;
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);

	}

	public static void init(final Context mContext) {
		if (instance == null)
			instance = new LogHelper(mContext);

	}

	public static void sendViaggiaRequest() {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appcustom);
		params[1] = "viaggiaRovereto";
		ast.execute(params);
	}

	public static void sendSearch(String what) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appdataqueryinitiate);
		params[1] = what;
		ast.execute(params);
	}

	public static void sendRating(String idEvent, int rate) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appcollaborate);
		params[1] = idEvent+"+rate+"+rate;
		ast.execute(params);
	}

	public static void sendAttending(String idEvent, boolean attend) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appcollaborate);
		params[1] = idEvent+"+attend+"+attend;
		ast.execute(params);
	}

	public static void sendComment(String idEvent, String comment) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appcollaborate);
		params[1] = idEvent+"+comment+"+comment;
		ast.execute(params);
	}

//	public static void sendDBSynch() {
//		SendLogTask ast = new SendLogTask();
//		String[] params = new String[2];
//		params[0] = ctx.getString(R.string.log_appodconsume);
//		params[1] = "";
//		ast.execute(params);
//	}

	public static void sendEventModified(String idEvent) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appprosume);
		params[1] = idEvent;
		ast.execute(params);
	}

	public static void sendListViewed(String category) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appconsume);
		params[1] = category+"+list";
		ast.execute(params);
	}

	public static void sendEventViewed(String idEvent) {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appconsume);
		params[1] = idEvent+"+event";
		ast.execute(params);
	}

	public static void sendQuestionnarieFinished() {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appquestionnaire);
		params[1] = "";
		ast.execute(params);
	}

	public static void sendStopLog() {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appstop);
		params[1] = "";
		ast.execute(params);
	}

	public static void sendStartLog() {
		SendLogTask ast = new SendLogTask();
		String[] params = new String[2];
		params[0] = ctx.getString(R.string.log_appstart);
		params[1] = "";
		ast.execute(params);
	}

	private static class SendLogTask extends AsyncTask<Object, Void, Boolean> {
		private long timestamp;
		private String appid;
		private long session;
		private String type = null;
		private String message = null;

		@Override
		protected Boolean doInBackground(Object... params) {
			// /event/{timestamp}/{appid}/{session}/{type}/{message}

			long timestampMS = System.currentTimeMillis();
			timestamp =TimeUnit.MILLISECONDS.toSeconds(timestampMS);
			appid = LogConstants.APP_ID;
			// get sessionId from SharedPreferences
			session = getSessionId(ctx);
			// getType
			type = (String) params[0];

			// getMessage
			message = (String) params[1];
			if ("".equals(message))
			{
				message="NoParams";
			}
			String url = LogConstants.SERVICE_LOG_RESPONSE + "/" + timestamp + "/" + LogConstants.APP_ID + "/"
					+ session + "/" + type + "/" + message;
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
		Random r = new Random();
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(LogConstants.SESSION_ID, (long)(r.nextDouble()*(MAX_SESSION_ID)));
		editor.commit();

	}

	public static boolean isPresentSessionId(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		return (sp.contains(LogConstants.SESSION_ID));

	}

	public static long getSessionId(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		Random r = new Random();
		long sessionid = sp.getLong(LogConstants.SESSION_ID, (long)(r.nextDouble()*(MAX_SESSION_ID)));
		return sessionid;

	}

	public static void deleteSessionId() {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(LogConstants.SESSION_ID);
		editor.commit();
	}
}
