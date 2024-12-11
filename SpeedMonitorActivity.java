package com.android.interviwe;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SpeedMonitorActivity extends AppCompatActivity {

    private static final String TAG = "SpeedMonitor";
    private DatabaseReference databaseReference;
    private String customerId = "Sairamkrishna_Mammahe"; // Example customer ID or we can take an integer value from api or user profile service 
    private double currentSpeed = 0.0; // Example speed, to be updated dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_monitor);

        // Initialize Firebase Database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("car_rentals");

        // Monitor speed in real-time for the given customer
        monitorSpeedLimit(customerId);
    }

    private void monitorSpeedLimit(String customerId) {
        // Get the speed limit for the customer
        databaseReference.child(customerId).child("speed_limit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double speedLimit = dataSnapshot.getValue(Double.class);
                    if (speedLimit != null) {
                        Log.d(TAG, "Speed limit for customer " + customerId + ": " + speedLimit);
                        checkSpeedLimit(speedLimit);
                    }
                } else {
                    Log.w(TAG, "Speed limit not set for customer: " + customerId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read speed limit", databaseError.toException());
            }
        });
    }

    private void checkSpeedLimit(double speedLimit) {
        // Simulate real-time speed updates (in a real-world scenario, this would come from GPS or sensors)
        currentSpeed = getCurrentSpeed();

        if (currentSpeed > speedLimit) {
            sendNotificationToCompany(customerId, currentSpeed);
            alertUser();
        }
    }

    private double getCurrentSpeed() {
        // Mock method to return current speed (replace with actual speed fetching logic)
        return 40; // Example: we can take this value from VHAL property in Car service
}

    private void sendNotificationToCompany(String customerId, double speed) {
        // Notify the rental company about the speed violation
        DatabaseReference notificationRef = databaseReference.child(customerId).child("notifications");
        notificationRef.push().setValue("Speed violation: " + speed + " km/h");

        Log.d(TAG, "Notification sent to company: Speed violation: " + speed + " km/h");
    }

    private void alertUser() {
        // Alert the user (could be a UI alert, sound, or vibration)
        Toast.makeText(this, "Warning: Speed limit exceeded!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "User alerted about speed limit exceedance.");
    }
}
