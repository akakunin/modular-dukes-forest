/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package com.forest.shipment.wiring;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.forest.events.OrderEvent;
import com.forest.model.CustomerOrder;

@Named
@RequestScoped
public class ShippingBean implements Serializable {

    private static final Logger logger =
            Logger.getLogger(ShippingBean.class.getCanonicalName());
    private static final String SERVICE_ENDPOINT =
            "http://localhost:8080/dukes-store/services/orders";
    private static final String MEDIA_TYPE = MediaType.APPLICATION_JSON;
    private static final long serialVersionUID = -2526289536313985021L;
    protected Client client;
    @EJB
    OrderBrowser orderBrowser;

    @PostConstruct
    private void init() {
        client = ClientBuilder.newClient();
    }

    @PreDestroy
    private void clean() {
        client.close();
    }
    private Map<String, CustomerOrder> orders;

    /**
     * @return the orders
     */
    public Map<String, CustomerOrder> getOrders() {
        return orders;
    }

    /**
     * @param orders the orders to set
     */
    public void setOrders(Map<String, CustomerOrder> orders) {
        this.orders = orders;
    }

     public String getEndpoint() {
        return SERVICE_ENDPOINT;
    }

    public List<OrderEvent> listByStatus(final int status) {
        List<OrderEvent> entity = (List<OrderEvent>) client.target(SERVICE_ENDPOINT)
                .queryParam("status", String.valueOf(status))
                .request(MEDIA_TYPE)
                .get(new GenericType<List<OrderEvent>>() {
        });

        
        return entity;
    }

    public void updateOrderStatus(final String messageID, final String status) {
        // consume message
        CustomerOrder order = orderBrowser.processOrder(messageID);

        // call order service to update db in Store
        Response response = client.target(SERVICE_ENDPOINT)
                .path("/shipping/" + order.getId())
                .request(MEDIA_TYPE)
                .put(Entity.text(String.valueOf(status)));

        logger.log(Level.FINEST, "PUT Status response: {0}", response.getStatus());
    }

    /**
     * @return the orders
     */
    public List<String> getPendingOrders() {
        Map<String, CustomerOrder> pendingOrders = orderBrowser.getOrders();

        if (pendingOrders == null) {
            return null;
        } else {
            // update current pending orders map
            setOrders(pendingOrders);
            return new ArrayList<>(getOrders().keySet());
        }
    }

    public List<OrderEvent> getCompletedOrders() {
        return listByStatus(4);
    }
}
