package models.Notifications;

import java.util.concurrent.Callable;

import scala.concurrent.ExecutionContext;

/**
 * 
 * Dispatcher for SOCommentsPr
 *
 */
public class SOCommentsDispatcher {
	 public static void ComputeTask(final SOCommentsPr scp) 
	    {
	    	final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
	    	akka.dispatch.Futures.future(new Callable<Boolean>() {
	    	public Boolean call() {
	    		if(UserSubscriptions.find.where().eq("subscriber",scp.product.Founder.contrib).eq("socommentpr", true).findUnique()!=null)
	    		{
	    			SOCommentsPr.DispatchMessage(scp);
	    		}
	    		return true;
	    	}
	    	}, ec);
	    }
}
