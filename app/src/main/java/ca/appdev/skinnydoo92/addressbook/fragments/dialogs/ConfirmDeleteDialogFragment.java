package ca.appdev.skinnydoo92.addressbook.fragments.dialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.Objects;

import ca.appdev.skinnydoo92.addressbook.R;
import ca.appdev.skinnydoo92.addressbook.activities.main.MainActivity;
import ca.appdev.skinnydoo92.addressbook.enums.ErrorMessages;
import ca.appdev.skinnydoo92.addressbook.fragments.detail.DetailFragment
	.OnDetailFragmentInteractionListener;

public class ConfirmDeleteDialogFragment extends DialogFragment {
	
	/* Public Members */
	// supply keys for the Bundle
	public static final String TITLE_ID = "title";
	public static final String MESSAGE_ID = "message";
	
	/* Private Members */
	private Uri mContactUri; // Uri of selected contact
	private OnDetailFragmentInteractionListener mListener;
	
	
	
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder =
			new AlertDialog.Builder(Objects.requireNonNull(getActivity(),
				ErrorMessages.ACTIVITY_NOT_NULL.toString()));
		
		// Get supplied args
		Bundle args = getArguments();
		if(args != null) {
			
			builder.setTitle(args.getString(TITLE_ID, ErrorMessages.SORRY.toString()));
			builder.setMessage(args.getString(MESSAGE_ID, ErrorMessages.AN_ERROR_OCCURRED.toString()));
			mContactUri = args.getParcelable(MainActivity.CONTACT_URI);
		} else {
			// supply default text if no argument were set
			builder.setTitle(ErrorMessages.SORRY.toString());
			builder.setMessage(ErrorMessages.AN_ERROR_OCCURRED.toString());
			return builder.create();
		}
		
		builder.setPositiveButton(R.string.button_delete, (dialog, which) -> {
			
			// Use Activity's ContentResolver to invoke delete on the AddressBoo
			getActivity().getContentResolver().delete(mContactUri, null, null);
			// notify listener
			Objects.requireNonNull(mListener, ErrorMessages.LISTENER_NOT_NULL.toString()).onContactDeleted();
		});
		
		builder.setNegativeButton(R.string.button_cancel, null);
		
		return builder.create();
	}
	
	public void setListener(
		OnDetailFragmentInteractionListener mListener) {
		
		this.mListener = mListener;
	}
}
