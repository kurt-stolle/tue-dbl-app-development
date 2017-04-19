package nl.tue.tuego.Holders;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.Models.ManifestEntry;
import nl.tue.tuego.R;

import static nl.tue.tuego.Activities.InboxItemActivity.GUESS_TIME;

public class ViewHolder {
    public TextView TVAuthor;
    public TextView TVTimeRemaining;
    public TextView TVTimeTaken;
    public TextView TVPoints;
    public ImageView IVImage;
    private ManifestEntry entry;
    private final Context context;

    public ViewHolder (Context context) {
        this.context = context;
    }

    public void setData(ManifestEntry item) {
        Log.d("ViewHolder", "Setting data");
        entry = item;
        // Populate the data into the template view using the data object
        TVAuthor.setText(item.UploaderName);
        TVTimeTaken.setText(item.Image.UploadTime);
        IVImage.setImageBitmap(item.Image.Image);
        //updateTimeRemaining(new Date());
    }

    /*public void updateTimeRemaining(Date currentDate) {
        // Setting text of time remaining
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        Date uploadDate = null;
        try {
            uploadDate = format.parse(entry.Image.UploadTime);
        } catch (ParseException e) {
            Log.e("ViewHolder", "Date cannot be parsed");
            e.printStackTrace();
        }

        final long diffDate = uploadDate.getTime() + TimeUnit.DAYS.toMillis(GUESS_TIME) - currentDate.getTime();
        final long diffSeconds = diffDate / 1000 % 60;
        final long diffMinutes = diffDate / (60 * 1000) % 60;
        final long diffHours = diffDate / (60 * 60 * 1000) % 24;
        final long diffDays = diffDate / (1000 * 60 * 60 * 24);
        Resources res = context.getResources();
        if (diffDate < 0) {
            TVTimeRemaining.setText(res.getString(R.string.dataTimeRemainingExpired));
        } else {
            TVTimeRemaining.setText(res.getString(R.string.dataTimeRemaining, diffDays, diffHours, diffMinutes, diffSeconds));
        }
    }*/
}