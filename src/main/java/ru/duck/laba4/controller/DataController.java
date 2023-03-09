package ru.duck.laba4.controller;

import com.google.gson.Gson;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.duck.laba4.database.CoordinatesDatabase;
import ru.duck.laba4.auth.Secured;
import ru.duck.laba4.entity.Result;
import ru.duck.laba4.util.NumberUtil;
import ru.duck.laba4.util.PlotUtil;

import java.util.Collections;

@Singleton
@Lock(LockType.WRITE)
@Path("/main")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataController {

    @EJB
    CoordinatesDatabase coordinatesDatabase;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Secured
    public Response addResult(
            @FormParam("x") String xString,
            @FormParam("y") String yString,
            @FormParam("r") String rString,
            @FormParam("clicked") String clicked
    ) {
        try {
            double x = NumberUtil.roundDouble(xString),
                    y = NumberUtil.roundDouble(yString),
                    r = NumberUtil.roundDouble(rString);

            if (!PlotUtil.validate(x, y, r, Boolean.parseBoolean(clicked)))
                throw new IllegalArgumentException();

            Result result = new Result();
            result.setX(x);
            result.setY(y);
            result.setR(r);
            result.setSuccessful(PlotUtil.isOnPlot(x, y, r));
            result.setTime(System.currentTimeMillis());

            coordinatesDatabase.save(result);

            return Response.ok()
                    .entity(new Gson().toJson(result))
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Gson().toJson(Collections.singletonMap(
                            "message",
                            "error"
                    )))
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response getAllResults() {
        return Response.ok()
                .entity(new Gson().toJson(coordinatesDatabase.getAll()))
                .build();
    }

    @DELETE
    @Secured
    public Response clearResults() {
        coordinatesDatabase.clear();
        return Response.ok().build();
    }

}