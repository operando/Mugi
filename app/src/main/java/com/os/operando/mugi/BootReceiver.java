package com.os.operando.mugi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import retrofit2.Call;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session == null) {
            Toast.makeText(context, "Twitter認証してねー", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int bootCount = Settings.Global.getInt(context.getContentResolver(), Settings.Global.BOOT_COUNT);
            Log.d(Tags.App, bootCount + "");
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
            StatusesService statusesService = twitterApiClient.getStatusesService();

            String deviceName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
                    ? Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME)
                    : Build.DEVICE;
            Call<Tweet> tweetCall = statusesService.update(session.getUserName() + "の" + deviceName + "が" + bootCount + "回目の起動に成功しましたーやったねー", null, false, null, null, null, false, null, null);
            tweetCall.enqueue(new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    Log.d(Tags.App, result.data.id + "");
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d(Tags.App, "Tweet failure", exception);
                }
            });
        } catch (Settings.SettingNotFoundException e) {
        }
    }
}