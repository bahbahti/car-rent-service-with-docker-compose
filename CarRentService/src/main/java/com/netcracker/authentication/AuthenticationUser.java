package com.netcracker.authentication;

import com.netcracker.entity.Customer;
import com.netcracker.entity.Order;
import com.netcracker.entity.RepairOrder;
import com.netcracker.exception.ForbiddenException;
import com.netcracker.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthenticationUser {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer getAuthenticationUser () {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        Customer customer = customerRepository.findCustomerByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Customer " + username + " was not found"));
        return customer;
    }

    public void checkOnForbiddenAccess (Integer customerId) throws ForbiddenException {
        Customer loggedInCustomer = getAuthenticationUser();
        //зашел USER
        if(loggedInCustomer.getRoleId() == 2) {
            if (!loggedInCustomer.getId().equals(customerId)) {
                throw new ForbiddenException();
            }
        }
    }

    public void checkOnForbiddenAccess (List<Integer> idList) throws ForbiddenException {
        Customer loggedInCustomer = getAuthenticationUser();
        //зашел USER
        if(loggedInCustomer.getRoleId() == 2 && idList != null) {
            for (Integer id : idList) {
                if(!loggedInCustomer.getId().equals(id)) {
                    throw new ForbiddenException();
                }
            }
        }
    }

    public void checkOnForbiddenAccess (Order order) throws ForbiddenException {
        Customer loggedInCustomer = getAuthenticationUser();
        //зашел USER
        if (loggedInCustomer.getRoleId() == 2 && !loggedInCustomer.getId().equals(order.getCustomerId())) {
            throw new ForbiddenException();
        }
    }

    public void checkOnForbiddenAccess (RepairOrder repairOrder) throws ForbiddenException {
        Customer loggedInCustomer = getAuthenticationUser();
        //зашел USER
        if (loggedInCustomer.getRoleId() == 2 && !loggedInCustomer.getId().equals(repairOrder.getCustomerId())) {
            throw new ForbiddenException();
        }
    }

}
