package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ToKnow;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.edit.Fragment_EvDetail_Edit_MultiValueField;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.edit.Fragment_EvDetail_Edit_SingleValueField;
import eu.trentorise.smartcampus.network.RemoteException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class EventDetailToKnowAdapter extends ArrayAdapter<ToKnow> {

	private Context mContext;
	private int layoutResourceId;
	private String mTag;
	private ExplorerObject mEvent;

	private View row = null;
	private EventInfoChildViewHolder eventChildViewHolder = null;

	public EventDetailToKnowAdapter(Context mContext, int layoutResourceId,
			String mTag, ExplorerObject mEvent) {
		super(mContext, layoutResourceId);
		this.mContext = mContext;
		this.layoutResourceId = layoutResourceId;
		this.mTag = mTag;
		this.mEvent = mEvent;
	}

	@Override
	public void addAll(Collection<? extends ToKnow> collection) {
		for (ToKnow toknowItem : collection) {
			add(toknowItem);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ToKnow toKnow = getItem(position);

		row = convertView;

		if (row == null) {
			// Inflate event_info_child_item.xml file for child rows

			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			eventChildViewHolder = new EventInfoChildViewHolder();
			eventChildViewHolder.bullet = (TextView) row
					.findViewById(R.id.event_info_bullet);
			eventChildViewHolder.text = (TextView) row
					.findViewById(R.id.event_info_attribute_values);
			eventChildViewHolder.imgsDx1 = (ImageView) row
					.findViewById(R.id.event_info_action1);

			row.setTag(eventChildViewHolder);
		} else {
			eventChildViewHolder = (EventInfoChildViewHolder) row.getTag();
		}

		// Get event_info_child_item.xml file elements and set values
		Map<String, List<String>> toKnowMap = Utils
				.getCustomToKnowDataFromEvent(mEvent);
//		if (toKnowMap.get(toKnow.getName()) == null
//				|| toKnowMap.get(toKnow.getName()).isEmpty()) {
//			row.setVisibility(View.GONE);
//			return row;
//		}
		Log.d("DASAPERE",
				"EventDetailToKnowAdapter --> toKnow NAME: " + toKnow.getName());

		if (toKnow.getName().startsWith("_toknow_")) {
			Integer resId = getContext().getResources().getIdentifier(
					toKnow.getName(), "string",
					"eu.iescities.pilot.rovereto.roveretoexplorer");
			if (resId != null && resId != 0) {
				String mandatoryTitle = getContext().getResources().getString(
						resId);
				eventChildViewHolder.text.setText(mandatoryTitle);
			}

			// the element is an attribute

		} else {
			// the element is a value or an attribute added by a user

			eventChildViewHolder.text.setText(toKnow.getName());
		}

		// set the typeface for text
		if (toKnow.getTextInBold()) {
			// title
			eventChildViewHolder.bullet.setVisibility(View.VISIBLE);
			eventChildViewHolder.text.setTypeface(null, Typeface.BOLD);
			eventChildViewHolder.text.setTextColor(mContext.getResources()
					.getColor(toKnow.getDividerColor()));
		} else {
			// content
			eventChildViewHolder.bullet.setVisibility(View.INVISIBLE);
			eventChildViewHolder.text.setTypeface(null, Typeface.NORMAL);
			eventChildViewHolder.text.setTextColor(mContext.getResources()
					.getColor(toKnow.getDefault_text_color()));
		}

		// set icon on the left side
		if (toKnow.getLeftIconId() != -1) {
			eventChildViewHolder.text.setCompoundDrawablesWithIntrinsicBounds(
					toKnow.getLeftIconId(), 0, 0, 0);
		} else
			eventChildViewHolder.text.setCompoundDrawablesWithIntrinsicBounds(
					0, 0, 0, 0);

		// set icons on the right side for the items of type 1 (telefono, email,
		// )
		if ((toKnow.getRightIconIds() != null)) {
			// Log.i("TOKNOW", "CHILD DX1 ICON ID: "
			// + toKnow.getRightIconIds()[0]);
			// eventChildViewHolder.text.setTypeface(null, Typeface.BOLD);

			eventChildViewHolder.imgsDx1.setVisibility(View.VISIBLE);
			eventChildViewHolder.imgsDx1.setImageResource(toKnow
					.getRightIconIds()[0]);
			eventChildViewHolder.imgsDx1
					.setOnClickListener(new EditClickListener(toKnow));
		} else {
			eventChildViewHolder.imgsDx1.setVisibility(View.INVISIBLE);
		}

		return row;
	}

	private static class EventInfoChildViewHolder {
		TextView bullet;
		TextView text;
		ImageView imgsDx1;
		ImageView imgsDx2;
		ImageView imgsDx3;
		int position;
	}

	private class UpdateEventProcessor extends
			AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		private ToKnow toKnow;

		public UpdateEventProcessor(Activity activity, ToKnow toKnow) {
			super(activity);
			this.toKnow = toKnow;
		}

		@Override
		public Boolean performAction(ExplorerObject... params)
				throws SecurityException, RemoteException, Exception {
			// to be enabled when the connection with the server is ok
			return DTHelper.saveEvent(params[0], mContext);
		}

		@Override
		public void handleResult(Boolean result) {
			if (getContext() != null) {
				if (result) {
					Toast.makeText(getContext(), R.string.event_create_success,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getContext(), R.string.update_success,
							Toast.LENGTH_SHORT).show();
				}
				remove(toKnow);
				// getActivity().getSupportFragmentManager().popBackStack();
			}
		}
	}

	/******************* Checkbox Checked Change Listener ********************/

	private final class EditClickListener implements OnClickListener {
		private final ToKnow row;

		private EditClickListener(ToKnow row) {
			this.row = row;
		}

		@Override
		public void onClick(View v) {

			Log.i("DASAPERE", "Right Icon Pressed!");

			FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext())
					.getSupportFragmentManager().beginTransaction();

			Fragment edit_fragment = null;
			Bundle args = new Bundle();
			String frag_description = null;

			if (!row.getMultiValue()) {
				// call a fragment where only one values is shown
				edit_fragment = new Fragment_EvDetail_Edit_SingleValueField();
				// Log.i("CONTACTS",
				// "EventDetailToKnowAdapter --> event selected ID: " + mEventId
				// + "!!");
				args.putString(Utils.ARG_EVENT_ID, mEvent.getId());
				args.putString(Utils.ARG_EVENT_FIELD_TYPE, row.getName());

				frag_description = "event_details_custom_edit_singlevalue";
			} else {
				// call a fragment where multivalues are allowed
				edit_fragment = new Fragment_EvDetail_Edit_MultiValueField();
				// Log.i("CONTACTS",
				// "EventDetailToKnowAdapter  --> event selected ID: " +
				// mEventId + "!!");
				args.putString(Utils.ARG_EVENT_ID, mEvent.getId());
				args.putString(Utils.ARG_EVENT_FIELD_TYPE, row.getName());
				args.putBoolean(Utils.ARG_EVENT_FIELD_TYPE_IS_MANDATORY,
						!row.getAddedbyUser());
				frag_description = "event_details_custom_edit_multivalue";
			}

			if (edit_fragment != null) {
				edit_fragment.setArguments(args);
				fragmentTransaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, edit_fragment,
						frag_description);
				fragmentTransaction.addToBackStack(edit_fragment.getTag());
				fragmentTransaction.commit();
				// reset event and event id
				// mEvent=null;
				// mEventId=null;
			}

		}

	}

}
