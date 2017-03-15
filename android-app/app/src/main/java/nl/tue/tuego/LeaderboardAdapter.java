package nl.tue.tuego;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class LeaderboardAdapter extends ArrayAdapter<UserModel> {

    public LeaderboardAdapter(Context context, UserModel[] items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserModel item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inbox_item, parent, false);
        }

        // Lookup view for data population
        // TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        // TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        // tvName.setText(user.name);
        // tvHome.setText(user.hometown);
        // Return the completed view to render on screen
        return convertView;

    }
}