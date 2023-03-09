package ru.duck.laba4.database;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import ru.duck.laba4.entity.Result;
import ru.duck.laba4.util.PlotUtil;
import ru.duck.laba4.util.TransactionUtil;

import java.util.List;

@Singleton
@Startup
@LocalBean
public class CoordinatesDatabase implements Database<Result> {

    @Override
    public void save(Result dto) {
        TransactionUtil.init(manager -> manager.persist(dto));
    }

    @Override
    public List<Result> getAll() {
        return TransactionUtil.initWithCallback(manager -> manager
                .createQuery("SELECT result FROM Result result", Result.class)
                .getResultList().stream()
                .peek(result -> result.setSuccessful(PlotUtil.isOnPlot(
                        result.getX(), result.getY(), result.getR()
                )))
                .toList());
    }

    @Override
    public void clear() {
        TransactionUtil.init(manager -> manager
                .createQuery("DELETE FROM Result")
                .executeUpdate());
    }
}
