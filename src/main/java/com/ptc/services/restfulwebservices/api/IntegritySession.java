/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/22 01:56:15CEST $
 */
package com.ptc.services.restfulwebservices.api;

import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.IntegrationPoint;
import com.mks.api.IntegrationPointFactory;
import com.mks.api.Option;
import com.mks.api.Session;
import com.mks.api.response.APIConnectionException;
import com.mks.api.response.APIException;
import com.mks.api.response.APIInternalError;
import com.mks.api.response.ApplicationConnectionException;
import com.mks.api.response.Field;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.api.util.ResponseUtil;
import com.ptc.services.restfulwebservices.gateway.ItemMapperConfig;
import com.ptc.services.restfulwebservices.model.Document;
import com.ptc.services.restfulwebservices.model.Item;
import com.ptc.services.restfulwebservices.model.Node;
import com.ptc.services.restfulwebservices.security.SecurityInterceptor;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import java.io.IOException;
import static java.lang.System.out;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import mks.ci.api.CISDMIssue.ModelType;
import org.xml.sax.SAXException;

/**
 *
 * @author veckardt
 */
public class IntegritySession {

    private static IntegrationPoint integrationPoint = null;
    private static Session apiSession = null;
    private static ConnectionDetails connection = null;
    private static Map<String, String> allUsers = new HashMap<>();
    private static ItemMapperConfig itemMapperConfig = null;

    public static void initGatewayConfig(String gatewayConfig) throws ParserConfigurationException, SAXException, IOException {
        // if (itemMapperConfig == null) {
        itemMapperConfig = new ItemMapperConfig(gatewayConfig);
        // }
    }

    public static ItemMapperConfig getItemMapperConfig() {
        return itemMapperConfig;
    }

    public static Properties getConfigProperties() {
        return itemMapperConfig.getProperties();
    }

    public static String getProperty(String config) {
        return itemMapperConfig.getProperty(config);
    }

    public static String getProperty(String config, String defaultValue) {
        return itemMapperConfig.getProperty(config, defaultValue);
    }

    public static void connect() throws APIException, IOException {
        connection = new ConnectionDetails(SecurityInterceptor.username, SecurityInterceptor.password);
        IntegrationPointFactory ipf = IntegrationPointFactory.getInstance();
        try {

            // System.out.println(SecurityInterceptor.username);
            integrationPoint = ipf.createIntegrationPoint(connection.getHostname(), connection.getPort(), 4, 11);
            apiSession = integrationPoint.createNamedSession(null, null, connection.getUser(), connection.getPassword());
            // apiSession = integrationPoint.createSession(connection.getUser(), connection.getPassword());
            apiSession.setDefaultUsername(connection.getUser());
            apiSession.setDefaultPassword(connection.getPassword());

            out.println(connection.getLoginInfo());
            // System.out.println("REST: Current Dir: " + System.getProperty("user.dir"));

            // Command cmd = new Command(Command.IM, "connect");
            // execute(cmd);
        } catch (APIConnectionException ce) {
            out.println("REST-API: Unable to connect to Integrity Server");
            throw new APIException(ce);
        } catch (ApplicationConnectionException ace) {
            out.println("REST-API: Integrity Client unable to connect to Integrity Server");
            throw new APIException(ace);
        } catch (APIException apiEx) {
            out.println("REST-API: Unable to initialize");
            throw new APIException(apiEx);
        }
    }

    public static Response execute(Command cmd) throws APIException {
        try {
            long timestamp = System.currentTimeMillis();
            CmdRunner cmdRunner = apiSession.createCmdRunner();
            cmdRunner.setDefaultUsername(connection.getUser());
            cmdRunner.setDefaultPassword(connection.getPassword());
            cmdRunner.setDefaultHostname(connection.getHostname());
            cmdRunner.setDefaultPort(connection.getPort());
            // commandUsed = cmd.getCommandName();
            // OptionList ol = cmd.getOptionList();
            // for (int i=0; i<ol.size(); i++);
            //    Iterator o = ol.getOptions();
            //    o.

            Response response = cmdRunner.execute(cmd);
            cmdRunner.release();
            timestamp = System.currentTimeMillis() - timestamp;
            System.out.println("REST-API: " + response.getCommandString() + " [" + timestamp + "ms]");
            return response;
        } catch (APIInternalError e) {
            out.println("REST-API: " + e.getMessage()
            );
            throw new APIInternalError(e);
        }
    }

