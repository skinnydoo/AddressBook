package ca.appdev.skinnydoo92.addressbook.enums;

public enum ErrorMessages {
	
	ACTIVITY_NOT_NULL("Activity must not be null..."),
	LISTENER_NOT_NULL("Listener must not be null..."),
	FRAGMENT_MANAGER_NOT_NULL("Fragment manager must not be null..."),
	TEXT_INPUT_LAYOUT_NO_EDITTEXT("No EditText inside TextInputLayout..."),
	SORRY("Sorry"),
	AN_ERROR_OCCURRED("There was an error...");
	
	private final String name;
	
	ErrorMessages(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
