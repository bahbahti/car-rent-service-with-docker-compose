package com.netcracker.repository.filtering;

import com.netcracker.entity.RepairOrder;
import com.netcracker.entity.enums.RepairStatus;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RepairRepositoryForFilterQuery {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RepairOrder> queryFunction(List<Integer> id, List<Integer> carId, List<Integer> customerId, List<Integer> repairIdExternal,
                                        List<Integer> price, List<RepairStatus> repairStatus, List<Date> startRepairDay, List<Date> endRepairDay) {

        Integer checkIfIdListIsEmpty = 0;
        Integer checkIfPriceListIsEmpty = 0;
        Integer checkIfCarIdListIsEmpty = 0;
        Integer checkIfCustomerIdListIsEmpty = 0;
        Integer checkIfRepairIdExternalListIsEmpty = 0;
        Boolean checkIfStartRepairDayListIsEmpty = false;
        Boolean checkIfEndRepairDayListIsEmpty = false;

        if(id == null) {
            id = new ArrayList<>();
            id.add(null);
            checkIfIdListIsEmpty = id.iterator().next();
        }
        if(carId == null) {
            carId = new ArrayList<>();
            carId.add(null);
            checkIfCarIdListIsEmpty = carId.iterator().next();
        }
        if(customerId == null) {
            customerId = new ArrayList<>();
            customerId.add(null);
            checkIfCustomerIdListIsEmpty = customerId.iterator().next();
        }
        if(repairIdExternal == null) {
            repairIdExternal = new ArrayList<>();
            repairIdExternal.add(null);
            checkIfRepairIdExternalListIsEmpty = repairIdExternal.iterator().next();
        }
        if(price == null) {
            price = new ArrayList<>();
            price.add(null);
            checkIfPriceListIsEmpty = price.iterator().next();
        }
        if(startRepairDay == null) {
            startRepairDay = new ArrayList<>();
            startRepairDay.add(null);
            checkIfStartRepairDayListIsEmpty = null;
        }
        if(endRepairDay == null) {
            endRepairDay = new ArrayList<>();
            endRepairDay.add(null);
            checkIfEndRepairDayListIsEmpty = null;
        }

        StringBuilder nativeSqlString = new StringBuilder("SELECT * FROM repairs ");
        nativeSqlString.append("WHERE (:checkIfIdListIsEmpty is null OR id IN :id) "
                + " AND (:checkIfPriceListIsEmpty is null OR price IN :price)"
                + " AND (:checkIfCarIdListIsEmpty is null OR car_id IN :carId)"
                + " AND (:checkIfCustomerIdListIsEmpty is null OR customer_id IN :customerId)"
                + " AND (:checkIfRepairIdExternalListIsEmpty is null OR repair_id_external IN :repairIdExternal)"
                + " AND (:checkIfStartRepairDayListIsEmpty is null OR start_day IN :startRepairDay)"
                + " AND (:checkIfEndRepairDayListIsEmpty is null OR end_day IN :endRepairDay)");

        Session session = entityManager.unwrap(Session.class);
        Query<RepairOrder> query = session.createNativeQuery(nativeSqlString.toString(), RepairOrder.class);

        query.setParameterList("id", id, IntegerType.INSTANCE);
        query.setParameterList("price", price, IntegerType.INSTANCE);
        query.setParameterList("carId", carId, IntegerType.INSTANCE);
        query.setParameterList("customerId", customerId, IntegerType.INSTANCE);
        query.setParameterList("repairIdExternal", repairIdExternal, IntegerType.INSTANCE);
        query.setParameterList("startRepairDay", startRepairDay, DateType.INSTANCE);
        query.setParameterList("endRepairDay", endRepairDay, DateType.INSTANCE);


        query.setParameter("checkIfIdListIsEmpty", checkIfIdListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfPriceListIsEmpty", checkIfPriceListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfCarIdListIsEmpty", checkIfCarIdListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfCustomerIdListIsEmpty", checkIfCustomerIdListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfRepairIdExternalListIsEmpty", checkIfRepairIdExternalListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfStartRepairDayListIsEmpty", checkIfStartRepairDayListIsEmpty, BooleanType.INSTANCE);
        query.setParameter("checkIfEndRepairDayListIsEmpty", checkIfEndRepairDayListIsEmpty, BooleanType.INSTANCE);

        List<RepairOrder> results =(List<RepairOrder>) query.list();

        if(!repairStatus.isEmpty()) {
            if(!repairStatus.contains(RepairStatus.PENDING)) {
                results.removeIf(repairOrder -> repairOrder.getRepairStatus().equals(RepairStatus.PENDING));
            }
            if(!repairStatus.contains(RepairStatus.IN_PROGRESS)) {
                results.removeIf(repairOrder -> repairOrder.getRepairStatus().equals(RepairStatus.IN_PROGRESS));
            }
            if(!repairStatus.contains(RepairStatus.FINISHED)) {
                results.removeIf(repairOrder -> repairOrder.getRepairStatus().equals(RepairStatus.FINISHED));
            }
        }

        return results;
    }

}