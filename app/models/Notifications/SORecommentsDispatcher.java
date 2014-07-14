package models.Notifications;

import java.util.concurrent.Callable;

import scala.concurrent.ExecutionContext;

public class SORecommentsDispatcher {
	 public static void ComputeTask(final SORecommends sr) 
	    {
	    	final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
	    	akka.dispatch.Futures.future(new Callable<Boolean>() {
	    	public Boolean call() {
	    		if(UserSubscriptions.find.where().eq("subscriber",sr.leader).eq("sorecommends", true).findUnique()!=null)
	    		{
	    			SORecommends.DispatchMessage(sr);
	    		}    		
	    		return true;
	    	}
	    	}, ec);
	    }
}
