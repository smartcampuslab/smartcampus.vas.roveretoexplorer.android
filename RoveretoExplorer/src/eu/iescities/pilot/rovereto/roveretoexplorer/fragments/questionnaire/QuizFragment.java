package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFragment;

public class QuizFragment extends Fragment implements QuizInterface {
	/** Called when the activity is first created. */
	private static final String PREF_QUIZ = "Quiz";
	private static final String PREF_INTRO = "Intro";
	private static final String PREF_QUESTIONS = "Questions";
	private static final String PREF_END = "End";

	private RadioButton radioButton;
	private TextView quizQuestion;
	private WebView quizQuestionBack;
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
	private int default_answer;

	// private int counter = 1;
	// private String label;


	public static QuizFragment newInstance() {
		QuizFragment f = new QuizFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.quiz, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		db = new QuizHelper(getActivity());
		prefs = db.getQuizPreferences();
		setupLayout();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		LogHelper.removeStartedQuestionnaire();

	}

	private void setupLayout() {
		introLayout = (LinearLayout) getActivity().findViewById(R.id.quizIntro);
		questionsLayout = (LinearLayout) getActivity().findViewById(R.id.quizQuestions);
		endLayout = (LinearLayout) getActivity().findViewById(R.id.quizEnd);
		openQuestion = (EditText) getActivity().findViewById(R.id.editOpenQuestion);
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
		quizQuestionBack = (WebView) getActivity().findViewById(R.id.introText);
		quizQuestionBack.loadData(getString(R.string.questionnaire_welcome_back), "text/html", "utf-8");
		Button btnClose = (Button) getActivity().findViewById(R.id.btnIntroNo);
		btnClose.setOnClickListener(btnNextTime_Listener);
		Button btnNext = (Button) getActivity().findViewById(R.id.btnIntroOk);
		btnNext.setOnClickListener(btnIntro_Listener);
		Button btnNoQuestionnaire = (Button) getActivity().findViewById(R.id.btnNo);
		btnNoQuestionnaire.setOnClickListener(btnNo_Quiz_Listener);
	}

	private void setupEndLayout() {
		introLayout.setVisibility(View.GONE);
		questionsLayout.setVisibility(View.GONE);
		endLayout.setVisibility(View.VISIBLE);

		// setta label fine
		quizQuestion = (TextView) getActivity().findViewById(R.id.TextView01);
		Button btnNext = (Button) getActivity().findViewById(R.id.btnExit);
		btnNext.setOnClickListener(btnEnd_Listener);
	}

	private void setupIntroLayout() {
		introLayout.setVisibility(View.VISIBLE);
		WebView view = (WebView) getActivity().findViewById(R.id.introText);
	    view.setVerticalScrollBarEnabled(false);
	    view.loadData(getString(R.string.questionnary_intro), "text/html", "utf-8");
		questionsLayout.setVisibility(View.GONE);
		endLayout.setVisibility(View.GONE);
		quizQuestion = (TextView) getActivity().findViewById(R.id.TextView01);
		Button btnClose = (Button) getActivity().findViewById(R.id.btnIntroNo);
		btnClose.setOnClickListener(btnNextTime_Listener);
		Button btnNext = (Button) getActivity().findViewById(R.id.btnIntroOk);
		btnNext.setOnClickListener(btnIntro_Listener);
		Button btnNoQuestionnaire = (Button) getActivity().findViewById(R.id.btnNo);
		btnNoQuestionnaire.setOnClickListener(btnNo_Quiz_Listener);
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
		radioGroup = (RadioGroup) getActivity().findViewById(R.id.rdbGp1);
		quizQuestion = (TextView) getActivity().findViewById(R.id.TextView01);
		displayQuestion();

		/* Displays the next options and sets listener on next button */
		Button btnNext = (Button) getActivity().findViewById(R.id.btnNext);
		btnNext.setOnClickListener(btnNext_Listener);
	}

	private View.OnClickListener btnNextTime_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// close everything and goodbye

			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.enter, R.anim.exit);
			Fragment fragment = QuizFragment.newInstance();
			// fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.content_frame, new MapFragment(), MainActivity.TAG_FRAGMENT_MAP);
			ft.addToBackStack(fragment.getTag());
			ft.commit();
			SharedPreferences.Editor editor = getActivity().getSharedPreferences(QuizHelper.MY_PREFERENCES,
					Context.MODE_PRIVATE).edit();
			editor.remove(QuizHelper.TIME_TO_QUIZ);
			editor.commit();
		}

	};
	private View.OnClickListener btnNo_Quiz_Listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			QuizHelper.finished();
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.enter, R.anim.exit);
			Fragment fragment = QuizFragment.newInstance();
			ft.replace(R.id.content_frame, new MapFragment(), MainActivity.TAG_FRAGMENT_MAP);
			ft.addToBackStack(fragment.getTag());
			ft.commit();
			getActivity().finish();
			startActivity(getActivity().getIntent());
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
						QuizHelper.sendData(questNo, answerNo, openQuestion.getText().toString(), QuizFragment.this);
					} else {
						Toast.makeText(getActivity(), "Email non valida", Toast.LENGTH_LONG).show();
					}
				} else {
					QuizHelper.sendData(questNo, answerNo, openQuestion.getText().toString(), QuizFragment.this);
				}
			} else {
				Toast.makeText(getActivity(), "Mancano i dati", Toast.LENGTH_LONG).show();
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
			
			// close everything and show the map fragment
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.enter, R.anim.exit);
			Fragment fragment = QuizFragment.newInstance();
			ft.replace(R.id.content_frame, new MapFragment(), MainActivity.TAG_FRAGMENT_MAP);
			ft.addToBackStack(fragment.getTag());
			ft.commit();
			SharedPreferences.Editor editor = getActivity().getSharedPreferences(QuizHelper.MY_PREFERENCES,
					Context.MODE_PRIVATE).edit();
			editor.remove(QuizHelper.TIME_TO_QUIZ);
			editor.commit();
			LogHelper.sendQuestionnarieFinished(getActivity());

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
		default_answer=0;
		if (isTheLastQuestion()) {
			// if last question you can also skip the answer
			Button skipButton = (Button) getActivity().findViewById(R.id.btnSkip);
			skipButton.setVisibility(View.VISIBLE);
			skipButton.setOnClickListener(btnSkip_Listener);
		}
		// Fetching data quiz data and incrementing on each click
		openQuestion.setText("");
		questions = db.getQuestion(questNo);
		answers = db.getAnswers(questNo);
		default_answer=db.getDefaultAnswer(questNo);
		quizQuestion.setText(questions);
		// check if answer has more than 1 elements
		if (answers.size() > 1) {
			openQuestion.setVisibility(View.GONE);
			radioGroup.setVisibility(View.VISIBLE);
			radioGroup.clearCheck();
			radioGroup.removeAllViews();

			for (int i = 0; i < answers.size(); i++) {
				radioButton = new RadioButton(getActivity());
				radioButton.setText(answers.get(i));
				radioButton.setId(i);
				radioButton.setOnCheckedChangeListener(rbChange_Listener);
				radioGroup.addView(radioButton);
				if (default_answer==i)
					radioButton.setChecked(true);

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
		// if it is sent store the new value
		//on sharedPreferences used for restoring
		questNo++;
		if (questNo < db.getQuestions().length) {
			displayQuestion();
		} else {
			QuizHelper.finished();
			setupEndLayout();
			getActivity().finish();
			getActivity().startActivity(getActivity().getIntent());
		}
	}

}