package com.hither.lianxiren.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.hither.lianxiren.model.ContactMember;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/6.
 */
public class GetPersion {
    static Cursor c;

    public static ArrayList<ContactMember> getContact(Context context) {
        ArrayList<ContactMember> listMembers = new ArrayList<ContactMember>();
        Cursor cursor = null;
        try {

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            // 这里是获取联系人表的电话里的信息  包括：名字，名字拼音，联系人id,电话号码；
            // 然后在根据"sort-key"排序
            cursor = context.getContentResolver().query(
                    uri,
                    new String[]{"display_name", "sort_key", "contact_id",
                            "data1"}, null, null, "sort_key");

            if (cursor.moveToFirst()) {
                do {
                    ContactMember contact = new ContactMember();
                    String contact_phone = cursor
                            .getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name = cursor.getString(0);
                    int contact_id = cursor
                            .getInt(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    contact.setContact_name(name);
                    contact.setContact_phone(contact_phone);
                    contact.setContact_id(contact_id);
                    if (name != null)
                        listMembers.add(contact);
                } while (cursor.moveToNext());
                c = cursor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context = null;
        }
        return listMembers;
    }
}
