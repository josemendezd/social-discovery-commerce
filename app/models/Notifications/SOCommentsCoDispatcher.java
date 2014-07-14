package models.Notifications;

import java.util.concurrent.Callable;

import scala.concurrent.ExecutionContext;

public class SOCommentsCoDispatcher {
	 public static void ComputeTask(final SOCommentsCo sc) 
	    {
	    	final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
	    	akka.dispatch.Futures.future(new Callable<Boolean>() {
	    	public Boolean call() {
	    		if(UserSubscriptions.find.where().eq("subscriber",sc.collection.contributor).eq("socommentco", true).findRowCount()>0)
	    		{
	    			SOCommentsCo.DispatchMessage(sc);
	    		}    		
	    		return true;
	    	}
	    	}, ec);
	    }
}
