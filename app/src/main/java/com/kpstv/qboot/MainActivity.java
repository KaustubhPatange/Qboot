package com.kpstv.qboot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.provider.MediaStore;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String Device_info,getvar,getdes;
    InterstitialAd mInterstitialAd;
    int c=0;
    public static int root=0;
    private static final String PRIVATE_PREF = "myapp";
    private static final String VERSION_KEY = "version_number";
    ListView list;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(myIntent);

            }
        }); */
       SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
       boolean showDown = SP.getBoolean("show_down",false);
       final boolean shellex = SP.getBoolean("shell_ex",true);
       if (showDown){
          getvar = "Download Reboot";
           getdes = "Restart Phone in Download Mode";
       } else {
           getvar = "Fastboot Reboot";
           getdes = "Restart Phone in Bootloader Mode";
       }
      final String[] itemname ={
               "Reboot",
               "Recovery Reboot",
                getvar,
               "Hot Reboot",
               "Safe Reboot",
               "Restart SystemUI",
               "Power OFF",

       };

       final String[] desc = {
               "Restart Phone",
               "Restart Phone in Recovery Mode",
              getdes,
               "Quick Restart Phone without Killing Hardware",
               "Allow you to Debug Application",
               "Restart Apps and UI",
               "Shutdown Phone Completely",
       };

      final Integer[] imgid={
               R.drawable.ic_action_reboot,
               R.drawable.ic_action_recovery,
               R.drawable.ic_action_fastboot,
               R.drawable.ic_action_hot,
               R.drawable.ic_action_safe,
               R.drawable.ic_action_ui,
               R.drawable.ic_action_poweroff,

       };
       if (shellex){
           if (isRootGiven()) {
               ShellExecuter exe = new ShellExecuter();
               String out = exe.Executer("su -c setprop persist.sys.safemode 0");
               Log.d("Output", out);
           }else{
               Toast.makeText(getApplicationContext(), "No Root Access Found !", Toast.LENGTH_SHORT).show();
           }
       }

       com.github.clans.fab.FloatingActionButton mfab1 = (FloatingActionButton) findViewById(R.id.fab_settings);
       mfab1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
               startActivity(myIntent);
           }
       });
       com.github.clans.fab.FloatingActionButton mfab2 = (FloatingActionButton) findViewById(R.id.fab_about);
       mfab2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              c=c+1;
               if (c>=2){
                   startAD();
               }
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setIcon(R.drawable.ic_action_name);
               builder.setMessage(Html.fromHtml("<b>QBoot App</b><br><br>Made with Android Studio by KP<br>Please Support Me by Not blocking ads<br><br><b>Contributors:</b><br>  -Dmytro Tarianyk (Clan)<br>  -Shripal Jain (shripal17)<br><br><i>App Version: "+BuildConfig.VERSION_NAME+"<br>Copyright KP @2017</i>"))
                       .setTitle( Html.fromHtml("<font color='#000000'>About</font>"));

               builder.setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });

               //show the dialog
               AlertDialog about = builder.create();
               about.show();

           }
       });
       com.github.clans.fab.FloatingActionButton mfab3 = (FloatingActionButton) findViewById(R.id.fab_device);
       mfab3.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               c=c+1;
               if (c>=2){
                   startAD();
               }
               setDeviceParams();
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setIcon(R.drawable.ic_action_name);
               builder.setMessage(Html.fromHtml(Device_info))
                       .setTitle( Html.fromHtml("<font color='#000000'>Device Info</font>"));

               builder.setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });

               //show the dialog
               AlertDialog about = builder.create();
               about.show();
           }
       });
       viewChangelog();
       MobileAds.initialize(getApplicationContext(), getString(R.string.InterstitialAds_ADBMOD));
       mInterstitialAd = new InterstitialAd(this);

       // set the ad unit ID
       mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
       CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid, desc);
       list=(ListView)findViewById(R.id.list);
       list.setAdapter(adapter);

       list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view,
                                   int position, long id) {
               // TODO Auto-generated method stub
              // String Slecteditem= itemname[+position];
              // Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
               SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
               boolean show = SP.getBoolean("prompt_val", false);
               switch(itemname[+position]) {

                   case "Reboot":
                       if (!shellex) {
                           startup();
                       }
                       if (show) {
                           shellexeute("su -c reboot");
                       } else {
                           Buildalert("Sure to Restart your Phone?", "su -c reboot");
                       }
                       break;
                   case "Download Reboot":
                       if (!shellex) {
                           startup();
                       }
                       if (show) {
                           shellexeute("su -c reboot download");
                       } else {
                           Buildalert("Sure to Restart your Phone?", "su -c reboot download");
                       }
                       break;
                   case "Hot Reboot":
                       if (!shellex) {
                           startup();
                       }
                       if (show) {
                           shellexeute("su -c setprop ctl.restart zygote");
                       } else {
                           Buildalert("Sure to Restart your Phone?", "su -c setprop ctl.restart zygote");
                       }
                       break;
                   case "Safe Reboot":
                       ShellExecuter exe = new ShellExecuter();
                       String outp = exe.Executer("su -c setprop persist.sys.safemode 1");
                       Log.d("Output", outp);
                       if (show) {
                           shellexeute("su -c setprop ctl.restart zygote");
                       } else {

                           Buildalert("Sure to Restart your Phone?", "su -c setprop ctl.restart zygote");
                       }
                       break;

                   case "Recovery Reboot":
                       if (!shellex) {
                           startup();
                       }
                       if (show) {
                           shellexeute("su -c reboot recovery");
                       } else {
                           Buildalert("Sure to Restart your Phone in Recovery?", "su -c reboot recovery");
                       }
                       break;
                   case "Restart SystemUI":
                       if (show) {
                           shellexeute("su -c pkill com.android.systemui");
                       } else {
                           Buildalert("Sure to Restart your UI?", "su -c pkill com.android.systemui");
               }
               ShellExecuter exe1 = new ShellExecuter();
                       String outp1 = exe1.Executer("su -c am startservice --user 0 -n com.android.systemui/.SystemUIService");
                       String outp2 = exe1.Executer("sh am startservice --user 0 -n com.android.systemui/.SystemUIService");
                       Log.d("Output", outp1);
                       Log.d("Output", outp2);
                       break;
                   case "Power OFF":
                       if (!shellex) {
                           startup();
                       }
                       if (show) {
                           shellexeute("su -c reboot -p");
                       } else {
                           Buildalert("Sure to Switch OFF Device?", "su -c reboot -p");
                       }
                       break;
                   case "Fastboot Reboot":
                       if (!shellex) {
                           startup();
                       }
                       if (show) {
                           shellexeute("su -c reboot bootloader");
                       } else {
                           Buildalert("Sure to Restart your Phone in Fastboot?", "su -c reboot bootloader");
                       }
                       break;
               }
           }
       });
      // viewChangelog();
    }
    private void showInterstitial() {
        SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean show = SP.getBoolean("ads_val",true);
        if (show) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
    public void startup(){
        if (isRootGiven()) {
            ShellExecuter exe = new ShellExecuter();
            String out = exe.Executer("su -c setprop persist.sys.safemode 0");
            Log.d("Output", out);
        }
    }
    public void startAD() {
        SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean show = SP.getBoolean("ads_val", true);
        if (show) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }
    }
    public void shellexeute(String command){
        if (isRootGiven()) {
            ShellExecuter exe = new ShellExecuter();
            String out = exe.Executer(command);
            Log.d("Output", out);
        }else{
            Toast.makeText(getApplicationContext(), "No Root Access Found !", Toast.LENGTH_SHORT).show();
        }
    }
    private void viewChangelog() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            // Place your dialog code here to display the dialog
