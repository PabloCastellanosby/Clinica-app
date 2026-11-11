package co.ufps.clinica.api.inventory;

import co.ufps.clinica.adapters.inventory.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/inventory")
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
}