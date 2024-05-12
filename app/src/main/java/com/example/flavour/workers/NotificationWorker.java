package com.example.flavour.workers;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.Manifest;
import android.util.Log;

import com.example.flavour.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationWorker extends Worker {
    FirebaseDatabase db;
    DatabaseReference recipes;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = FirebaseDatabase.getInstance();
        recipes = db.getReference("recipes");
    }

    @NonNull
    @Override
    public Result doWork() {
        checkRecipes();
        return Result.success();
    }

    public void checkRecipes() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("last_id", Context.MODE_PRIVATE);
        Long last_id = sharedPreferences.getLong("last_id", 0);
        recipes.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                String id = data.child(String.valueOf(data.getChildrenCount() - 1)).getKey();
                if (id == null) return;
                if (!id.equals(String.valueOf(last_id))) {
                    createNotification("Уведомление", "Добавлен новый рецепт");
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
                    editor.putLong("last_id", Long.parseLong(id)).apply();
                }
            }
        });
    }

    public void createNotification(String title, String text) {
        NotificationChannel channel = new NotificationChannel("1", "name", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("new posts");
        NotificationManager notificationManagerService = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManagerService.createNotificationChannel(channel);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.pie)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }

}
