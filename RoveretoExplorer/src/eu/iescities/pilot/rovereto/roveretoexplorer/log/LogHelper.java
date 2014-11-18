package eu.iescities.pilot.rovereto.roveretoexplorer.log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire.QuizHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire.RemoteConnector;

public class LogHelper {


	private static SharedPreferences sp;
	private static LogHelper instance;

	private static long MAX_SESSION_ID = 2147483648L;

	protected LogHelper(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);

	}

	public static void init(final Context mContext) {
		if (instance == null)
			instance = new LogHelper(mContext);

	}

	public static void sendViaggiaRequest(Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appcustom);
		params[1] = "viaggiaRovereto";
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendSearch(String what, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appdataqueryinitiate);
		params[1] = what;
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendRating(String idEvent, int rate, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appcollaborate);
		params[1] = idEvent + "+rate+" + rate;
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendAttending(String idEvent, boolean attend, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appcollaborate);
		params[1] = idEvent + "+attend+" + attend;
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendComment(String idEvent, String comment, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appcollaborate);
		params[1] = idEvent + "+comment+" + comment;
		params[2] = ctx;

		ast.execute(params);
	}


	public static void sendEventModified(String idEvent, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appprosume);
		params[1] = idEvent;
		params[2] = ctx;
		ast.execute(params);
	}

	public static void sendListViewed(String category, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appconsume);
		params[1] = category + "+list";
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendEventViewed(String idEvent, Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appconsume);
		params[1] = idEvent + "+event";
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendQuestionnarieFinished(Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appquestionnaire);
		params[1] = "";
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendStopLog(Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appstop);
		params[1] = "";
		params[2] = ctx;

		ast.execute(params);
	}

	public static void sendStartLog(Context ctx) {
		SendLogTask ast = new SendLogTask();
		Object[] params = new Object[3];
		params[0] = ctx.getString(R.string.log_appstart);
		params[1] = "";
		params[2] = ctx;
		ast.execute(params);
	}

	private static class SendLogTask extends AsyncTask<Object, Void, Boolean> {
		private long timestamp;
		private String appid;
		private long session;
		private String type = null;
		private String message = null;
		private Context ctx;

		@Override
		protected Boolean doInBackground(Object... params) {
			// /event/{timestamp}/{appid}/{session}/{type}/{message}

			long timestampMS = System.currentTimeMillis();
			timestamp = TimeUnit.MILLISECONDS.toSeconds(timestampMS);
			appid = LogConstants.APP_ID;
			// get sessionId from SharedPreferences
			ctx = (Context) params[2];
			session = getSessionId(ctx);
			// getType
			type = (String) params[0];

			// getMessage
			message = (String) params[1];
			if ("".equals(message)) {
				message = "NoParams";
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

//	public static void createSessionId(Context ctx) {
//		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
//		Random r = new Random();
//		SharedPreferences.Editor editor = sp.edit();
//		editor.putLong(LogConstants.SESSION_ID, (long) (r.nextDouble() * (MAX_SESSION_ID)));
//		editor.commit();
//
//	}

	public static boolean isPresentSessionId(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		return (sp.contains(LogConstants.SESSION_ID));

	}

	public static long getSessionId(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		Random r = new Random();
		long newSessionId =(-(long) (r.nextDouble() * (MAX_SESSION_ID)));
		long sessionid = sp.getLong(LogConstants.SESSION_ID, newSessionId);
		if (!isPresentSessionId(ctx))
			{		
			SharedPreferences.Editor editor = sp.edit();
			editor.putLong(LogConstants.SESSION_ID, newSessionId);
			editor.commit();
			}
		return sessionid;

	}

	public static void deleteSessionId(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(LogConstants.SESSION_ID);
		editor.commit();
	}

	public static boolean isStartedQuestionnaire(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		return (sp.contains(QuizHelper.QUIZ_IS_RUNNING));
	}

	public static void startedQuestionnaire(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(QuizHelper.QUIZ_IS_RUNNING, true);
		editor.commit();
	}

	public static void removeStartedQuestionnaire(Context ctx) {
		sp = ctx.getSharedPreferences(LogConstants.LOG_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(QuizHelper.QUIZ_IS_RUNNING);
		editor.commit();
	}
}
