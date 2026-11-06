package com.example.uidesignlab;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserRecord")
public class UserRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "created_at")
    public long createdAt;

    // Room needs this exact constructor
    public UserRecord(@NonNull String name) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
    }
}