package com.example.padelwear;

import android.os.Bundle;
import android.support.wear.widget.SwipeDismissFrameLayout;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class SwipeDismiss extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_dismiss);

        SwipeDismissFrameLayout root = (SwipeDismissFrameLayout) findViewById(R.id.swipe_dismiss_root);
        root.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override public void onDismissed(SwipeDismissFrameLayout layout) {
                SwipeDismiss.this.finish();
                }
            }
        );

        // Enables Always-on
        setAmbientEnabled();
    }
}
