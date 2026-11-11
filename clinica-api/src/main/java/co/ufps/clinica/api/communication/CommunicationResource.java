package co.ufps.clinica.api.communication;

import co.ufps.clinica.adapters.communication.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/communication")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommunicationResource {
    @Inject CommunicationController controller;

    @POST @Path("/messages")
    public Response sendMessage(SendMessageRequest request) {
        MessageResponse response = controller.sendMessage(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/messages/user/{userId}")
    public List<MessageResponse> getMessagesForUser(@PathParam("userId") String userId) {
        return controller.getMessagesForUser(userId);
    }

    @POST @Path("/notices")
    public Response publishNotice(PublishNoticeRequest request) {
        NoticeResponse response = controller.publishNotice(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/notices/branch/{branchId}")
    public List<NoticeResponse> getActiveNotices(@PathParam("branchId") String branchId) {
        return controller.getActiveNotices(branchId);
    }
}