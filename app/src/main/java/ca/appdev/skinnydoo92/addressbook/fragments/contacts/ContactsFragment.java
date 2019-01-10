package ca.appdev.skinnydoo92.addressbook.fragments.contacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ca.appdev.skinnydoo92.addressbook.R;
import ca.appdev.skinnydoo92.addressbook.adapters.ContactsAdapter;
import ca.appdev.skinnydoo92.addressbook.utils.ItemDivider;
import ca.appdev.skinnydoo92.addressbook.utils.LogWrapper;

import static ca.appdev.skinnydoo92.addressbook.data.AddressBookDatabaseDescription.Contact;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnContactFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsFragment extends Fragment
	implements LoaderManager.LoaderCallbacks<Cursor>,
	           ContactsAdapter.ContactClickListener {
	
	private static final int CONTACT_LOADER = 0;
	
	// used to inform MainActivity when a contact is selected
	private OnContactFragmentInteractionListener mListener;
	// Adapter for RecyclerView
	private ContactsAdapter mAdapter;
	
	// constructor
	public ContactsFragment() {
	
	}
	
	/**
	 * Set OnContactFragmentInteractionListener when fragment attached
	 * @param context
	 */
	@Override
	public void onAttach(Context context) {
		
		super.onAttach(context);
		if(context instanceof OnContactFragmentInteractionListener) {
			mListener = (OnContactFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnContactFragmentInteractionListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true); // Fragment has menu items to display
		
		View view = inflater.inflate(R.layout.fragment_contacts, container, false);
		RecyclerView recycler = view.findViewById(R.id.contactRecycler);
		
		// Recycler should display items in a vertical list
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		// Create RecyclerView's adapter and item click listner
		mAdapter = new ContactsAdapter(this);
		recycler.setAdapter(mAdapter);
		
		// Attach a custom ItemDecorator to draw dividers between list items
		recycler.addItemDecoration(new ItemDivider(Objects.requireNonNull(getContext())));
		
		// improves performance if RecyclerView's layout size never changes
		recycler.setHasFixedSize(true);
		
		FloatingActionButton addButtonFAB = view.findViewById(R.id.addButton);
		addButtonFAB.setOnClickListener(v -> {
			LogWrapper.d("Adding new contact");
			mListener.onAddContact();
		});
		
		return view;
	}
	
	// Initialize a loader when this fragment's activity is created
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(CONTACT_LOADER, null, this);
	}
	
	/**
	 * Remove OnContactFragmentInteractionListener when fragment detached
	 */
	@Override
	public void onDetach() {
		
		super.onDetach();
		mListener = null;
	}
	
	// Called from MainActivity when other fragment's update the database
	public void updateContactList() {
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onContactClick(Uri contactUri) {
		
		if(mListener == null) { return; }
		LogWrapper.d("User touches contact #" + contactUri.getLastPathSegment());
		mListener.onContactSelected(contactUri);
	}
	
	/**
	 * Called by LoaderManager to create a loader
	 * @param id the Id of the loader
	 * @param args
	 * @return {CursorLoader}
	 */
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		
		// Create an appropriate CursorLoader based on the id argument
		switch (id) {
			
			case CONTACT_LOADER:
				return new CursorLoader(Objects.requireNonNull(getActivity()),
					Contact.CONTENT_URI,      // Uri of contacts table
					null,           // null projection returns all columns
					null,           // null selection returns all rows
					null,       // no selection args
					Contact.COLUMN_NAME + " COLLATE NOCASE ASC");
				
			default: return null;
		}
	}
	
	/**
	 * Called by LoaderManager when loading completes
	 * @param loader the loader
	 * @param cursor the cursor
	 */
	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}
	
	/**
	 * Called by LoaderManager when the loader is being reset
	 * @param loader the cursor loader
	 */
	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 */
	public interface OnContactFragmentInteractionListener {
		
		void onContactSelected(Uri contactUri); // called when a contact is selected
		void onAddContact(); // called with add button is pressed
	}
}
