package com.example.uidesignlab;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private static final String TAG = "RoomLab";
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        RecyclerView recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);


        Button btnAddJohn = findViewById(R.id.btnAddJohn);
        Button btnClearAll = findViewById(R.id.btnClearAll);


        btnAddJohn.setOnClickListener(v -> {
            AppDatabase.databaseExecutor.execute(() -> {
                AppDatabase.getInstance(getApplicationContext())
                        .userDao()
                        .insert(new UserRecord("John"));
                Log.d(TAG, "Added John to database");


                loadUsers();
            });
        });

        btnClearAll.setOnClickListener(v -> {
            AppDatabase.databaseExecutor.execute(() -> {
                int deletedCount = AppDatabase.getInstance(getApplicationContext())
                        .userDao()
                        .deleteAll();
                Log.d(TAG, "Cleared all users, deleted: " + deletedCount);


                loadUsers();
            });
        });
        Button btnReadProvider = findViewById(R.id.btnReadProvider);

        btnReadProvider.setOnClickListener(v -> {
            AppDatabase.databaseExecutor.execute(() -> {
                int count = 0;

                 try (Cursor cursor = getContentResolver().query(UserProvider.CONTENT_URI, null, null, null, null)) {
                    if (cursor != null) {
                        int nameIndex = cursor.getColumnIndexOrThrow("name");

                        while (cursor.moveToNext()) {
                            String name = cursor.getString(nameIndex);
                            Log.d("RoomLab", "Provider row name: " + name);
                            count++;
                        }
                    }
                } catch (Exception e) {
                    Log.e("RoomLab", "Error querying provider", e);
                }

                final int finalCount = count;

                runOnUiThread(() -> {
                    Toast.makeText(UsersActivity.this, "Provider returned " + finalCount + " rows", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<UserRecord> users = AppDatabase.getInstance(getApplicationContext())
                    .userDao()
                    .getAll();

            Log.d(TAG, "Loaded " + users.size() + " users from database.");

            runOnUiThread(() -> {
                adapter.setUsers(users);
            });
        });
    }
}