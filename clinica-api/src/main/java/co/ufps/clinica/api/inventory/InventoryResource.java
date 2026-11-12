package co.ufps.clinica.api.inventory;

import co.ufps.clinica.adapters.inventory.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;

@Path("/api/inventory")
@RequireRoles({Role.ADMINISTRADOR, Role.FARMACIA})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {
    @Inject InventoryController controller;

    @POST @Path("/medicines")
    public Response registerMedicine(RegisterMedicineRequest request) {
        MedicineResponse response = controller.registerMedicine(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/medicines")
    public List<MedicineResponse> getAllActiveMedicines() {
        return controller.getAllActiveMedicines();
    }

    @POST @Path("/stock-lots")
    public Response createStockLot(CreateStockLotRequest request) {
        StockLotResponse response = controller.createStockLot(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST @Path("/movements")
    public Response recordMovement(RecordMovementRequest request) {
        StockMovementResponse response = controller.recordMovement(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET @Path("/alerts/low-stock")
    public List<StockLotResponse> lowStock(@QueryParam("branchId") String branchId) { return controller.lowStock(branchId); }

    @GET @Path("/alerts/expiring")
    public List<StockLotResponse> expiringBefore(@QueryParam("branchId") String branchId,
                                                 @QueryParam("before") java.time.LocalDate before) {
        return controller.expiringBefore(branchId, before);
    }
}