showDialog();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }
    public void showDialog(){
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_action_name);
        builder.setMessage(Html.fromHtml("<b>Version 1.0 (13-7-17)</b><br>  -Initial Release<br>  -Public Commit on Github<br><br><font color='#008080'>Notice:</font><br><br>I am not Responsible for Anything happens to your device. This app is intended to be Free and Without Warranty. If you want to Contact send an Email through App<br><br><font color='#8B0000'>Root Privileges Required</font><br><br><font color='#008000'>Basic:</font><br><br>How to : <i>Just Click on any Reboot Option Accept the Dialog Confirmation</i><br><br>Settings : <i>In Settings you can Set some Functions as per your need.<br>1. Checking 'Do Not Show Prompt' will disable the prompt ask for every reboot task.<br>2. Checking 'Download mode' will basically Enable Download Mode Option in Menu needed for Samsung devices for Flashing and Stuffs.<br>3. Unchecking 'Execute Shell Execute' will disable the safety feature of build prop ids running at startup, this may decrease app launch activity.</i><br><br>Version 1.0"))
                .setTitle(Html.fromHtml("<font color='#000000'>What's New</font>"))
                .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
    public static boolean isRootAvailable(){
        for(String pathDir : System.getenv("PATH").split(":")){
            if(new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }
    public static boolean isRootGiven(){
        if (isRootAvailable()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = in.readLine();
                if (output != null && output.toLowerCase().contains("uid=0"))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null)
                    process.destroy();
            }
        }

        return false;
    }
    public void setDeviceParams() {

        String model = Build.MODEL;
        String build = Build.DISPLAY;
        String Brand = Build.MANUFACTURER;
        String hardware = Build.HARDWARE;
        String Bootloader = Build.BOOTLOADER;
       String version = Build.VERSION.RELEASE;
        if (isRootGiven()){
            Device_info = "<b>Brand:</b> " + Brand + "<br><b>Model:</b> "+ model + "<br><b>Build:</b> " + build+"<br><b>Hardware:</b> " + hardware + "<br><b>Version:</b> " + version + "<br><b>Bootloader:</b> " +Bootloader+"<br><br>Your Device is Rooted!";
        }
        else{
            Device_info = "<b>Brand:</b> " + Brand + "<br><b>Model:</b> "+ model + "<br><b>Build:</b> " + build+"<br><b>Hardware:</b> " + hardware + "<br><b>Version:</b> " + version + "<br><b>Bootloader:</b> " +Bootloader+"<br><br>No Root Found!";
        }

    }
 /*   private static boolean isRooted() {
        return findBinary("su");
    }
    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = { "/sbin/", "/system/bin/", "/system/xbin/",
                    "/data/local/xbin/", "/data/local/bin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" };
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;

                    break;
                }
            }
        }
        return found;
    }*/
    @Override
    public void onBackPressed() {
        FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.menu);
        if (fab.isOpened()) {
            fab.close(true);
        } else {
            SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
            boolean show = SP.getBoolean("exit_val", true);
            if (show) {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);

                adb.setTitle(Html.fromHtml("<font color='#000000'>Warning</font>"));
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setMessage("Are you Sure ?");
                adb.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                adb.show();
            } else {
                finish();
            }
        }
    }



private void Buildalert(String msg, final String Command){
    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
    builder1.setMessage(msg);
    builder1.setCancelable(true);

    builder1.setPositiveButton(
            "Yes",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (isRootGiven()) {
                        ShellExecuter exe = new ShellExecuter();
                        String outp = exe.Executer(Command);
                        Log.d("Output", outp);
                    }else{
                        Toast.makeText(getApplicationContext(), "No Root Access Found !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    builder1.setNegativeButton(
            "No",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

    AlertDialog alert11 = builder1.create();
    alert11.show();
}
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } */

}