    public static void release() throws APIException, IOException {
        if (apiSession != null) {
            apiSession.release();
        }
        if (integrationPoint != null) {
            integrationPoint.release();
        }
    }

    public static List<Item> getAllItems(String queryName) throws APIException {
        List<Item> list = new ArrayList<>();
        try {
            connect();
            Command cmd = new Command(Command.IM, "issues");
            cmd.addOption(new Option("query", queryName));
            cmd.addOption(new Option("fields", "ID,Summary,State,Description,Project"));
            Response response = execute(cmd);
            WorkItemIterator wii = response.getWorkItems();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                list.add(new Item(wi));
            }
            release();
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        } catch (IOException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return list;
    }

    public static void getAllUsers() throws APIException, IOException {

        try {
            connect();
            Command cmd = new Command(Command.IM, "users");
            cmd.addOption(new Option("fields", "name,fullname"));
            Response response = execute(cmd);
            WorkItemIterator wii = response.getWorkItems();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                allUsers.put(wi.getId().toLowerCase(), wi.getField("fullname").getValueAsString() != null ? wi.getField("fullname").getValueAsString().toLowerCase() : "none");
            }
            release();
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        } catch (IOException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IOException(ex);
        }
        // return list;
    }

    public static Item getItem(String id) throws APIException {
        WorkItem wi = null;
        try {
            connect();
            Command cmd = new Command(Command.IM, "viewissue");
            cmd.addSelection(id);
            Response response = execute(cmd);
            wi = response.getWorkItem(id);
            release();
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        } catch (IOException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return new Item(wi);
    }

    public static Document getDocument(String id) throws APIException, IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Document document = new Document();
        List<Node> nodeList = new ArrayList<>();

        connect();
        Command cmd = new Command(Command.IM, "viewsegment");

        String fieldList = "";
        for (Document.FieldList fl : Document.FieldList.values()) {
            fieldList += (fieldList.isEmpty() ? "" : ",") + fl.toString();
        }
        for (Node.FieldList fl : Node.FieldList.values()) {
            fieldList += (fieldList.isEmpty() ? "" : ",") + fl.toString();
        }

        cmd.addOption(new Option("fields", fieldList));
        cmd.addSelection(id);
        Response response = execute(cmd);
        document.setId(id);

        WorkItemIterator wii = response.getWorkItems();
        WorkItem wiDoc = wii.next();

        document.fillFieldValues(wiDoc);

        while (wii.hasNext()) {
            WorkItem wi = wii.next();
            nodeList.add(new Node(wi));
        }
        release();
        Node[] nodeArray = new Node[nodeList.size()];
        nodeArray = nodeList.toArray(nodeArray);
        document.setNodelist(nodeArray);
        return document;
    }

    public static Node getNode(String id) throws APIException {
        WorkItem wi = null;
        try {
            connect();
            Command cmd = new Command(Command.IM, "viewissue");
            cmd.addSelection(id);
            Response response = execute(cmd);
            wi = response.getWorkItem(id);
            release();
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        } catch (IOException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return new Node(wi);
    }

    public static String putDocument(String[] typeDef, Document document) throws APIException, IOException {
        connect();

        String id;
        Command cmd;
        Boolean isNew = document.getId() == null || document.getId().replaceAll("-", "").isEmpty();
        if (isNew) {
            cmd = new Command(Command.IM, "createsegment");
            cmd.addOption(new Option("type", typeDef[0]));
        } else {
            cmd = new Command(Command.IM, "editissue");
            cmd.addSelection(document.getId());
        }

        cmd.addOption(new Option("field", Document.FieldList.Project.toString() + "=" + document.getProject()));
        cmd.addOption(new Option("field", Document.FieldList.Summary.toString() + "=" + document.getSummary()));
        cmd.addOption(new Option("field", Document.FieldList.Description.toString() + "=" + document.getDescription()));
        cmd.addOption(new Option("field", Document.FieldList.AssignedUser.toString() + "=" + parseUser(document.getAssignedUser())));

        Response response = execute(cmd);
        if (isNew) {
            id = response.getResult().getPrimaryValue().getId();
            document.setId(id);
        }

        for (Node node : document.getNodelist()) {
            putNode(typeDef[1], document.getId(), node);
        }

        release();
        return document.getId();
    }

    /**
     * Parses the given user to find the user name, if possible
     *
     * @param username
     * @return the found/not-found user
     */
    public static String parseUser(String username) {
        if (username == null || username.isEmpty()) {
            return "";
        }
        if (allUsers.containsKey(username.toLowerCase())) {
            return username;
        }
        for (String user : allUsers.keySet()) {
            if (allUsers.get(user).contains(username.toLowerCase())) {
                return user;
            }
        }
        return username;
    }

    public static String putNode(String type, String parent, Node node) throws APIException {
        Boolean isNew = node.getNodeid() == null || node.getNodeid().replaceAll("-", "").isEmpty();
        try {
            Command cmd;
            if (isNew) {
                cmd = new Command(Command.IM, "createcontent");
                cmd.addOption(new Option("type", type));
                cmd.addOption(new Option("parentID", parent));
            } else {
                cmd = new Command(Command.IM, "editissue");
                cmd.addSelection(node.getNodeid());
            }
            cmd.addOption(new Option("field", Node.FieldList.Text.toString() + "=" + node.getText()));
            
            cmd.addOption(new Option("field", Node.FieldList.Category.toString() + "=" + node.getCategory()));
            cmd.addOption(new Option("field", Node.FieldList.TextKey.toString() + "=" + node.getTextkey()));
            cmd.addOption(new Option("field", Node.FieldList.AssignedUser.toString() + "=" + parseUser(node.getAssignee())));
            cmd.addOption(new Option("field", Node.FieldList.Action.toString() + "=" + node.getAction()));
            if (node.getTargetdate() != null) {
                // Please enter the date using "MMM d, yyyy" format.
                DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                cmd.addOption(new Option("field", Node.FieldList.TargetDate.toString() + "=" + dateFormat.format(node.getTargetdate())));
            }

            Response response = execute(cmd);
            if (isNew) {
                String id = response.getResult().getPrimaryValue().getId();
                node.setNodeid(id);
            }
        } catch (APIException ex) {
            ExceptionHandler exh = new ExceptionHandler(ex);
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, exh.getMessage(), exh);
            throw new APIException(ex);
        }
        return node.getNodeid();
    }

    public static String putItem(Item item) throws APIException {
        String id = "";
        try {
            connect();
            Command cmd = new Command(Command.IM, "createissue");
            cmd.addOption(new Option("field", "summary=" + item.getSummary()));
            cmd.addOption(new Option("field", "project=" + item.getProject()));
            cmd.addOption(new Option("field", "description=" + item.getDescription()));
            cmd.addOption(new Option("type", "Defect"));
            Response response = execute(cmd);
            id = response.getResult().getPrimaryValue().getId();
            release();
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        } catch (IOException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return id;
    }

    /**
     * Reads a picklist field from Integrity admin
     *
     * @param fieldName
     * @param targetMap
     * @param entryName
     * @param descrField
     * @throws APIException
     */
    public static void readPickField(String fieldName, Map<String, Field> targetMap, String entryName,
            String descrField) throws APIException {
        targetMap.clear();
        Command cmd = new Command(Command.IM, "viewfield");
        cmd.addSelection(fieldName);
        // System.out.println("Reading " + fieldName + " ..");
        Response respo = execute(cmd);
        // ResponseUtil.printResponse(respo, 1, System.out);
        WorkItem wi = respo.getWorkItem(fieldName);
        Field picks = wi.getField(entryName);
        @SuppressWarnings("rawtypes")
        ListIterator li = picks.getList().listIterator();
        while (li.hasNext()) {
            com.mks.api.response.Item it = (com.mks.api.response.Item) li.next();
            targetMap.put(it.getField("value").getValueAsString(), it.getField(descrField));
        }
        log("Values in Picklist " + fieldName + ": " + targetMap.size(), 1);
    }
}
