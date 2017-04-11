package nl.tue.tuego.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nl.tue.tuego.Activities.InboxItemActivity;
import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.Holders.ViewHolder;
import nl.tue.tuego.R;

public class InboxAdapter extends ArrayAdapter<ImageModel> {

    private LayoutInflater li;
    private List<ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                Date currentTime = new Date();
                for (ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public InboxAdapter(Context context, List<ImageModel> items) {
        super(context, 0, items);
        li = LayoutInflater.from(context);
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }

    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final ImageModel item = getItem(position);
        LinearLayout LLInboxItem = null;
        ViewHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            holder = new ViewHolder(getContext());
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inbox_item, parent, false);
            holder.TVAuthor = (TextView) convertView.findViewById(R.id.feedItemAuthor);
            holder.TVTimeRemaining = (TextView) convertView.findViewById(R.id.feedItemTimeRemaining);
            holder.TVTimeTaken = (TextView) convertView.findViewById(R.id.feedItemTimeTaken);
            holder.TVPoints = (TextView) convertView.findViewById(R.id.feedItemPoints);
            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }

            // Set event listeners
            LLInboxItem = (LinearLayout) convertView.findViewById(R.id.feedItemLayout);
            LLInboxItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Inbox item", "Clicked!");
                    Intent intent = new Intent(getContext(), InboxItemActivity.class);
                    intent.putExtra("UUID", item.UUID);
                    intent.putExtra("Uploader", item.Uploader);
                    intent.putExtra("UploadTime", item.UploadTime);
                    intent.putExtra("Finder", item.Finder);
                    getContext().startActivity(intent);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setData(item);

        // Return the completed view to render on screen
        return convertView;
    }

}