package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 17/12/2014.
 */
public class MobileContactAdapter extends CursorAdapter {

    private User currentUser;
    private HashMap<String, String> contactList;
    private AddContactListener addContactListener;

    public MobileContactAdapter(Context context, User currentUser,
                                HashMap<String, String> contactList,
                                AddContactListener addContactListener) {
        super(context, null, 0);
        this.currentUser = currentUser;
        this.contactList = contactList;
        this.addContactListener = addContactListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.user_contact_listitem_inner, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        if (cursor != null) {
            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
            screenNameText.setText(contactName);

            String thumbnailUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
            AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
            Bitmap bitmap = ImageUtils.getContactBitmapFromURI(context, thumbnailUri);
            if (bitmap != null) {
                avatarImage.setImageBitmap(bitmap);
            } else {
                avatarImage.setImageResource(R.drawable.ic_avatar);
            }

            final String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            TextView inviteButton = (TextView) view.findViewById(R.id.text_invite);
            Button addButton = (Button) view.findViewById(R.id.b_add_contact);
            TextView addedText = (TextView) view.findViewById(R.id.text_added);
            inviteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            addedText.setVisibility(View.GONE);
            final String userId = contactList.get(number.replace(" ", ""));
            if (!currentUser.getObjectId().equals(userId)) {
                if (number != null && userId != null) {
                    if (currentUser.isContact(userId)) {
                        addedText.setVisibility(View.VISIBLE);
                    } else {
                        addButton.setVisibility(View.VISIBLE);
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addContactListener.onAddContact(userId);
                            }
                        });
                    }
                } else {
                    inviteButton.setVisibility(View.VISIBLE);
                    inviteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Animation.animateTextView(view);
                            startSmsInvitationActivity(context, number);
                        }
                    });
                }
            }
        }
    }

    private void startSmsInvitationActivity(Context context, String mobile) {
        Uri uri = Uri.parse("smsto:" + mobile);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", context.getString(R.string.info_invitation));
        context.startActivity(intent);
    }
}
