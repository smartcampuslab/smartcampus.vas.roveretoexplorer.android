package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Constants;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ToKnow;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere.EventDetailToKnowAdapter_OLD.DaSapereHolder;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.network.RemoteException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class EventDetailToKnowAdapter extends ArrayAdapter<ToKnow> {

	private Context mContext;
	private int layoutResourceId;
	private String mTag;
	private String mEventId;
	
	private View row = null;
	private EventInfoChildViewHolder eventChildViewHolder = null;


	

	public EventDetailToKnowAdapter(Context mContext, int layoutResourceId, String mTag, String mEventId) {
		super(mContext, layoutResourceId);
		this.mContext = mContext;
		this.layoutResourceId = layoutResourceId;
		this.mTag = mTag;
		this.mEventId = mEventId;
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
			eventChildViewHolder.text = (TextView) row
					.findViewById(R.id.event_info_attribute_values);
			eventChildViewHolder.imgsDx1 = (ImageView) row
					.findViewById(R.id.event_info_action1);

			eventChildViewHolder.divider = (View) row
					.findViewById(R.id.event_info_item_divider);

			//this will be added again when it will be possible to cancel/edit the single items
			//			eventChildViewHolder.imgsDx2 = (ImageView) row
			//					.findViewById(R.id.event_info_action2);
			//			eventChildViewHolder.imgsDx3 = (ImageView) row
			//					.findViewById(R.id.event_info_action3);

			row.setTag(eventChildViewHolder);
		} else {
			eventChildViewHolder = (EventInfoChildViewHolder) row.getTag();
		}

		// Get event_info_child_item.xml file elements and set values


		if (toKnow.getTextInBold())
			eventChildViewHolder.text.setTypeface(null, Typeface.BOLD);
		else
			eventChildViewHolder.text.setTypeface(null, Typeface.NORMAL);


		if (!toKnow.getText().contains("http")){
			eventChildViewHolder.text.setText(toKnow.getText());
		}
		else{

			if(!toKnow.getText().matches(mContext.getString(R.string.start_url))){

				Log.i("TOKNOW", "make the text part clickable!!!");

				//make the text part clickable
				int i1 = 0;
				int i2 = toKnow.getName().length()-1;
				eventChildViewHolder.text.setMovementMethod(LinkMovementMethod.getInstance());
				eventChildViewHolder.text.setText(toKnow.getName(), BufferType.SPANNABLE);
		
				Spannable mySpannable = (Spannable)eventChildViewHolder.text.getText();
				ClickableSpan myClickableSpan = new ClickableSpan()
				{
					@Override
					public void onClick(View widget) { 
						String url = toKnow.getText();
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url)); 
						mContext.startActivity(i); 

					}
				};
				mySpannable.setSpan(myClickableSpan, i1, i2 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			else
				eventChildViewHolder.text.setText(toKnow.getName());
		}
		

		// set icon on the left side
		if (toKnow.getLeftIconId() != -1) {
			Log.i("TOKNOW", "CHILD SX ICON ID: " + toKnow.getLeftIconId());
			eventChildViewHolder.text.setCompoundDrawablesWithIntrinsicBounds(toKnow.getLeftIconId(), 0, 0, 0);
		}else
			eventChildViewHolder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		
		
		// set icons on the right side for the items of type 1 (telefono, email, )
		if ((toKnow.getRightIconIds() != null) && (toKnow.getType() == 1)) {
			Log.i("TOKNOW", "CHILD DX1 ICON ID: "
					+ toKnow.getRightIconIds()[0]);

			eventChildViewHolder.text.setTypeface(null, Typeface.BOLD);

			eventChildViewHolder.imgsDx1.setVisibility(View.VISIBLE);
			eventChildViewHolder.imgsDx1.setImageResource(toKnow.getRightIconIds()[0]);
			eventChildViewHolder.imgsDx1
			.setOnClickListener(new ChildAddIconClickListener(
					mContext, toKnow));
		} else {
			Log.i("TOKNOW", "CHILD DX1 ICON NULL");
			eventChildViewHolder.imgsDx1.setVisibility(View.INVISIBLE);
		}


		//set divider line height and color
		eventChildViewHolder.divider.setBackgroundColor(toKnow.getDividerColor());
		eventChildViewHolder.divider.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				toKnow.getDividerHeight()));





		// set icons on the right side for the items of type 0 (single values)
		if ((toKnow.getRightIconIds() != null) && (toKnow.getType() == 0)) {
			Log.i("TOKNOW", "CHILD DX1 ICON ID: "
					+ toKnow.getRightIconIds()[0]);
			int iconsNumb = toKnow.getRightIconIds().length;
			Log.i("TOKNOW", "ICON NUMMBER: " + iconsNumb);
			eventChildViewHolder.imgsDx1.setVisibility(View.VISIBLE);
			eventChildViewHolder.imgsDx1.setImageResource(toKnow.getRightIconIds()[0]);
			eventChildViewHolder.imgsDx1
			.setOnClickListener(new ChildActionIconClickListener(
					mContext, toKnow));
			//this will be added when cancel/edit for single item will be possible
			//			eventChildViewHolder.imgsDx2.setVisibility(View.VISIBLE);
			//			eventChildViewHolder.imgsDx2.setImageResource(child
			//					.getRightIconIds()[1]);
			//			if (iconsNumb == 3)
			//				eventChildViewHolder.imgsDx3.setVisibility(View.VISIBLE);
			//			eventChildViewHolder.imgsDx3.setImageResource(child
			//					.getRightIconIds()[2]);

		} else {
			Log.i("TOKNOW", "CHILD DX1 ICON NULL");
			eventChildViewHolder.imgsDx1.setVisibility(View.INVISIBLE);

			//this will be added when cancel/edit for single item will be possible
			//			eventChildViewHolder.imgsDx2.setVisibility(View.INVISIBLE);
			//			eventChildViewHolder.imgsDx3.setVisibility(View.INVISIBLE);
		}

		return row;
	}

		
	private static class EventInfoChildViewHolder {
		TextView text;
		ImageView imgsDx1;
		ImageView imgsDx2;
		ImageView imgsDx3;
		View divider;
		int position;
	}
	
	
	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		private EventToKnowRow toKnow;

		public UpdateEventProcessor(Activity activity, EventToKnowRow toKnow) {
			super(activity);
			this.toKnow = toKnow;
		}

		@Override
		public Boolean performAction(ExplorerObject... params) throws SecurityException, RemoteException, Exception {
			// to be enabled when the connection with the server is ok
			return DTHelper.saveEvent(params[0]);
		}

		@Override
		public void handleResult(Boolean result) {
			if (getContext() != null) {
				if (result) {
					Toast.makeText(getContext(), R.string.event_create_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
				}
				remove(toKnow);
				// getActivity().getSupportFragmentManager().popBackStack();
			}
		}
	}
}
