package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ToKnow;

public class EventDetailToKnowAdapter extends ArrayAdapter<ToKnow> {

	private Context mContext;
	private int layoutResourceId;
	private String mTag;
	private String mEventId;

	public EventDetailToKnowAdapter(Context mContext, int layoutResourceId, String mTag, String mEventId) {
		super(mContext, layoutResourceId);
		this.mContext = mContext;
		this.layoutResourceId = layoutResourceId;
		this.mTag = mTag;
		this.mEventId = mEventId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DaSapereHolder holder = null;

		final int pos = position;

		ToKnow toKnow = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new DaSapereHolder();
			holder.toKnowTitle = (TextView) row.findViewById(R.id.daSapereTitleTextView);
			holder.toKnowContent = (TextView) row.findViewById(R.id.daSapereContentTextView);
			holder.toKnowDelete = (ImageButton) row.findViewById(R.id.daSapereDeleteButton);
			holder.toKnowEdit = (ImageButton) row.findViewById(R.id.daSapereEditButton);
			row.setTag(holder);
		} else {
			holder = (DaSapereHolder) row.getTag();
		}

		if (toKnow.getTitle().startsWith("_toknow_")) {
			holder.toKnowDelete.setVisibility(ImageButton.GONE);
			Integer resId = getContext().getResources().getIdentifier(toKnow.getTitle(), "string",
					"eu.iescities.pilot.rovereto.roveretoexplorer");
			if (resId != null && resId != 0) {
				String mandatoryTitle = getContext().getResources().getString(resId);
				holder.toKnowTitle.setText(mandatoryTitle);
			}
		} else {
			holder.toKnowDelete.setVisibility(ImageButton.VISIBLE);
			holder.toKnowTitle.setText(toKnow.getTitle());
		}

		holder.toKnowContent.setText(toKnow.getContent());

		holder.toKnowEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager()
						.beginTransaction();
				Bundle args = new Bundle();
				String frag_description = null;

				Fragment editFragment = new Fragment_EvDetail_DaSapere_Form();
				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + mEventId + "!!");
				args.putString(Fragment_EvDetail_DaSapere_Form.ARG_EVENT_ID, mEventId);
				args.putInt(Fragment_EvDetail_DaSapere_Form.ARG_TOKNOW_INDEX, pos);
				frag_description = "Fragment_EvDetail_DaSapere_Form";

				editFragment.setArguments(args);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, editFragment, frag_description);
				fragmentTransaction.addToBackStack(mTag);
				fragmentTransaction.commit();
				// reset event and event id
				// fragment.mEvent = null;
				// fragment.mEventId = null;
			}
		});

		return row;
	}

	public static class DaSapereHolder {
		TextView toKnowTitle;
		TextView toKnowContent;
		ImageButton toKnowDelete;
		ImageButton toKnowEdit;
	}
}
