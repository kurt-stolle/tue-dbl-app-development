package nl.tue.tuego.Holders;

import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.R;

import static nl.tue.tuego.Activities.InboxItemActivity.GUESS_TIME;

public class ViewHolder {
    public TextView TVAuthor;
    public TextView TVTimeRemaining;
    public TextView TVTimeTaken;
    public TextView TVPoints;
    ImageModel mImageModel;

    public void setData(ImageModel item) {
        mImageModel = item;
        // Populate the data into the template view using the data object
        TVAuthor.setText(item.Uploader);
        TVTimeRemaining.setText(item.UploadTime);
        TVTimeTaken.setText(item.UploadTime);
        TVPoints.setText(R.string.itemPointsValue);
        updateTimeRemaining(new Date());
    }

    public void updateTimeRemaining(Date currentDate) {
        // Setting text of time remaining
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        Date uploadDate = null;
        try {
            uploadDate = format.parse(mImageModel.UploadTime);
        } catch (ParseException e) {
            Log.e("ViewHolder", "Date cannot be parsed");
            e.printStackTrace();
        }

        long diffDate = uploadDate.getTime() + TimeUnit.DAYS.toMillis(GUESS_TIME) - currentDate.getTime();
        final long diffSeconds = diffDate / 1000 % 60;
        final long diffMinutes = diffDate / (60 * 1000) % 60;
        final long diffHours = diffDate / (60 * 60 * 1000) % 24;
        final long diffDays = diffDate / (1000 * 60 * 60 * 24);
        TVTimeRemaining.setText(String.format(Locale.ENGLISH, "Time left: %1$d:%2$02d:%3$02d:%4$02d", diffDays, diffHours, diffMinutes, diffSeconds));
    }
}