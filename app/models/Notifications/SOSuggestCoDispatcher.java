package models.Notifications;

import java.util.concurrent.Callable;

import scala.concurrent.ExecutionContext;

public class SOSuggestCoDispatcher {
	public static void ComputeTask(final SOSuggestsCo ssc) 
    {
    	final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
    	akka.dispatch.Futures.future(new Callable<Boolean>() {
    	public Boolean call() {
    		if(UserSubscriptions.find.where().eq("subscriber",ssc.collection.contributor).eq("sosuggestsco", true).findRowCount()>0)
    		{
    			SOSuggestsCo.DispatchMessage(ssc);
    		}    		
    		return true;
    	}
    	}, ec);
    }
}
