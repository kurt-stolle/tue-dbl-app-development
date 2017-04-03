package nl.tue.tuego.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.R;

public class InboxAdapter extends ArrayAdapter<ImageModel> {

    public InboxAdapter(Context context, List<ImageModel> items) {
        super(context, 0, items);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ImageModel item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inbox_item, parent, false);
        }

        // Lookup view for data population
        TextView TVAuthor = (TextView) convertView.findViewById(R.id.itemAuthor);
        TextView TVTimeRemaining = (TextView) convertView.findViewById(R.id.itemTimeRemaining);
        TextView TVTimeTaken = (TextView) convertView.findViewById(R.id.itemTimeTaken);
        TextView TVPoints = (TextView) convertView.findViewById(R.id.itemPoints);

        // Populate the data into the template view using the data object
        TVAuthor.setText(String.format("Author: %s", item.getUploader()));
        TVTimeRemaining.setText(String.format("%s time left", item.getUploadTime()));
        TVTimeTaken.setText(String.format("taken at %s", item.getUploadTime()));
        TVPoints.setText(R.string.tenPoints);

        // Return the completed view to render on screen
        return convertView;
    }
}