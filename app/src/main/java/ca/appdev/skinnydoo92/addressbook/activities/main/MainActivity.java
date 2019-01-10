package ca.appdev.skinnydoo92.addressbook.activities.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ca.appdev.skinnydoo92.addressbook.R;
import ca.appdev.skinnydoo92.addressbook.fragments.addedit.AddEditFragment;
import ca.appdev.skinnydoo92.addressbook.fragments.contacts.ContactsFragment;
import ca.appdev.skinnydoo92.addressbook.fragments.detail.DetailFragment;

/**
 * Host the app's fragments and handles communication between them
 */
public class MainActivity extends AppCompatActivity
	implements ContactsFragment.OnContactFragmentInteractionListener,
	           DetailFragment.OnDetailFragmentInteractionListener,
	           AddEditFragment.OnAddEditFragmentInteractionListener {
	
	// Key for storing a contact's Uri in a Bundle passed to a fragment
	public static final String CONTACT_URI = "contact_uri";
	public static final String CONTACT_FRAGMENT_TAG = "contact_fragment";
	
	// Private Members
	private ContactsFragment mContactsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		// Handle fragments and configuration changes
		// If layout contains fragmentContainer, the phone layout is in use
		// create and display a ContactsFragment
		if(savedInstanceState == null && findViewById(R.id.fragmentContainer) != null) {
			
			// Create ContactsFragment
			mContactsFragment = new ContactsFragment();
			// Add the fragment to the FrameLayout
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.fragmentContainer, mContactsFragment, CONTACT_FRAGMENT_TAG);
			transaction.commit(); // display ContactsFragment
			
		} else { // get references to Fragments that have already been restored
			mContactsFragment = (ContactsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.contactsFragment);
		}
		
	}
	
	// Display DetailFragment for selected contact
	@Override
	public void onContactSelected(Uri contactUri) {
		
		if(findViewById(R.id.fragmentContainer) != null) { // phone
			displayContact(contactUri, R.id.fragmentContainer);
		}
		else { // tablet
			// removes top of back stack
			getSupportFragmentManager().popBackStack();
			displayContact(contactUri, R.id.rightPaneContainer);
		}
	}
	
	// display AddEditFragment to add contact
	@Override
	public void onAddContact() {
		
		if(findViewById(R.id.fragmentContainer) != null) // phone
			displayAddEditFragment(R.id.fragmentContainer, null);
		else // tablet
			displayAddEditFragment(R.id.rightPaneContainer, null);
	}
	
	// return to contact list when displayed contact deleted
	@Override
	public void onContactDeleted() {
		// remove top of back stack
		getSupportFragmentManager().popBackStack();
		mContactsFragment.updateContactList(); // refresh contact
	}
	
	// display the AddEditFragment to edit an existing contact
	@Override
	public void onEditContact(Uri contactUri) {
		
		if(findViewById(R.id.fragmentContainer) != null) {// phone
			displayAddEditFragment(R.id.fragmentContainer, contactUri);
		}
		else {// tablet
			displayAddEditFragment(R.id.rightPaneContainer, contactUri);
		}
	}
	
	// update GUI after new contact or updated contact saved
	@Override
	public void onAddEditCompleted(Uri contactUri) {
		
		// removes top of back stack
		getSupportFragmentManager().popBackStack();
		mContactsFragment.updateContactList(); // refresh contacts
		
		if(findViewById(R.id.fragmentContainer) == null) { // tablet
			
			// removes top of back stack
			getSupportFragmentManager().popBackStack();
			
			// on tablet, display contact that was just added or edited
			displayContact(contactUri, R.id.rightPaneContainer);
			
		}
	}
	
	private void displayContact(Uri contactUri, int viewID) {
		
		DetailFragment detailFragment = new DetailFragment();
		
		// specify contact's Uri as an argument to the DetailFragment
		Bundle args = new Bundle();
		args.putParcelable(CONTACT_URI, contactUri);
		detailFragment.setArguments(args);
		
		// Use a FragmentTransaction to display the DetailFragment
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(viewID, detailFragment);
		transaction.addToBackStack(null);
		transaction.commit(); // display DetailFragment
	}
	
	// Display fragment to adding a new or editing an existing contact
	private void displayAddEditFragment(int viewID, Uri contactUri) {
		
		AddEditFragment addEditFragment = new AddEditFragment();
		
		// if editing existing contact, provide contactUri as an argument
		if(contactUri != null) {
			Bundle args = new Bundle();
			args.putParcelable(CONTACT_URI, contactUri);
			addEditFragment.setArguments(args);
		}
		
		// Use a FragmentTransaction to display the AddEditFragment
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(viewID, addEditFragment);
		transaction.addToBackStack(null);
		transaction.commit(); // causes AddEditFragment to display
	}
}
