package lk.sheha.agriconnect.model;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import lk.sheha.agriconnect.R;

public class SuccessDialog extends Dialog {


    public SuccessDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_success);

        // Initialize views
        ImageView successIcon = findViewById(R.id.successIcon);
        Button okButton = findViewById(R.id.okButton);

        // Load and start the animation
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.success_animation);
        successIcon.startAnimation(animation);

        // Set click listener for the OK button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}