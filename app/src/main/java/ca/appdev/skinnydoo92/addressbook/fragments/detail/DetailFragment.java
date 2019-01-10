package ca.appdev.skinnydoo92.addressbook.fragments.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import ca.appdev.skinnydoo92.addressbook.R;
import ca.appdev.skinnydoo92.addressbook.activities.main.MainActivity;
import ca.appdev.skinnydoo92.addressbook.enums.ErrorMessages;
import ca.appdev.skinnydoo92.addressbook.fragments.dialogs.ConfirmDeleteDialogFragment;

import static ca.appdev.skinnydoo92.addressbook.data.AddressBookDatabaseDescription.Contact;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment
	implements LoaderManager.LoaderCallbacks<Cursor> {
	
	/* Public Members */
	public static final String CONFIRM_DELETE_TAG = "confirm_delete";
	
	/* Private Members */
	private static final int CONTACT_LOADER = 0;
	
	private OnDetailFragmentInteractionListener mListener;
	private Uri mContactUri;
	
	private TextView mNameTextView; // displays contact's name
	private TextView mPhoneTextView; // displays contact's phone
	private TextView mEmailTextView; // displays contact's email
	private TextView mStreetTextView; // displays contact's street
	private TextView mCityTextView; // displays contact's city
	private TextView mStateTextView; // displays contact's state
	private TextView mZipTextView; // displays contact's zip
	
	public DetailFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onAttach(Context context) {
		
		super.onAttach(context);
		if(context instanceof OnDetailFragmentInteractionListener) {
			mListener = (OnDetailFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnDetailFragmentInteractionListener");
		}
	}
	
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true); // fragment has menu item to display
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		
		// get the TextViews
		mNameTextView = view.findViewById(R.id.nameTextView);
		mPhoneTextView = view.findViewById(R.id.phoneTextView);
		mEmailTextView = view.findViewById(R.id.emailTextView);
		mStreetTextView = view.findViewById(R.id.streetTextView);
		mCityTextView = view.findViewById(R.id.cityTextView);
		mStateTextView = view.findViewById(R.id.stateTextView);
		mZipTextView = view.findViewById(R.id.zipTextView);
		
		// Get Bundle of args then extract the contact's uri
		Bundle args = getArguments();
		if(args != null) {
			mContactUri = args.getParcelable(MainActivity.CONTACT_URI);
		}
		
		return  view;
	}
	
	// Initialize a loader when this fragment's activity is created
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(CONTACT_LOADER, null, this);
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_fragment_details, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			
			case R.id.action_edit :
				mListener.onEditContact(mContactUri);
				return true;
				
			case R.id.action_delete :
				deleteContact();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		
		// Construct the new query in the form of a Cursor Loader. Use the id
		// parameter to construct and return different loaders.
		String[] projection = null;     // null projection returns all columns
		String where = null;            // null selection returns all rows
		String[] whereArgs = null;      // no selection arguments
		String sortOrder = null;        // no sorting
		
		// Query URI
		Uri queryUri = mContactUri; // Uri of contact to display
		
		// Create the new Cursor Loader
		return new CursorLoader(
			Objects.requireNonNull(getActivity(), ErrorMessages.ACTIVITY_NOT_NULL.toString()),
			queryUri, projection, where, whereArgs, sortOrder);
	}
	
	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		
		// If the contact exist in the database, display adapter
		if(cursor != null && cursor.moveToFirst()) {
			
			// Get the value for each data item
			String name = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME));
			String phone = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_PHONE));
			String email = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_EMAIL));
			String street = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_STREET));
			String city = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_CITY));
			String state = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_STATE));
			String zip = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_ZIP));
			
			// fill TextViews with the retrieved data
			mNameTextView.setText(name);
			mPhoneTextView.setText(phone);
			mEmailTextView.setText(email);
			mStreetTextView.setText(street);
			mCityTextView.setText(city);
			mStateTextView.setText(state);
			mZipTextView.setText(zip);
		}
	}
	
	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {}
	
	/**
	 * Uses FragmentManager to display the confirmDelete DialogFragment
	 */
	private void deleteContact() {
		
		Bundle args = new Bundle();
		args.putParcelable(MainActivity.CONTACT_URI, mContactUri);
		args.putString(ConfirmDeleteDialogFragment.TITLE_ID, getString(R.string.confirm_title));
		args.putString(ConfirmDeleteDialogFragment.MESSAGE_ID, getString(R.string.confirm_message));
		
		ConfirmDeleteDialogFragment deleteDialogFragment = new ConfirmDeleteDialogFragment();
		deleteDialogFragment.setListener(mListener);
		deleteDialogFragment.setArguments(args);
		deleteDialogFragment.show(Objects.requireNonNull(getFragmentManager(),
			ErrorMessages.FRAGMENT_MANAGER_NOT_NULL.toString()), CONFIRM_DELETE_TAG);
		
	}
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 */
	public interface OnDetailFragmentInteractionListener {
		
		// called when a contact is deleted
		void onContactDeleted();
		
		// pass Uri of contact to edit to the OnDetailFragmentInteractionListener
		void onEditContact(Uri contactUri);
	}
}
