package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.log.LogConstants;
import eu.iescities.pilot.rovereto.roveretoexplorer.log.LogHelper;

public class QuizHelper {

	public static final String MY_PREFERENCES = "Questionnaire";
	public static final String NEXT_QUESTION = "Question stored";
	public static final String TIME_TO_QUIZ = "time to quiz";
	public static final String QUIZ_IS_RUNNING = "quiz is running";
	private static final int QUIZ_SKIP_DAYS = 7;
	private static final String QUIZ_FINISHED = "quiz finished";

	private static String[][] answers;
	private static int[] default_answers;
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

	public int[] getDefaultAnswers() {
		return default_answers;
	}

	public int getDefaultAnswer(int i) {
		if (i <= default_answers.length - 1)
			return default_answers[i];
		else return 0;
	}

	public ArrayList<String> getAnswers(int i) {
		return new ArrayList<String>(Arrays.asList(answers[i]));
	}

	public void initializedata() {
		// qpa stores pairs of question and its possible answers
		Resources res = ctx.getResources();
		questions = res.getStringArray(R.array.questions);
		default_answers = res.getIntArray(R.array.default_answers);
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

	private static class SendQuestionTask extends
			AsyncTask<Object, Void, Boolean> {
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

			if ("".equals(answerTosend)) {
				answerTosend = "multiplechoice";
			}
			String url = LogConstants.SERVICE_QUESTIONS_RESPONSE + "/"
					+ LogConstants.APP_ID + "/" + (questionNumber + 1) + "/"
					+ (answerNumber + 1) + "/" + answerTosend;
			try {
				RemoteConnector.postJSON(LogConstants.HOST, url, "", null);
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
			// if (true) {
			if (result == true) {
				// store on SharedPreferences number of question
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong(NEXT_QUESTION, questionNumber + 1);
				editor.commit();
				// next question
				quizActivityInterface.nextQuestion();

			} else {
				// error
				Toast.makeText(ctx, R.string.questionnaire_error, Toast.LENGTH_LONG).show();
			}
			if (pd.isShowing())
				pd.dismiss();
		}
	}

	public SharedPreferences getQuizPreferences() {
		return sp;
	}
	
	public static boolean checkQuizForDrawer(FragmentActivity activity){
		// check if is done
		sp = activity.getSharedPreferences(QuizHelper.MY_PREFERENCES,
				Context.MODE_PRIVATE);
			if (!sp.contains(QUIZ_FINISHED)) {
				return true;
			}
			return false;
	}
	public static boolean checkQuiz(FragmentActivity activity){
		DateFormat readFormat = new SimpleDateFormat("MM dd HH:mm:ss zzz yyyy",
				Locale.getDefault());
		Date oldDate, newDate;
		sp = activity.getSharedPreferences(QuizHelper.MY_PREFERENCES,
				Context.MODE_PRIVATE);
		if (sp.contains(TIME_TO_QUIZ)) {
			// check if time is finished and starty quiz activity
			try {
				oldDate = readFormat.parse(sp.getString(TIME_TO_QUIZ,
						readFormat.format(new Date())));
			} catch (ParseException e) {
				e.printStackTrace();
				oldDate = new Date();
			}
			newDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(oldDate); // Now use today date.
			c.add(Calendar.DATE, QUIZ_SKIP_DAYS); // Adding 5 days

			if (newDate.after(c.getTime())) {
				return true;
			}
		} else {
			// check if is done
			if (!sp.contains(QUIZ_FINISHED)) {
				// put the date
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(TIME_TO_QUIZ, readFormat.format(new Date()));
				editor.commit();
			}
		}
		return false;
	}
	public static void launchQuiz(FragmentActivity activity) {
		DateFormat readFormat = new SimpleDateFormat("MM dd HH:mm:ss zzz yyyy",
				Locale.getDefault());
		Date oldDate, newDate;
		sp = activity.getSharedPreferences(QuizHelper.MY_PREFERENCES,
				Context.MODE_PRIVATE);
		if (sp.contains(TIME_TO_QUIZ)) {
			// check if time is finished and starty quiz activity
			try {
				oldDate = readFormat.parse(sp.getString(TIME_TO_QUIZ,
						readFormat.format(new Date())));
			} catch (ParseException e) {
				e.printStackTrace();
				oldDate = new Date();
			}
			newDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(oldDate); // Now use today date.
			c.add(Calendar.DATE, QUIZ_SKIP_DAYS); // Adding 5 days

			if (newDate.after(c.getTime())) {
				// we are after 5 days so do the quiz
				// activity.startActivity(new Intent(activity,
				// QuizFragment.class));
				FragmentTransaction ft = activity.getSupportFragmentManager()
						.beginTransaction();
				ft.setCustomAnimations(R.anim.enter, R.anim.exit);
				Fragment fragment = QuizFragment.newInstance();
				// fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.replace(R.id.content_frame, fragment, fragment.getTag());
				ft.addToBackStack(fragment.getTag());
				ft.commit();
				LogHelper.startedQuestionnaire(activity);
			}
		} else {
			// check if is done
			if (!sp.contains(QUIZ_FINISHED)) {
				// put the date
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(TIME_TO_QUIZ, readFormat.format(new Date()));
				editor.commit();
			}
		}
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static void finished() {
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(QUIZ_FINISHED, true);
		if (sp.contains(TIME_TO_QUIZ))
		{
			editor.remove(TIME_TO_QUIZ);
		}
		editor.commit();

	}
}