package dwhit.emerapp;
/**
 * Created by Fabio Ricardoo on 10/28/2015.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterTimeline{
    public String user;
    public TwitterTimeline(String user){
        this.user = user;
    }
    public List<String> GetTimeline() {
        List<String> response = new ArrayList<String>();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("ZIjvYZwGiYsOzEXhZU4sDLMQh")
                .setOAuthConsumerSecret("nQQB7zVJZgIdnQOPm5sLMWaY2Q2gUPeiPCBbZtJdaltG4Z3SXg")
                .setOAuthAccessToken("3944073617-YO5VbT0mu4I0ttjeCzRNsPasOLt7YGTg1T4mWhI")
                .setOAuthAccessTokenSecret("oxRavA2vCqhcPZy03opanHv7sxgIZvs20EquwmTLkjMSA")
                .setUseSSL(true);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter1 = tf.getInstance();
        List<Status> statuses;
        try {

            statuses = twitter1.getUserTimeline(user);
            for (Status status : statuses) {
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                response.add("@" + status.getUser().getScreenName() + " - " + status.getText());
            }
        } catch (TwitterException te) {
                te.printStackTrace();
                response.add("Failed to get timeline: " + te.getMessage());
                //System.out.println("Failed to get timeline: " + te.getMessage());
                //System.exit(-1);
        }

        return  response;
    }

}
