package ca.appdev.skinnydoo92.addressbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import ca.appdev.skinnydoo92.addressbook.utils.LogWrapper;

import static ca.appdev.skinnydoo92.addressbook.data.AddressBookDatabaseDescription.*;

public class AddressBookDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "AddressBook.db";
	private static final int DATABASE_VERSION = 1;
	
	public AddressBookDatabaseHelper(@Nullable Context context) {
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/* Creates the contacts table when the database is created */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		LogWrapper.d("creating the contacts table...");
		
		// SQL for creating table
		final String CREATE_CONTACTS_TABLE =
				"CREATE TABLE " + Contact.TABLE_NAME + "(" +
						Contact.COLUMN_ID + " integer primary key, " +
						Contact.COLUMN_NAME + " TEXT, " +
						Contact.COLUMN_PHONE + " TEXT, " +
						Contact.COLUMN_EMAIL + " TEXT, " +
						Contact.COLUMN_STREET + " TEXT, " +
						Contact.COLUMN_CITY + " TEXT, " +
						Contact.COLUMN_STATE + " TEXT, " +
						Contact.COLUMN_ZIP + " TEXT);";
		
		LogWrapper.d("Create table string = " + CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE); // create the contact table
		LogWrapper.d("contact table created");
		LogWrapper.d("exit");
	}
	
	/* Defines how to upgrade the database when the schema changes */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	}
}
