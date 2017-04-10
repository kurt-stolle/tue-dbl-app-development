package nl.tue.tuego.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import nl.tue.tuego.Activities.InboxActivity;
import nl.tue.tuego.Activities.InboxItemActivity;
import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.R;

public class InboxAdapter extends ArrayAdapter<ImageModel> {

    public InboxAdapter(Context context, List<ImageModel> items) {
        super(context, 0, items);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final ImageModel item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inbox_item, parent, false);
        }

        // Lookup view for data population
        LinearLayout LLInboxItem = (LinearLayout) convertView.findViewById(R.id.feedItemLayout);
        TextView TVAuthor = (TextView) convertView.findViewById(R.id.feedItemAuthor);
        TextView TVTimeRemaining = (TextView) convertView.findViewById(R.id.feedItemTimeRemaining);
        TextView TVTimeTaken = (TextView) convertView.findViewById(R.id.feedItemTimeTaken);
        TextView TVPoints = (TextView) convertView.findViewById(R.id.feedItemPoints);

        // Populate the data into the template view using the data object
        TVAuthor.setText(String.format("Author: %s", item.Uploader));
        TVTimeRemaining.setText(String.format("%s time left", item.UploadTime));
        TVTimeTaken.setText(String.format("taken at %s", item.UploadTime));
        TVPoints.setText(R.string.tenPoints);

        // Set event listeners
        LLInboxItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Itemclick", "Clicked!");
                Intent intent = new Intent(getContext(), InboxItemActivity.class);
                intent.putExtra("UUID", item.UUID);
                intent.putExtra("Uploader", item.Uploader);
                intent.putExtra("UploadTime", item.UploadTime);
                intent.putExtra("Finder", item.Finder);
                getContext().startActivity(intent);
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
}