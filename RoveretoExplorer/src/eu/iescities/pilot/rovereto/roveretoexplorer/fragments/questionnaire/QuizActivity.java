package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.MainActivity;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.log.LogHelper;

public class QuizActivity extends Activity implements QuizInterface {
	/** Called when the activity is first created. */
	private static final String PREF_QUIZ = "Quiz";
	private static final String PREF_INTRO = "Intro";
	private static final String PREF_QUESTIONS = "Questions";
	private static final String PREF_END = "End";

	private RadioButton radioButton;
	private TextView quizQuestion;
	private int questNo = 0;
	private int answerNo = 0;
	private SharedPreferences prefs = null;
	private LinearLayout introLayout, questionsLayout, endLayout;
	private RadioGroup radioGroup;
	private EditText openQuestion;
	// private String[] corrAns = new String[5];
	private QuizHelper db = null;
	private String questions;
	private ArrayList<String> answers;

	// private int counter = 1;
	// private String label;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new QuizHelper(this);
		prefs = db.getQuizPreferences();
		setContentView(R.layout.quiz);
		setupLayout();

	}

	private void setupLayout() {
		introLayout = (LinearLayout) findViewById(R.id.quizIntro);
		questionsLayout = (LinearLayout) findViewById(R.id.quizQuestions);
		endLayout = (LinearLayout) findViewById(R.id.quizEnd);
		openQuestion = (EditText) findViewById(R.id.editOpenQuestion);
		// check previous question if it was done
		if (prefs != null) {
			if (prefs.contains(QuizHelper.NEXT_QUESTION)) {
				restoreQuestionnaire();
				return;
			}
		}
		if (isIntroTime()) {
			setupIntroLayout();
		} else if (isQuestionsTime()) {
			setupQuestionsLayout();
		} else if (isEndTime()) {
			setupEndLayout();
		}

	}

	private void restoreQuestionnaire() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PREF_QUIZ, PREF_QUESTIONS);
		editor.commit();
		questNo = (int) prefs.getLong(QuizHelper.NEXT_QUESTION, 0);
		welcomeBackLayout();
	}

	private void welcomeBackLayout() {
		introLayout.setVisibility(View.VISIBLE);
		questionsLayout.setVisibility(View.GONE);
		endLayout.setVisibility(View.GONE);
		quizQuestion = (TextView) findViewById(R.id.introText);
		quizQuestion.setText(R.string.questionnaire_welcome_back);
		Button btnClose = (Button) findViewById(R.id.btnIntroNo);
		btnClose.setOnClickListener(btnNextTime_Listener);
		Button btnNext = (Button) findViewById(R.id.btnIntroOk);
		btnNext.setOnClickListener(btnIntro_Listener);
	}

	private void setupEndLayout() {
		introLayout.setVisibility(View.GONE);
		questionsLayout.setVisibility(View.GONE);
		endLayout.setVisibility(View.VISIBLE);

		// setta label fine
		quizQuestion = (TextView) findViewById(R.id.TextView01);
		Button btnNext = (Button) findViewById(R.id.btnExit);
		btnNext.setOnClickListener(btnEnd_Listener);
	}

	private void setupIntroLayout() {
		introLayout.setVisibility(View.VISIBLE);
		questionsLayout.setVisibility(View.GONE);
		endLayout.setVisibility(View.GONE);
		quizQuestion = (TextView) findViewById(R.id.TextView01);
		Button btnClose = (Button) findViewById(R.id.btnIntroNo);
		btnClose.setOnClickListener(btnNextTime_Listener);
		Button btnNext = (Button) findViewById(R.id.btnIntroOk);
		btnNext.setOnClickListener(btnIntro_Listener);
	}

	private boolean isEndTime() {
		if (prefs.contains(PREF_QUIZ) && PREF_END.equals(prefs.getString(PREF_QUIZ, "")))
			return true;
		return false;
	}

	private boolean isQuestionsTime() {
		if (prefs.contains(PREF_QUIZ) && PREF_QUESTIONS.equals(prefs.getString(PREF_QUIZ, "")))
			return true;
		return false;
	}

	private boolean isIntroTime() {
		// check shared preferences if nothing is setted
		if (!prefs.contains(PREF_QUIZ))
			return true;
		return false;
	}

	private void setupQuestionsLayout() {
		introLayout.setVisibility(View.GONE);
		questionsLayout.setVisibility(View.VISIBLE);
		endLayout.setVisibility(View.GONE);
		radioGroup = (RadioGroup) findViewById(R.id.rdbGp1);
		quizQuestion = (TextView) findViewById(R.id.TextView01);
		displayQuestion();
		
		/* Displays the next options and sets listener on next button */
		Button btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(btnNext_Listener);
	}

	
	private View.OnClickListener btnNextTime_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// close everything and goodbye
			Intent mIntent = new Intent(QuizActivity.this, MainActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putBoolean(QuizHelper.TIME_TO_QUIZ, true);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
			finish();
			SharedPreferences.Editor editor = QuizActivity.this.getSharedPreferences(QuizHelper.MY_PREFERENCES,
					Context.MODE_PRIVATE).edit();
			editor.remove(QuizHelper.TIME_TO_QUIZ);
			editor.commit();
		}

	};
	private View.OnClickListener btnSkip_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			nextQuestion();
		}

	};
	/* Called when next button is clicked */
	private View.OnClickListener btnNext_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// send the question and answer selected
			answerNo = radioGroup.getCheckedRadioButtonId();
			if ((openQuestion.getVisibility() == View.GONE && answerNo != -1)
					|| (openQuestion != null && !openQuestion.getText().toString().equals(""))) {
				// if last question, check email
				if (isTheLastQuestion()) {

					if (QuizHelper.isEmailValid(openQuestion.getText().toString())) {
						QuizHelper.sendData(questNo, answerNo, openQuestion.getText().toString(), QuizActivity.this);
					} else {
						Toast.makeText(QuizActivity.this, "Email non valida", Toast.LENGTH_LONG).show();
					}
				} else {
					QuizHelper.sendData(questNo, answerNo, openQuestion.getText().toString(), QuizActivity.this);
				}
			} else {
				Toast.makeText(QuizActivity.this, "Mancano i dati", Toast.LENGTH_LONG).show();
			}

		}

	};

	/* Called when close button is clicked */
	private View.OnClickListener btnIntro_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// close intro and start to do question
			setupQuestionsLayout();

		}

	};

	/* Called when save button is clicked */
	private View.OnClickListener btnEnd_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// close everything and goodbye
			Intent mIntent = new Intent(QuizActivity.this, MainActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putBoolean(QuizHelper.TIME_TO_QUIZ, true);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
			finish();
			SharedPreferences.Editor editor = QuizActivity.this.getSharedPreferences(QuizHelper.MY_PREFERENCES,
					Context.MODE_PRIVATE).edit();
			editor.remove(QuizHelper.TIME_TO_QUIZ);
			editor.commit();
			LogHelper.sendQuestionnarieFinished();
		}
	};
	private CompoundButton.OnCheckedChangeListener rbChange_Listener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// if button is "others", show the openquestion
			if (buttonView.getText().toString().equals(getString(R.string.open_answer))) {
				if (isChecked) {
					openQuestion.setVisibility(View.VISIBLE);
				} else {
					openQuestion.setVisibility(View.GONE);

				}
			}

		}
	};

	private void displayQuestion() {
		
		if (isTheLastQuestion()) {
			//if last question you can also skip the answer
			Button skipButton = (Button) findViewById(R.id.btnSkip);
			skipButton.setVisibility(View.VISIBLE);
			skipButton.setOnClickListener(btnSkip_Listener);
		}
		// Fetching data quiz data and incrementing on each click
		openQuestion.setText("");
		questions = db.getQuestion(questNo);
		answers = db.getAnswers(questNo);
		quizQuestion.setText(questions);
		// check if answer has more than 1 elements
		if (answers.size() > 1) {
			openQuestion.setVisibility(View.GONE);
			radioGroup.setVisibility(View.VISIBLE);
			radioGroup.clearCheck();
			radioGroup.removeAllViews();

			for (int i = 0; i < answers.size(); i++) {
				radioButton = new RadioButton(this);
				radioButton.setText(answers.get(i));
				radioButton.setId(i);
				radioButton.setOnCheckedChangeListener(rbChange_Listener);
				radioGroup.addView(radioButton);

			}
		} else {
			openQuestion.setVisibility(View.VISIBLE);
			radioGroup.setVisibility(View.GONE);
		}

	}

	private boolean isTheLastQuestion() {
		return questNo == db.getQuestions().length - 1;
	}

	@Override
	public void nextQuestion() {
		// if it is sended store the new value on sharedPreferences
		questNo++;
		if (questNo < db.getQuestions().length) {
			displayQuestion();
		} else {
			QuizHelper.finished();
			setupEndLayout();
		}
	}

}