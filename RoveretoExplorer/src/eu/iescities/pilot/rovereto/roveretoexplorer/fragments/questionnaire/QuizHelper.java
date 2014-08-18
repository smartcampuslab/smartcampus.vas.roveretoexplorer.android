package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;

public class QuizHelper {

	public static final String MY_PREFERENCES = "Questionnaire";
	public static final String QUESTIONS_STORED = "Question stored";
	public static final String TIME_TO_QUIZ = "time to quiz";
	public static final String HOST = "http://150.241.239.65:8080";
	public static final String SERVICE_RESPONSE = "/IESCities/api/log/rating/response";
	public static final String APP_ID = "RoveretoExplorer";
	private static final int QUIZ_SKIP_DAYS = 5;

	private static String[][] answers;
	private String[] questions;
	private static Context ctx;
	private static SharedPreferences sp;

	public QuizHelper(Context ctx) {
		this.ctx = ctx;
		sp = ctx.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

		initializedata();
	}

	public String getQuestion(int i) {
		return questions[i];
	}

	public String[] getQuestions() {
		return questions;
	}

	public ArrayList<String> getAnswers(int i) {
		return new ArrayList<String>(Arrays.asList(answers[i]));
	}

	public void initializedata() {
		// qpa stores pairs of question and its possible answers
		Resources res = ctx.getResources();
		questions = res.getStringArray(R.array.questions);
		TypedArray taAnswers = res.obtainTypedArray(R.array.answers);
		int n = taAnswers.length();
		answers = new String[n][];
		for (int i = 0; i < n; ++i) {
			int id = taAnswers.getResourceId(i, 0);
			if (id > 0) {
				answers[i] = res.getStringArray(id);
			} else {
				// something wrong with the XML
			}
		}
		taAnswers.recycle(); // Important!

	}

	public static void sendData(Object... obj) {
		SendQuestionTask ast = new SendQuestionTask();
		ast.execute(obj);
	}

	private static class SendQuestionTask extends AsyncTask<Object, Void, Boolean> {
		ProgressDialog pd;
		int questionNumber;
		int answerNumber;
		String answerTosend;
		QuizInterface quizActivityInterface;

		@Override
		protected Boolean doInBackground(Object... params) {
			questionNumber = (Integer) params[0];
			answerNumber = (Integer) params[1];
			answerTosend = (String) params[2];
			quizActivityInterface = (QuizInterface) params[3];

			if ("".equals(answerTosend))
			{
				answerTosend="multiplechoice";
			}
			String url = SERVICE_RESPONSE + "/"+ APP_ID + "/"+ (questionNumber+1) + "/"+ (answerNumber+1)+"/"+ answerTosend;
			try {
				RemoteConnector.postJSON(HOST, url, "", null);
			} catch (Exception e) {
				e.printStackTrace();
				// to be fixed with real data
				return false;
			}
			return true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(ctx);
			pd.setTitle("Sending Data");
			pd.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// to be fixed with real data
//			if (true) {
				 if (result == true) {
				// store on SharedPreferences number of question
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong(QUESTIONS_STORED, questionNumber);
				editor.commit();
				// next question
				quizActivityInterface.nextQuestion();

			} else {
				// error
			}
			if (pd.isShowing())
				pd.dismiss();
		}
	}

	public SharedPreferences getQuizPreferences() {
		return sp;
	}

	public static void checkQuiz(Activity activity) {
		DateFormat readFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date oldDate, newDate;
		sp = activity.getSharedPreferences(QuizHelper.MY_PREFERENCES, Context.MODE_PRIVATE);
		if (sp.contains(TIME_TO_QUIZ)) {
			// check if time is finished and starty quiz activity
			try {
				oldDate = readFormat.parse(sp.getString(TIME_TO_QUIZ, readFormat.format(new Date())));
			} catch (ParseException e) {
				e.printStackTrace();
				oldDate = new Date();
			}
			newDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(oldDate); // Now use today date.
			c.add(Calendar.SECOND, QUIZ_SKIP_DAYS); // Adding 5 days

			if (newDate.after(c.getTime())) {
				// we are after 5 days so do the quiz
				activity.startActivity(new Intent(activity, QuizActivity.class));
			}
		} else {
			// put the date
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(TIME_TO_QUIZ, readFormat.format(new Date()));
			editor.commit();
		}
	}
}