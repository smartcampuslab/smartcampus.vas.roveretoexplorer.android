package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.trentorise.smartcampus.network.RemoteConnector;
import eu.trentorise.smartcampus.network.RemoteException;

public class QuizHelper {

	public static final String MY_PREFERENCES = "Questionnaire";
	public static final String QUESTIONS_STORED = "Question stored";
	public static final String HOST = "http://150.241.239.65:8080/IESCities/api";
	public static final String SERVICE_RESPONSE = "/log/rating/response";
	public static final String APP_ID = "5";

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


			String url = SERVICE_RESPONSE + "/"+ APP_ID + "/"+ questionNumber + "/"+ answerNumber+"/"+ answerTosend;
			try {
				RemoteConnector.postJSON(HOST, url, "", null);
			} catch (Exception e) {
				e.printStackTrace();
				// to be fixed with real data
				return true;
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

}