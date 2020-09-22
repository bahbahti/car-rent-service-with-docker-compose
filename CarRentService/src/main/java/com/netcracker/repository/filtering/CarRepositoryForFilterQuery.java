package com.netcracker.repository.filtering;

import com.netcracker.entity.Car;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CarRepositoryForFilterQuery {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Car> queryFunction(List<Integer> id, List<String> name, List<Integer> cost, Boolean isAvailable,
                                   List<String> color, List<String> storage, List<String> registrationNumber){
        String checkIfColorListIsEmpty = "";
        String checkIfNameListIsEmpty = "";
        String checkIfStorageListIsEmpty = "";
        String checkIfRegistrationNumberListIsEmpty = "";
        Integer checkIfIdListIsEmpty = 0;
        Integer checkIfCostListIsEmpty = 0;

        if(color.isEmpty()) {
            color.add(null);
            checkIfColorListIsEmpty = color.iterator().next();
        }
        if(name.isEmpty()) {
            name.add(null);
            checkIfNameListIsEmpty = name.iterator().next();
        }
        if(storage.isEmpty()) {
            storage.add(null);
            checkIfStorageListIsEmpty = storage.iterator().next();
        }
        if(registrationNumber.isEmpty()) {
            registrationNumber.add(null);
            checkIfRegistrationNumberListIsEmpty = registrationNumber.iterator().next();
        }
        if(id == null) {
            id = new ArrayList<>();
            id.add(null);
            checkIfIdListIsEmpty = id.iterator().next();
        }
        if(cost == null) {
            cost = new ArrayList<>();
            cost.add(null);
            checkIfCostListIsEmpty = cost.iterator().next();
        }

        StringBuilder nativeSqlString = new StringBuilder("SELECT * FROM cars ");
        nativeSqlString.append("WHERE (:checkIfIdListIsEmpty is null OR id IN :id) "
                + " AND (:checkIfNameListIsEmpty is null OR name IN :name)"
                + " AND (:checkIfCostListIsEmpty is null OR cost IN :cost)"
                + " AND (:checkIfColorListIsEmpty is null OR color IN :color)"
                + " AND (:checkIfStorageListIsEmpty is null OR storage IN :storage)"
                + " AND (:checkIfRegistrationNumberListIsEmpty is null OR registration_number IN :registrationNumber)");

        Session session = entityManager.unwrap(Session.class);
        Query<Car> query = session.createNativeQuery(nativeSqlString.toString(), Car.class);

        query.setParameterList("id", id, IntegerType.INSTANCE);
        query.setParameterList("name", name, StringType.INSTANCE);
        query.setParameterList("cost", cost, IntegerType.INSTANCE);
        query.setParameterList("registrationNumber", registrationNumber, StringType.INSTANCE);
        query.setParameterList("color", color, StringType.INSTANCE);
        query.setParameterList("storage", storage, StringType.INSTANCE);

        query.setParameter("checkIfNameListIsEmpty", checkIfNameListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfColorListIsEmpty", checkIfColorListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfStorageListIsEmpty", checkIfStorageListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfRegistrationNumberListIsEmpty", checkIfRegistrationNumberListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfIdListIsEmpty", checkIfIdListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfCostListIsEmpty", checkIfCostListIsEmpty, IntegerType.INSTANCE);

        List<Car> results =(List<Car>) query.list();

        if (isAvailable != null) {
            if (isAvailable) {
                results.removeIf(car -> !car.getIsAvailable());
            }
            else {
             results.removeIf(car -> car.getIsAvailable());
            }
        }

        return results;
    }

}
