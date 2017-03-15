package nl.tue.tuego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class ChangePasswordActivity extends Activity {
    EditText ETPassword, ETConfirmPassword;
    Button BNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // look up all needed views
        ETPassword = (EditText) findViewById(R.id.editTextChangePasswordPassword);
        ETConfirmPassword = (EditText) findViewById(R.id.editTextChangePasswordVerifyPassword);
        BNext = (Button) findViewById(R.id.buttonForgotPasswordNext);

        // set event listeners
        BNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });
    }

    // method which is called when BNext is pressed
    public void changePassword(View view) {
        // TODO: write code to check password and change password
        showPopup(ChangePasswordActivity.this);
    }

    // the method that displays the popup.
    // TODO: popup is ugly, should be replaced
    private void showPopup(final Activity context) {
        Point p = new Point();

        // Inflate the activity_popup_password_changed.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popupLayout);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.activity_popup_password_changed, viewGroup);

        // getting screen size in px
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // changing the popup size in dp to px (is hard-coded, should not be hard-coded)
        int popupWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int popupHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());

        // calculating the location where the popup should be placed
        p.x = size.x/2 - popupWidth/2;
        p.y = size.y/2 - popupHeight/2;

        // creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // displaying the popup at the specified location
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

        // getting a reference to Close button, and close the popup when clicked.
        ImageButton back = (ImageButton) layout.findViewById(R.id.imageButtonClose);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
