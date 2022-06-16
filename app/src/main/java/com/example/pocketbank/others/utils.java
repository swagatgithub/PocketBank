package com.example.pocketbank.others;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pocketbank.R;
import com.example.pocketbank.model.user;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class utils
{
    private final Context context;
    private final Gson gson;
    public utils(Context context)
    {
        this.context = context;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void addUserToSharedPreferences(user user)
    {
        SharedPreferences userData = context.getSharedPreferences(context.getString(R.string.loggedInUser),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putString(context.getString(R.string.userCredential),gson.toJson(user));
        editor.apply();
    }

    public user getUserFromSharedPreference()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.loggedInUser), Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(context.getString(R.string.userCredential), null);
        if (value != null)
        {
            Type type = new TypeToken<user>() {
            }.getType();
            return gson.fromJson(value, type);
        }
        else
            return null;
    }

    public void signOutUser()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.loggedInUser), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.userCredential) ,null);
        editor.apply();
    }

}
