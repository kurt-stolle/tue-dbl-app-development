package nl.tue.tuego.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.tue.tuego.Models.LeaderboardEntry;
import nl.tue.tuego.Models.UserModel;
import nl.tue.tuego.R;

public class LeaderboardAdapter extends ArrayAdapter<LeaderboardEntry> {

    public LeaderboardAdapter(Context context, List<LeaderboardEntry> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LeaderboardEntry item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item, parent, false);
        }

        // Lookup view for data population
        TextView TVRank = (TextView) convertView.findViewById(R.id.itemRank);
        TextView TVName = (TextView) convertView.findViewById(R.id.itemName);
        TextView TVPoints = (TextView) convertView.findViewById(R.id.LBitemPoints);

        // Populate the data into the template view using the data object
        TVRank.setText(item.position + "");
        TVName.setText(item.Name);
        TVPoints.setText(item.Points + " studypoints");

        // Return the completed view to render on screen
        return convertView;

    }
}