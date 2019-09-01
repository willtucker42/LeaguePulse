package us.williamtucker.leaguepulse.parseserver;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Sashido init
       /* Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Jap0ao41AYnShj4kq1bZ4psL7QVcVNWOdePQfPRM")
                .clientKey("1iHtQK8JAleK9ikWTBCsZlZOUgMQQqeJfOPPVWcd")
                .server("https://pg-app-19hwlzwfmf2x3eq453vzygve4bugau.scalabl.cloud/1/")
                .build()
        );*/
        //old parse init
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("e697c0e6173b2416d8bc987584b3edb50d6b9b50")
                .clientKey("9fe64c509137d28490a682ca52ef2f39fda38064")
                .server("http://ec2-52-14-169-175.us-east-2.compute.amazonaws.com:80/parse/")
                .build()
        );

        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}