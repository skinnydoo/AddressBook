package ca.appdev.skinnydoo92.addressbook.adapters;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.appdev.skinnydoo92.addressbook.utils.LogWrapper;

import static ca.appdev.skinnydoo92.addressbook.data.AddressBookDatabaseDescription.Contact;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
	
	/**
	 * Interface implemented by ContactsFragment to respond
	 * 	when the user touches an item in the RecyclerView
	 */
	public interface ContactClickListener {
		void onContactClick(Uri contactUri);
	}
	
	/**
	 * Nested subclass of RecyclerView.ViewHolder used to implement
	 * the view-holder patter in the context of a RecyclerView.
	 * Here we provide references to the view of views used in the RecyclerView
	 */
	class ViewHolder extends RecyclerView.ViewHolder {
		
		private final TextView textView;
		private long contactID;
		
		public ViewHolder(View view) {
			super(view);
			
			textView = view.findViewById(android.R.id.text1);
			
			//Attach listener to textview
			textView.setOnClickListener( v -> {
				listener.onContactClick(Contact.buildContactUri(contactID));
			});
			
		}
		
		/**
		 * Set the database contact id for the contact in this viewHolder
		 * @param contactID the id of the contact
		 */
		public void setContactID(long contactID) {
			
			this.contactID = contactID;
		}
	}
	
	// ContactsAdapter instance variable
	private final ContactClickListener listener;
	private Cursor cursor = null;
	
	// Constructor
	public ContactsAdapter(ContactClickListener listener) {
		
		this.listener = listener;
	}
	
	/**
	 * Set Up new view and its ViewHolder
	 * @param parent
	 * @param viewType
	 * @return
	 */
	@NonNull
	@Override
	public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		
		// inflate the android.R.Layout.simple_list_item_1 layout
		LogWrapper.d("Setting up and inflating ContactsAdapter's TextViews");
		View view = LayoutInflater.from(parent.getContext()).inflate(
			android.R.layout.simple_list_item_1, parent, false );
		
		LogWrapper.d("exit");
		return new ViewHolder(view);
		
	}
	
	/**
	 * Configure and set values into the TextViews
	 * @param viewHolder
	 * @param position
	 */
	@Override
	public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder viewHolder, int position) {
		
		cursor.moveToPosition(position);
		
		// get the contact id
		long contactID = cursor.getLong(cursor.getColumnIndex(Contact.COLUMN_ID));
		LogWrapper.d("Contact ID = " + contactID);
		viewHolder.setContactID(contactID);
		
		// get the contact name
		String contactName = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME));
		LogWrapper.d("Contact Name = " + contactName);
		viewHolder.textView.setText(contactName);
		
		LogWrapper.d("exit");
	}
	
	@Override
	public int getItemCount() {
		return ( cursor != null ) ? cursor.getCount() : 0;
	}
	
	// Swap this adapter's current cursor for a new one
	public void swapCursor(Cursor cursor) {
		this.cursor = cursor;
		notifyDataSetChanged();
	}
}
