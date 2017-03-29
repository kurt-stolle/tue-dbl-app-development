package nl.tue.tuego.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.R;

public class InboxAdapter extends ArrayAdapter<ImageModel> {

    public InboxAdapter(Context context, List<ImageModel> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ImageModel item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inbox_item, parent, false);
        }

        // Lookup view for data population
        ImageView IVImage = (ImageView) convertView.findViewById(R.id.itemImage);
        TextView TVAuthor = (TextView) convertView.findViewById(R.id.itemAuthor);
        TextView TVTimeRemaining = (TextView) convertView.findViewById(R.id.itemTimeRemaining);
        TextView TVTimeTaken = (TextView) convertView.findViewById(R.id.itemTimeTaken);
        TextView TVPoints = (TextView) convertView.findViewById(R.id.itemPoints);

        // Populate the data into the template view using the data object
        TVAuthor.setText(item.Uploader);
        TVTimeRemaining.setText(item.UploadTime + " time left");
        TVTimeTaken.setText("taken at " + item.UploadTime);
        TVPoints.setText("10 points");

        // Return the completed view to render on screen
        return convertView;
    }
}