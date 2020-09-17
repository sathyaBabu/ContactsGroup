package com.sathya.contactsgroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
/// https://developer.android.com/reference/android/provider/ContactsContract.RawContacts
public class MainActivity extends Activity {
    Context context;
    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    String[] permissons = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        if (CheckPermission(MainActivity.this, permissons[0])) {
            // you have permission go ahead
            actionButton();
        } else {
            // you do not have permission go request runtime permissions
            RequestPermission(MainActivity.this, permissons, REQUEST_RUNTIME_PERMISSION);
        }


    }
    void actionButton(){
        Button btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), createContact(), Toast.LENGTH_SHORT).show();

            }
        });
    }





    /////
    String createContact(){
        ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
        ContactOperation co=new ContactOperation(this);
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)

                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "YourGroupName")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "YourGroupName")

                .build()
        );
        co.addPhoto(ops);
        co.addContactToGroup(ops);
        //createNewGroup("GAMES");
        contactName("Sathya",ops);
        contactNumber("1234567890",ops);
        contactEmail("sathyahelp123@gmail.com",ops);
        contactOrganization("Edureka","SME",ops);
        contactAddress(" #1 , Commercial St, Bangalore 560 001",ops);

        return contactProvider(ops);
    }
    //### Contact provider to create a new contact
    String contactProvider(ArrayList<ContentProviderOperation> ops){
        String what;
        try{
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            what="Contact Created ";
        }
        catch (Exception e){
            e.printStackTrace();
            what="Unable to Create Contact ";
        }
        return what;
    }
    //### Contact Name
    void contactName(String name,ArrayList<ContentProviderOperation> ops){
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name).build()
        );
    }
    //### Contact Number
    void contactNumber(String no,ArrayList<ContentProviderOperation> ops){
        ops.add(ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) .build()
        );
    }


    void contactAddress(String address,ArrayList<ContentProviderOperation> ops) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, "Address : "+address)
                .build());
    }


    void contactEmail(String email,ArrayList<ContentProviderOperation> ops) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());
    }
    void contactOrganization(String company, String jobTitle,ArrayList<ContentProviderOperation> ops) {

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                .build());
    }
    public void createNewGroup(String name)
    {
        try
        {
            ContentValues groupValues = new ContentValues();
            groupValues.put(ContactsContract.Groups.TITLE, name);
            groupValues.put(ContactsContract.Groups.SHOULD_SYNC, true);
            groupValues.put(ContactsContract.Groups.GROUP_VISIBLE, 1);
            groupValues.putNull(ContactsContract.Groups.ACCOUNT_TYPE);
            groupValues.putNull(ContactsContract.Groups.ACCOUNT_NAME);
            getContentResolver().insert(ContactsContract.Groups.CONTENT_URI, groupValues);
            //return true;
        }
        catch (Exception e){}
    }
/////////




    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {

            case REQUEST_RUNTIME_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // you have permission go ahead
                    actionButton();
                } else {
                    // you do not have permission show toast.
                }
                return;
            }
        }
    }

    public void RequestPermission(Activity thisActivity, String[] Permission, int Code) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Permission[0])
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Permission[0])) {
            } else {
                ActivityCompat.requestPermissions(thisActivity, Permission,
                        Code);
            }
        }
    }

    public boolean CheckPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}



