/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision$
 * Last changed:   $Date$
 */

package com.ptc.services.restfulwebservices.model;

import java.util.List;

/**
 *
 * @author veckardt
 */
public class ItemList {
    Metadata metadata;
    List<Item2> items;
    
    public ItemList (Metadata metadata, List<Item2> items) {
        this.metadata = metadata;
        this.items = items;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void setItems(List<Item2> items) {
        this.items = items;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Item2> getItems() {
        return items;
    }

}
