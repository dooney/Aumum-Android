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
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;

/**
 * Created by Administrator on 17/12/2014.
 */
public class MobileContactAdapter extends CursorAdapter {

    public MobileContactAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.mobile_contact_listitem_inner, null);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        if (cursor != null) {
            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            TextView contactNameText = (TextView) view.findViewById(R.id.text_contact_name);
            contactNameText.setText(contactName);

            String thumbnailUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
            AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
            Bitmap bitmap = ImageUtils.getContactBitmapFromURI(context, thumbnailUri);
            if (bitmap != null) {
                avatarImage.setImageBitmap(bitmap);
            } else {
                ImageLoaderUtils.displayImage(R.drawable.ic_avatar, avatarImage);
            }

            final String mobile = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            TextView inviteButton = (TextView) view.findViewById(R.id.text_invite);
            inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation.animateTextView(view);
                    startSmsInvitationActivity(context, mobile);
                }
            });
        }
    }

    private void startSmsInvitationActivity(Context context, String mobile) {
        Uri uri = Uri.parse("smsto:" + mobile);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", context.getString(R.string.info_invitation));
        context.startActivity(intent);
    }
}
