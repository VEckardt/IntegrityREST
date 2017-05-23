/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.restfulwebservices.tools;

import com.mks.gateway.data.ExternalItem;
import com.mks.gateway.mapper.ItemMapperException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author veckardt
 */
public class PrefixingHandler {

    private static Map<String, String> prefixMap = new TreeMap<>();

    private static Boolean idPrefixingEnabled = true;

    public static void enableIDPrefixing() {
        idPrefixingEnabled = true;
    }

    public static void disableIDPrefixing() {
        idPrefixingEnabled = false;
    }

    public static void setIDPrefixing(Boolean prefixingEnabled) {
        idPrefixingEnabled = prefixingEnabled;
    }

    /**
     * addIdPrefix
     *
     * @param item
     */
    public static void addIdPrefix(String typeName) {
        if (idPrefixingEnabled) {
            String shortTypeName = getTypeShortName(typeName);
            prefixMap.put(shortTypeName, typeName);
        }
    }

    /**
     * addIdPrefix
     *
     * @param item
     */
    public static void addIdPrefix(ExternalItem item) {
        if (idPrefixingEnabled) {
            try {
                String type = getType(item);
                String shortTypeName = getTypeShortName(type);
                prefixMap.put(shortTypeName, type);

                if (item.hasField("ID") && !item.getValueAsString("ID").isEmpty()) {
                    item.getItemData().addField("ID", shortTypeName + "-" + item.getValueAsString("ID"));
                } else {
                    item.getItemData().addField("ID", shortTypeName + "-" + item.getId().getInternalID());
                }
            } catch (ItemMapperException ex) {
                Logger.getLogger(PrefixingHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     *
     * @return
     */
    public static String getPrefixInfo() {
        if (idPrefixingEnabled) {
            String info = "";
            for (String key : prefixMap.keySet()) {
                info = info + (info.isEmpty() ? "" : ", ") + key + " = " + prefixMap.get(key);
            }
            return "Abbreviations: " + info;
        } else {
            return "";
        }
    }

    private static String getType(ExternalItem item) throws ItemMapperException {
        return item.getValueAsString("Type");
    }

    /**
     * Returns a short name from all first letters (for a type)
     *
     * @param input
     * @return
     */
    public static String getTypeShortName(String input) {
        String typeShortName = "";
        String tt = (input + "").replaceAll("_", " ");
        for (String s : tt.split("(?<=[\\S])[\\S]+")) {
            typeShortName = typeShortName.concat(s.trim());
        }
        return typeShortName;
    }

}
