package eu.iescities.pilot.rovereto.roveretoexplorer.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import eu.iescities.pilot.rovereto.roveretoexplorer.MainActivity;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.EventsListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.SearchFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapManager;

public class CategoryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.category_fragment, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		initLayoutAction();
	}

	private void initLayoutAction() {

		LinearLayout lAll = (LinearLayout) getActivity().findViewById(
				R.id.layout_category_all);
		lAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cat = CategoryHelper.CAT_CULTURA;
				Bundle args = new Bundle();
				args.putBoolean(SearchFragment.ARG_ALL, true);
				executeTransactionEventListFragment(args);
			}
		});

		LinearLayout lCultural = (LinearLayout) getActivity().findViewById(
				R.id.layout_category_cultural);
		lCultural.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cat = CategoryHelper.CAT_CULTURA;
				Bundle args = new Bundle();
				args.putString(SearchFragment.ARG_CATEGORY, cat);
				executeTransactionEventListFragment(args);

			}
		});
		LinearLayout lSport = (LinearLayout) getActivity().findViewById(
				R.id.layout_category_sport);
		lSport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cat = CategoryHelper.CAT_SPORT;
				Bundle args = new Bundle();
				args.putString(SearchFragment.ARG_CATEGORY, cat);
				executeTransactionEventListFragment(args);
			}
		});
		LinearLayout lPerson = (LinearLayout) getActivity().findViewById(
				R.id.layout_category_social);
		lPerson.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cat = CategoryHelper.CAT_SOCIALE;
				Bundle args = new Bundle();
				args.putString(SearchFragment.ARG_CATEGORY, cat);
				executeTransactionEventListFragment(args);
			}
		});
		LinearLayout lOthers = (LinearLayout) getActivity().findViewById(
				R.id.layout_category_others);
		lOthers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cat = CategoryHelper.EVENT_NONCATEGORIZED;
				Bundle args = new Bundle();
				args.putString(SearchFragment.ARG_CATEGORY, cat);
				executeTransactionEventListFragment(args);
			}
		});
		((ActionBarActivity) getActivity()).getSupportActionBar()
				.setBackgroundDrawable(
						new ColorDrawable(Color.parseColor(getResources()
								.getString(R.color.actionbar_default))));
		((ActionBarActivity) getActivity()).getSupportActionBar()
				.setDisplayShowTitleEnabled(false);
		((ActionBarActivity) getActivity()).getSupportActionBar()
				.setDisplayShowTitleEnabled(true);
	}

	private void executeTransactionEventListFragment(Bundle args) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.setCustomAnimations(R.anim.enter, R.anim.exit);
		Fragment elf = new EventsListingFragment();
		elf.setArguments(args);
		ft.replace(R.id.content_frame, elf,
				MainActivity.TAG_FRAGMENT_EVENT_LIST);
		ft.addToBackStack(args.toString());
		getActivity().getSupportFragmentManager().popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		ft.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.category_event_menu,
				menu);
		super.onPrepareOptionsMenu(menu);

		// menu.findItem(R.id.map_view).setVisible(false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.map_view) {
			MapManager.switchToMapView(CategoryHelper.CATEGORY_ALL,
					MapFragment.ARG_EVENT_CATEGORY, this);
			return true;
		}
		return false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
}
