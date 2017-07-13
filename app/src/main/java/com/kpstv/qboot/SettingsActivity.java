package com.kpstv.qboot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kp on 8/7/17.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName("settings");
            addPreferencesFromResource(R.xml.settings);
            CheckBoxPreference ShowDown = (CheckBoxPreference) findPreference("show_down");
            final CheckBoxPreference Shellex = (CheckBoxPreference) findPreference("shell_ex");
            Shellex.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                //   Toast.makeText(getActivity(), "Will Do", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            ShowDown.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.drawable.ic_action_name);
                    builder.setMessage(Html.fromHtml("Please Restart the App in Order to take Effect"))
                            .setTitle( Html.fromHtml("<font color='#000000'>Device Info</font>"));

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                        }
                    });
                    builder.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = getActivity().getPackageManager()
                                    .getLaunchIntentForPackage( getActivity().getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    //show the dialog
                    AlertDialog about = builder.create();
                    about.show();
                    return true;
                }
            });
            Preference Appdet = findPreference("app_det");
            Preference VisitWeb = findPreference("web_id");
            Preference Changelog = findPreference("log");
            Preference Email = findPreference("email");
            Preference Ops = findPreference("ops");
            Ops.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KaustubhPatange/Qboot"));
                    startActivity(browserIntent);
                    return true;
                }
            });
            Email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String[] TO = {"developerkp16@gmail.com"};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send email to Kaustubh Patange"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            Changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.drawable.ic_action_name);
                    builder.setMessage(Html.fromHtml("<b>Version 1.0 (13-7-17)</b><br>  -Initial Release<br>  -Public Commit on Github<br><br><font color='#008080'>Notice:</font><br><br>I am not Responsible for Anything happens to your device. This app is intended to be Free and Without Warranty. If you want to Contact send an Email through App<br><br><font color='#8B0000'>Root Privileges Required</font><br><br><font color='#008000'>Basic:</font><br><br>How to : <i>Just Click on any Reboot Option Accept the Dialog Confirmation</i><br><br>Settings : <i>In Settings you can Set some Functions as per your need.<br>1. Checking 'Do Not Show Prompt' will disable the prompt ask for every reboot task.<br>2. Checking 'Download mode' will basically Enable Download Mode Option in Menu needed for Samsung devices for Flashing and Stuffs.<br>3. Unchecking 'Execute Shell Execute' will disable the safety feature of build prop ids running at startup, this may decrease app launch activity.</i><br><br>Version 1.0"))
                            .setTitle(Html.fromHtml("<font color='#000000'>Notice</font>"))
                            .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                    return true;
                }
            });
            VisitWeb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developerkp.capstricks.net/"));
                    startActivity(browserIntent);
                    return true;
                }
            });
            Appdet.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    try{
                        Intent o = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        o.setData(Uri.parse("package:" + "com.kpstv.qboot"));
                        startActivity(o);
                    } catch (ActivityNotFoundException e){
                        Intent o = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(o);
                    }
                    return true;
                }
            });

        }
    }
}