package myusick.controller;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseBroadcaster;

import javax.ws.rs.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by david on 01/05/2015.
 */
@Path("ws")
public class SSEProvider {

    private static ConcurrentHashMap<String,SseBroadcaster> listenerMap = new ConcurrentHashMap<>();
    private static SSEDispatcher SSEDispatcher = new SSEDispatcher();

    @Path("/sub/{id}")
    @GET
    public EventOutput subscribe(@PathParam("id") String id){
//        Client client = ClienbtBuilder.newBuilder().register(SseFeature.class).build();
        SseBroadcaster b = new SseBroadcaster();
        final EventOutput eventOutput = new EventOutput();
        b.add(eventOutput);
//        WebSocketBroadcaster b = new WebSocketBroadcaster();
        listenerMap.put(id,b);
        return eventOutput;
    }

    @Path("/unsub/{id}")
    @DELETE
    public String unsubscribe(@PathParam("id") String id){
        SseBroadcaster b = listenerMap.remove(id);
        b.closeAll();
        return "Unsubscribed from broker";
    }

    public static SSEDispatcher getSSEDispatcher() {
        return SSEDispatcher;
    }

    protected static ConcurrentHashMap<String, SseBroadcaster> getListenerMap() {
        return listenerMap;
    }
}
