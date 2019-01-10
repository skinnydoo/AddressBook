package ca.appdev.skinnydoo92.addressbook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Objects;

import ca.appdev.skinnydoo92.addressbook.R;
import ca.appdev.skinnydoo92.addressbook.utils.LogWrapper;

import static ca.appdev.skinnydoo92.addressbook.data.AddressBookDatabaseDescription.AUTHORITY;
import static ca.appdev.skinnydoo92.addressbook.data.AddressBookDatabaseDescription.Contact;

public class AddressBookProvider extends ContentProvider {
	
	private AddressBookDatabaseHelper dbHelper; // to access the database
	
	// UriMatcher helps ContentProvider determine operation to perform
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	// constants used with UriMatcher to determine operation to perform
	private static final int ONE_CONTACT = 1;   // manipulate one contact
	private static final int CONTACTS = 2;      // manipulate the contacts table
	
	private static final String ONE_CONTACT_MIME_TYPE =
			"vnd.android.cursor.item/vnd.skinnydoo92.addressbook.data.contacts";
	private static final String CONTACTS_MIME_TYPE =
			"vnd.android.cursor.dir/vnd.skinnydoo92.addressbook.data.contacts";
	
	/* static block to configure this ContentProvider's UriMatcher */
	static {
		// Uri for Contact with the specified id (#)
		uriMatcher.addURI(AUTHORITY, Contact.TABLE_NAME + "/#", ONE_CONTACT);
		
		// Uri for Contacts table
		uriMatcher.addURI(AUTHORITY, Contact.TABLE_NAME, CONTACTS);
	}
	
	public AddressBookProvider() {
	
	}
	
	/* Called when the ContentProvider is created */
	@Override
	public boolean onCreate() {
		LogWrapper.d("Initializing the content provider...");
		LogWrapper.d("Creating the AddressBookDatabaseHelper");
		
		dbHelper = new AddressBookDatabaseHelper(getContext());
		
		LogWrapper.d("AddressBookContentProvider successfully created");
		LogWrapper.d("exit");
		return true;
	}
	
	/**
	 * Insert a new contact into the database
	 * @param uri A Uri representing the data resources
	 * @param values data to insert into the db
	 * @return A Uri of the location of the resource created
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		Uri newContactUri;
		
		switch (uriMatcher.match(uri)) {
			
			case CONTACTS:
				// insert a new contacts - success yields a new contacts's row id
				long rowID = dbHelper.getWritableDatabase().insert(Contact.TABLE_NAME, null,
						values);
				
				// if the contact was inserted, created an appropriate Uri;
				// otherwise, throw an exception
				if(rowID > 0) {
					newContactUri = Contact.buildContactUri(rowID);
					
					// notify observers that the database changed
					Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
				} else {
					throw new SQLException(
							Objects.requireNonNull(getContext(), "Context cannot be null").getString(R
							.string.insert_failed) + uri);
				}
				break;
				
				default: throw new UnsupportedOperationException(Objects.requireNonNull
						(getContext()).getString(R.string.invalid_insert_uri) + uri);
		}
		
		return newContactUri;
	}
	
	/** retrieve data from the provider's data source ( the database )
     uri : A Uri representing the data to retrieve
     projection : String array representing the columns to retrieve, If this arg is null
                 all columns will be included in the result
     selection : A string containing the selection criteria. This is the SQL WHERE clause.
                 if this arg is null, all rows will be included in the result
     selectionArgs : A String array containing the argument used to replace any args placeholders
      (?) in the selection string
      sortOrder : a String representing the sort order. This is the SQL ORDER BY clause.
     */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		// Create the SQLiteQueryBuilder for querying contacts table
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(Contact.TABLE_NAME);
		
		switch (uriMatcher.match(uri)) {
			
			case ONE_CONTACT: // contact with specified id will be selected
				queryBuilder.appendWhere(Contact.COLUMN_ID + "=" + uri.getLastPathSegment());
				break;
			
			case CONTACTS: // all contacts will be selected
				break;
				
			default:
				throw new UnsupportedOperationException(
						Objects.requireNonNull(getContext())
								.getString(R.string.invalid_insert_uri) + uri);
		}
		
		// Execute the query to select one or all contacts
		Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		
		// Configure cursor to watch for content changes
		cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
		return cursor;
	}
	
	/**
	 * Update an existing contact in the database
	 * @param uri
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int numOfRowsUpdated; // 1 if update successful; 0 otherwise
		
		switch (uriMatcher.match(uri)) {
			
			case ONE_CONTACT:
				// get from the uri the id of contact to update
				String id = uri.getLastPathSegment();
				
				// Limiting update to one row at most
				numOfRowsUpdated = dbHelper.getWritableDatabase().update(
						Contact.TABLE_NAME, values, Contact.COLUMN_ID + "=" + id, selectionArgs);
				break;
				
			default:
				throw new UnsupportedOperationException(
						Objects.requireNonNull(getContext()).getString(R.string
								.invalid_update_uri) + uri);
		}
		
		// If changes were made, notify observers that the database changed
		if(numOfRowsUpdated != 0){
			Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
		}
		
		return numOfRowsUpdated;
	}
	
	/**
	 * Delete an existing contact from the database
	 * @param uri
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		int numOfRowsDeleted;
		
		switch (uriMatcher.match(uri)) {
			
			case ONE_CONTACT :
				// get the uri from the id of contact to delete
				String id = uri.getLastPathSegment();
				
				// Delete the contact. Limiting deletion to one row at most
				numOfRowsDeleted = dbHelper.getWritableDatabase().delete(
						Contact.TABLE_NAME, Contact.COLUMN_ID + "=" + id, selectionArgs);
				break;
				
			default:
				throw new UnsupportedOperationException(
						Objects.requireNonNull(getContext())
								.getString(R.string.invalid_delete_uri) + uri);
		}
		
		// notify observers that the database change
		if(numOfRowsDeleted != 0)
			Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
		
		return numOfRowsDeleted;
	}
	
	/**
	 * Returns a String that identifies the MIME type for the content provider
	 * @param uri A Uri representing the data to retrieve
	 * @return MIME type for the uri
	 */
	@Override
	public String getType(@NonNull Uri uri) {
		
		switch (uriMatcher.match(uri)) {
			
			case ONE_CONTACT : return ONE_CONTACT_MIME_TYPE;
			
			case CONTACTS: return CONTACTS_MIME_TYPE;
			
			default:
				LogWrapper.d("Unsupported Uri. We shouldn't get here...");
				return null;
		}
	}
}
