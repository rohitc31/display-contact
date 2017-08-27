package rohitc.contact_display;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {

    // File url to download
    private static String file_url = "http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt";
    private static String file_path = "/sdcard/Android/data/AccData/cont.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                Log.println(Log.INFO, "URL", "Opening URL Connection");
                URLConnection connection = url.openConnection();
                connection.connect();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(file_path);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {

                    // writing data to file
                    output.write(data, 0, count);
                }

                Log.println(Log.INFO, "URL", "Download Done!! Closing Connection");

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
        }
    }

    public void Download(View v) {
        new DownloadFileFromURL().execute(file_url);
    }

    public void AddContacts(View v) {

        String fileData = ReadFile(file_path);

        String[] name = new String[5];
        String[] email = new String[5];
        String[] mobNum = new String[5];
        String[] homeNum = new String[5];

        int idx = 0;
        for (int m = 0; m < 5; m++)
        {
            name[m] = "";
            email[m] = "";
            mobNum[m] = "";
            homeNum[m] = "";

            while(fileData.charAt(idx) != ' ')
            {
                name[m] += Character.toString(fileData.charAt(idx));
                idx++;
            }
            idx++;
            while(fileData.charAt(idx) != ' ')
            {
                email[m] += Character.toString(fileData.charAt(idx));
                idx++;
            }
            idx++;
            while(fileData.charAt(idx) != ' ')
            {
                mobNum[m] += Character.toString(fileData.charAt(idx));
                idx++;
            }
            idx++;
            while(fileData.charAt(idx) != '\n')
            {
                homeNum[m] += Character.toString(fileData.charAt(idx));
                idx++;
            }
            idx++;
        }

        for (int n = 0; n < 5; n++) {
            String DisplayName = name[n];
            String MobileNumber = mobNum[n];
            String HomeNumber = homeNum[n];
            String emailID = email[n];

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI).
                    withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).
                    withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).
                    build());

            //------------------------------------------------------ Names
            if (DisplayName != null) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                DisplayName).build());
            }

            //------------------------------------------------------ Mobile Number
            if (MobileNumber != null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }

            //------------------------------------------------------ Home Numbers
            if (HomeNumber != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .build());
            }


            //------------------------------------------------------ Email
            if (emailID != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }

            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error:", e.getMessage());
            }
        }
    }

    public String ReadFile (String filepath)
    {
        BufferedReader mBufferedReader;
        String read_str = "";

        mBufferedReader = null;
        String tmp = null;

        try
        {
            mBufferedReader = new BufferedReader(new FileReader(filepath));
            // Read string
            while ((tmp = mBufferedReader.readLine()) != null)
            {
                tmp += "\n";
                read_str += tmp;
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return read_str;
    }


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID

    String[] contactNames = new String[5];
    String[] latitudes    = new String[5];
    String[] longitudes   = new String[5];

    int m = -1;

    public void showOnMap(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("com.contactdownload.Names", contactNames);
        intent.putExtra("com.contactdownload.Latitudes", latitudes);
        intent.putExtra("com.contactdownload.Longitudes", longitudes);
        startActivity(intent);
    }


    public void selectContact(View btnSelectContact) {

        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            m++;
            retrieveContactName();
            retrieveContactNumber();
            retrieveHomeNumber();

            Log.d(TAG, "m :" + m);

        }
    }

    private void retrieveContactNumber() {

        latitudes[m] = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            latitudes[m] = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + latitudes[m]);
    }

    private void retrieveContactName() {

        contactNames[m] = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactNames[m] = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactNames[m]);

    }

    private void retrieveHomeNumber() {

        longitudes[m] = null;

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            longitudes[m] = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Home Number: " + longitudes[m]);
    }
}
