package models.Notifications;

import java.util.concurrent.Callable;

import scala.concurrent.ExecutionContext;

public class SOFollowsDispatcher {
	public static void ComputeTask(final SOFollows sf) 
    {
    	final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
    	akka.dispatch.Futures.future(new Callable<Boolean>() {
    	public Boolean call() {
    		if(UserSubscriptions.find.where().eq("subscriber",sf.leader).eq("sofollows", true).findUnique()!=null)
    		{
    			SOFollows.DispatchMessage(sf);
    		}    		
    		return true;
    	}
    	}, ec);
    }
}
