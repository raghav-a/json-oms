package olympus.json.oms;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import olympus.common.JID;
import olympus.common.OlympusService;
import olympus.common.executors.KeyedExecutor;
import olympus.message.json.JSONSerializer;
import olympus.message.processor.MessagingService;
import olympus.message.types.OlympusMessage;
import olympus.message.types.Response;
import olympus.spartan.*;
import olympus.spartan.lookup.AllocationTracker;
import olympus.spartan.messaging.Router;
import olympus.spartan.transport.*;
import olympus.spartan.transport.ot.IITransport;
import olympus.spartan.transport.sparta.request.FetchAllocationsRequest;
import olympus.spartan.transport.sparta.request.RegisterRequest;
import olympus.spartan.transport.sparta.request.UnRegisterRequest;
import olympus.spartan.util.Environment;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


public class MessagingServiceStub extends MessagingService {


    LinkedBlockingQueue<Response> receivedResponses = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<String> receivedMessages = new LinkedBlockingQueue<>();

    public MessagingServiceStub(OlympusService olympusService) {
        super(new SpartanStub(Environment.LOCAL, olympusService), OlympusService.apollo, "1.0", new JSONSerializer());

    }

    @Override
    public boolean isLocalNode(JID jid) {
        return true;
    }

    @Override
    public JID getInstanceJID() {
        return JID.serviceInstanceJID("apollo", "123245");
    }

    @Override
    public ImmutableMessage send(OlympusMessage message) {
        receivedResponses.add((Response) message);
        return null;
    }

    @Override
    public ImmutableMessage send(RouterMessage message) {
        receivedMessages.add(message.getPayLoadAsString());
        return null;
    }

    @Override
    public ImmutableMessage send(JID jid, String message) {
        receivedMessages.add(message);
        return null;
    }

    public Response getResponse() {
        try {
            Response response = receivedResponses.poll(2, TimeUnit.SECONDS);
            assertFalse("Something is wrong! Response is null. \n ALL RECEIVED msgs : " + receivedResponses, response == null);
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMsgRcved() {
        try {

            return receivedMessages.poll(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void assertResponseReceived(String jsonRegex) {
        String rcved = new Gson().toJson(getResponse().payload());
        jsonRegex = jsonRegex.replaceAll("\\{", "\\\\{");
        jsonRegex = jsonRegex.replaceAll("\\}", "\\\\}");
        jsonRegex = jsonRegex.replaceAll("\\[", "\\\\[");
        jsonRegex = jsonRegex.replaceAll("\\]", "\\\\]");
        assertTrue("Stanza not received:" + jsonRegex + " \n but rcvd : \n" + rcved, rcved.matches(jsonRegex));
    }

    public void assertMessageReceived(String message) {
        String msgRcved = getMsgRcved();
        assertFalse("Something is wrong! Response is null. \n ALL RECEIVED msgs : " + receivedMessages, msgRcved == null);
        assertTrue("Stanza not received:" + message + " \n but rcvd : \n" + msgRcved, msgRcved.equals(message));
    }

    public RouterMessage prepareRouterMessage(OlympusMessage message) {
        return super.prepareRouterMessage(message);
    }

    @Override
    public String toString() {
        return "MessagingServiceStub";
    }

    private static class SpartaTransportStub implements SpartaTransport {

        public SpartaTransportStub(OlympusService olympusService) {
            this.olympusService = olympusService;
        }

        private OlympusService olympusService;

        @Override
        public ListenableFuture<FetchAllocationsRequest.FetchResponse> fetchAllocations(String serviceType, int version) {
            return Futures.immediateFuture(new FetchAllocationsRequest.FetchResponse(serviceType, version, null));
        }

        @Override
        public ListenableFuture<RegisterRequest.RegisterResponse> register(LocalServiceInstance serviceInstance) {
            int version = 1;

            JID localhost = JID.serviceInstanceJID(OlympusService.apollo.getPrimaryName(), "localhost");
            return Futures.immediateFuture(new RegisterRequest.RegisterResponse(localhost.toString(), version));
        }

        @Override
        public ListenableFuture<UnRegisterRequest.UnRegisterResponse> unRegister(LocalServiceInstance serviceInstance) {
            return Futures.immediateFuture(new UnRegisterRequest.UnRegisterResponse(1, 1));
        }

        @Override
        public EventBus getEventBus() {
            return new EventBus();
        }

        @Override
        public void close() {

        }
    }

    ;

    public static class InstanceTransportStub extends IITransport {

        public InstanceTransportStub(Environment env) {
            super(env);
        }

        @Override
        public Sender getSender(RemoteServiceInstance instance) {
            return mock(Sender.class);
        }

        @Nonnull
        @Override
        protected Sender createSender(RemoteServiceInstance instance) {
            return mock(Sender.class);
        }

        @Override
        public Receiver startReceiver(LocalServiceInstance instance, AllocationTracker a) throws Exception {
            return super.startReceiver(instance, a);
        }

        @Nonnull
        @Override
        protected Receiver createReceiver(LocalServiceInstance instance, AllocationTracker a) throws IOException {
            return new AbstractReceiver(instance, a) {
                @Override
                public boolean isRunning() {
                    return true;
                }

                @Override
                public void stop() {

                }

                @Override
                public String getListeningAddress() {
                    return "localhost";
                }
            };
        }

        @Override
        public void shortCircuit(LocalServiceInstance instance, RouterMessage message) {
            //super.shortCircuit(instance, message);
        }

        @Override
        public void stopListening(LocalServiceInstance instance) {
            //
        }

        @Override
        public synchronized void shutdown() {
            //
        }
    }

    public static class SpartanStub extends Spartan {

        private OlympusService olympusService;

        public SpartanStub(Environment env, OlympusService olympusService) {
            super(env);
            this.olympusService = olympusService;
        }

        @Override
        protected AllocationTracker createAllocationTracker(@Nonnull SpartaTransport transport, @Nonnull KeyedExecutor executor) {
            return mock(AllocationTracker.class);
        }


        @Override
        protected SpartaTransport createSpartaTransport(Environment env, EventBus eventBus) {
            return new SpartaTransportStub(olympusService);
        }

        @Override
        protected InstanceTransport createInstanceTransport(Environment env) {
            return new InstanceTransportStub(env);
        }

        @Override
        protected Router createRouter(@Nonnull InstanceTransport instanceTransport, @Nonnull AllocationTracker tracker) {
            return mock(Router.class);
        }

        @Override
        public ServiceInstance createService(OlympusService olympusService, String version) {
            ServiceInstance service = super.createService(olympusService, version);
            service.register(new ImmutableMessage.Listener() {
                @Override
                public void onMessage(ImmutableMessage message) {

                }
            });
            return service;
        }


    }
}


