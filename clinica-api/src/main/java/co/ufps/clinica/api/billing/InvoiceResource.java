package co.ufps.clinica.api.billing;

import co.ufps.clinica.adapters.billing.BillingController;
import co.ufps.clinica.adapters.billing.CreateInvoiceRequest;
import co.ufps.clinica.adapters.billing.InvoiceResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;

@Path("/api/invoices")
@RequireRoles({Role.ADMINISTRADOR, Role.RECEPCION})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceResource {
    @Inject BillingController billingController;

    @POST public Response createInvoice(CreateInvoiceRequest request) {
        InvoiceResponse response = billingController.createInvoice(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT @Path("/{id}/status")
    public InvoiceResponse changeStatus(@PathParam("id") String id, @QueryParam("status") String status) {
        return billingController.changeStatus(id, status);
    }

    @GET @Path("/{id}")
    public InvoiceResponse findById(@PathParam("id") String id) {
        return billingController.findById(id);
    }

    @GET @Path("/patient/{patientId}")
    public List<InvoiceResponse> findByPatient(@PathParam("patientId") String patientId) {
        return billingController.findByPatient(patientId);
    }
}
