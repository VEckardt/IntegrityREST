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
import com.mks.api.response.ApplicationConnectionException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemList;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.api.util.ResponseUtil;
import static com.ptc.services.restfulwebservices.api.Config.defaultAppDateFormat;
// import static com.ptc.services.restfulwebservices.api.Config.appDateFormat;
import com.ptc.services.restfulwebservices.gateway.ItemMapperConfig;
import com.ptc.services.restfulwebservices.model.Document;
import com.ptc.services.restfulwebservices.model.Item2;
import com.ptc.services.restfulwebservices.model.NameValuePair;
import com.ptc.services.restfulwebservices.model.Node;
import static com.ptc.services.restfulwebservices.tools.LogAndDebug.log;
import java.io.IOException;
import static java.lang.System.out;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 *
 * @author veckardt
 */
public class IntegritySession {

    private static IntegrationPoint integrationPoint = null;
    private static Session apiSession = null;
    private static ConnectionDetails connection = null;
    private Map<String, String> allUsers = new HashMap<>();
    private ItemMapperConfig itemMapperConfig = null;
    private static String appDateFormat = defaultAppDateFormat;

    public void initGatewayConfig(String gatewayConfig) throws ParserConfigurationException, SAXException, IOException {
        // if (itemMapperConfig == null) {
        itemMapperConfig = new ItemMapperConfig(gatewayConfig);
        // }
    }

    public IntegritySession() throws APIException, IOException {
        connect();
    }

    public IntegritySession(String dateFormat) throws APIException, IOException {
        appDateFormat = (dateFormat != null && !dateFormat.isEmpty()) ? dateFormat : appDateFormat;
        connect();
    }

    public ItemMapperConfig getItemMapperConfig() {
        return itemMapperConfig;
    }

    public Properties getConfigProperties() {
        return itemMapperConfig.getProperties();
    }

    public String getProperty(String config) {
        return itemMapperConfig.getProperty(config);
    }

    public String getProperty(String config, String defaultValue) {
        return itemMapperConfig.getProperty(config, defaultValue);
    }

    private void connect() throws APIException, IOException {
        connection = new ConnectionDetails(); // SecurityInterceptor.username, SecurityInterceptor.password);
        IntegrationPointFactory ipf = IntegrationPointFactory.getInstance();
        try {

            // System.out.println(SecurityInterceptor.username);
            integrationPoint = ipf.createIntegrationPoint(connection.getHostname(), connection.getPort(), 4, 11);
            apiSession = integrationPoint.createSession(connection.getUser(), connection.getPassword());
            // apiSession = integrationPoint.createSession(connection.getUser(), connection.getPassword());
            apiSession.setDefaultUsername(connection.getUser());
            apiSession.setDefaultPassword(connection.getPassword());
            // apiSession.setDefaultHostname(connection.getHostname());
            // apiSession.setDefaultPort(connection.getPort());
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

//    private void connect() throws APIException, IOException {
//        connection = new ConnectionDetails(); // SecurityInterceptor.username, SecurityInterceptor.password);
//        IntegrationPointFactory ipf = IntegrationPointFactory.getInstance();
//        try {
//
////            // System.out.println(SecurityInterceptor.username);
////            integrationPoint = ipf.createIntegrationPoint(connection.getHostname(), connection.getPort(), 4, 11);
////            apiSession = integrationPoint.createNamedSession("SESSION", null, connection.getUser(), connection.getPassword());
////            // apiSession = integrationPoint.createSession(connection.getUser(), connection.getPassword());
////            apiSession.setDefaultUsername(connection.getUser());
////            apiSession.setDefaultPassword(connection.getPassword());
////            apiSession.setDefaultHostname(connection.getHostname());
////            apiSession.setDefaultPort(connection.getPort());
////            out.println(connection.toString());
////            // System.out.println("REST: Current Dir: " + System.getProperty("user.dir"));
////
////            CmdRunner cmdr = apiSession.createCmdRunner();
////            cmdr.setDefaultUsername(connection.getUser());
////            cmdr.setDefaultPassword(connection.getPassword());
////            cmdr.setDefaultHostname(connection.getHostname());
////            cmdr.setDefaultPort(connection.getPort());
////            
////            Command cmd = new Command(Command.IM, "connect");
////            cmdr.execute(cmd);
//            
//            
//            
//            CmdRunner cmdRunner = ipf.createIntegrationPoint("localhost", 7001, 4, 11)
//                    .createNamedSession(null, null,"mksadmin", "password")
//                    .createCmdRunner();
//
//            //Setup connection
//            cmdRunner.setDefaultUsername("mksadmin");
//            cmdRunner.setDefaultPassword("password");
//
//            //	Test the connection is valid
//            cmdRunner.execute(new String[]{"im", "connect"});            
//
//        } catch (APIConnectionException ce) {
//            out.println("REST-API: Unable to connect to Integrity Server: " + ce.getMessage());
//            throw new APIException(ce);
//        } catch (ApplicationConnectionException ace) {
//            out.println("REST-API: Integrity Client unable to connect to Integrity Server: " + ace.getMessage());
//            throw new APIException(ace);
//        } catch (APIException apiEx) {
//            out.println("REST-API: Unable to initialize: " + apiEx.getMessage());
//            throw new APIException(apiEx);
//        }
//    }
    public Response execute(Command cmd) throws APIException {
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
        } catch (APIException e) {
            out.println("REST-API: APIInternalError - " + e.getMessage());
            throw new APIException(e);
        }
    }

    public void release() {
        if (apiSession != null) {
            try {
                apiSession.release();
            } catch (IOException | APIException ex) {
                Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        if (integrationPoint != null) {
            integrationPoint.release();
        }
    }

    public String getQueryFields(String queryName) throws APIException {
        // im queries --fields=fields "All Defects"
        Command cmd = new Command(Command.IM, "queries");
        cmd.addOption(new Option("fields", "fields"));
        cmd.addSelection(queryName);
        Response response = execute(cmd);
        // ResponseUtil.printResponse(response, 1, System.out);
        WorkItem wi = response.getWorkItem(queryName);
        return wi.getField("fields").getValueAsString();
    }

    public List<Item2> getAllItemsByQuery(String queryName) throws APIException {
        return getAllItemsByQuery(queryName, null);
    }

    public List<Item2> getAllItemsByQuery(String queryName, String fieldList) throws APIException {
        if (fieldList == null || fieldList.isEmpty()) {
            fieldList = getQueryFields(queryName);
        }
        List<Item2> list = new ArrayList<>();
        try {
            Command cmd = new Command(Command.IM, "issues");
            cmd.addOption(new Option("query", queryName));
            cmd.addOption(new Option("fields", fieldList));
            Response response = execute(cmd);

            WorkItemIterator wii = response.getWorkItems();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                list.add(new Item2(wi, fieldList));
            }
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return list;
    }

    public void getAllUsers() throws APIException, IOException {

        try {
            Command cmd = new Command(Command.IM, "users");
            cmd.addOption(new Option("fields", "name,fullname"));
            Response response = execute(cmd);
            WorkItemIterator wii = response.getWorkItems();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                allUsers.put(wi.getId().toLowerCase(), wi.getField("fullname").getValueAsString() != null ? wi.getField("fullname").getValueAsString().toLowerCase() : "none");
            }
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
    }

    public Item2 getItem(String id) throws APIException {
        WorkItem wi = null;
        try {
            Command cmd = new Command(Command.IM, "viewissue");
            cmd.addSelection(id);
            Response response = execute(cmd);
            wi = response.getWorkItem(id);
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return new Item2(wi);
    }

    public void deleteItem(String id) throws APIException {
        try {
            Command cmd = new Command(Command.IM, "deleteissue");
            cmd.addOption(new Option("noconfirm"));
            cmd.addSelection(id);
            Response response = execute(cmd);
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
    }

    public Document getDocument(String id) throws APIException, IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Document document = new Document();
        List<Node> nodeList = new ArrayList<>();

        Command cmd = new Command(Command.IM, "viewsegment");

        String fieldList = "";
        // for (Document.FieldList fl : Document.FieldList.values()) {
        for (String fl : Document.fieldList.split(",")) {
            fieldList += (fieldList.isEmpty() ? "" : ",") + fl.toString();
        }

        // for (Node.FieldList fl : Node.FieldList.values()) {
        for (String fl : Node.fieldList.split(",")) {
            fieldList += (fieldList.isEmpty() ? "" : ",") + fl.toString();
        }

        cmd.addOption(new Option("fields", fieldList));
        cmd.addSelection(id);
        Response response = execute(cmd);
        // ResponseUtil.printResponse(response, 1, System.out);
        document.setId(id);

        WorkItemIterator wii = response.getWorkItems();
        WorkItem wiDoc = wii.next();

        fillFieldValues(wiDoc, document.getValues());

        while (wii.hasNext()) {
            WorkItem wi = wii.next();
            nodeList.add(new Node(wi));
        }
        Node[] nodeArray = new Node[nodeList.size()];
        nodeArray = nodeList.toArray(nodeArray);
        document.setNodelist(nodeArray);
        return document;
    }

    /**
     * Returns an object Node from the Integrity server
     *
     * @param id
     * @return
     * @throws APIException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Node getNode(String id) throws APIException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        WorkItem wi = null;
        try {
            Command cmd = new Command(Command.IM, "viewissue");
            cmd.addSelection(id);
            Response response = execute(cmd);
            wi = response.getWorkItem(id);
        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new APIException(ex);
        }
        return new Node(wi);
    }

    public String putDocument(String[] typeDef, Document document) throws APIException, IOException, ParseException {
        // System.out.println(document.getId());
        // System.out.println(document.getNodelist().length);
        // System.out.println(document.getValues().length);
        Command cmd;
        Boolean isNew = document.getId() == null || document.getId().replaceAll("-", "").isEmpty();
        if (isNew) {
            cmd = new Command(Command.IM, "createsegment");
            cmd.addOption(new Option("type", typeDef[0]));
        } else {
            cmd = new Command(Command.IM, "editissue");
            cmd.addSelection(document.getId());
        }

        // System.out.println("Doc Count Values = " + document.getValues().size());
        // System.out.println("Doc Count Nodes  = " + document.getNodelist().length);
        for (NameValuePair fvp : document.getValues()) {
            // System.out.println(fvp.getName() + "=" + fvp.getValue());
            cmd.addOption(new Option("field", fvp.getName() + "=" + fvp.getValue()));
        }
//        cmd.addOption(new Option("field", Document.FieldList.AssignedUser.toString() + "=" + parseUser(document.getAssignedUser())));
        Response response = execute(cmd);

        if (isNew) {
            String id = response.getResult().getPrimaryValue().getId();
            document.setId(id);
        }

        for (Node node : document.getNodelist()) {
            putNode(typeDef[1], document.getId(), node);
        }

        return document.getId();
    }

    /**
     * Parses the given user to find the user name, if possible
     *
     * @param username
     * @return the found/not-found user
     */
    public String parseUser(String username) {
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

    public String putNode(String type, String parent, Node node) throws APIException, ParseException {

        // System.out.println("Node ID = " + node.getNodeid());
        Boolean isNew = (node.getNodeid() == null || node.getNodeid().isEmpty());

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

            // System.out.println("cmd: "+ cmd.);
            for (NameValuePair fvp : node.getValues()) {
                if (fvp.getName().equals("nodeid")) {
                } else if (fvp.getName().endsWith("Date") && fvp.hasValue()) {
                    Date someDate;
                    try {
                        DateFormat format = new SimpleDateFormat(appDateFormat, Locale.ENGLISH);
                        someDate = format.parse(fvp.getValue());
                    } catch (ParseException ex) {
                        DateFormat format = new SimpleDateFormat(appDateFormat.split(" ")[0], Locale.ENGLISH);
                        someDate = format.parse(fvp.getValue());
                    }

                    // Please enter the date using "MMM d, yyyy" format.
                    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                    cmd.addOption(new Option("field", fvp.getName() + "=" + dateFormat.format(someDate)));

                } else {
                    cmd.addOption(new Option("field", fvp.getName() + "=" + fvp.getValue()));
                }
                // System.out.println("Put Node: " + fvp.getName() + "=" + fvp.getValue());
            }

//            cmd.addOption(new Option("field", Node.FieldList.AssignedUser.toString() + "=" + parseUser(node.getAssignee())));
            Response response = execute(cmd);
            if (isNew) {
                String id = response.getResult().getPrimaryValue().getId();
                System.out.println("New node ID: " + id);
                node.setNodeid(id);
            }
        } catch (APIException ex) {
            ExceptionHandler exh = new ExceptionHandler(ex);
            Logger
                    .getLogger(IntegritySession.class
                            .getName()).log(Level.SEVERE, exh.getMessage(), exh);
            throw new APIException(ex);
        }

        return node.getNodeid();
    }

    public String putItem(Item2 item) throws APIException {
        String id = "";
        String type = "";
        try {
            Command cmd = new Command(Command.IM, "createissue");
            for (NameValuePair nvp : item.getValues()) {
                if (nvp.getName().toLowerCase().equals("type")) {
                    type = nvp.getValue();
                } else if (nvp.getValue() != null && !nvp.getValue().isEmpty() && !nvp.getValue().equals("unspecified")) {
                    cmd.addOption(new Option("field", nvp.getName() + "=" + nvp.getValue()));
                }
            }
            cmd.addOption(new Option("type", type));
            Response response = execute(cmd);
            id = response.getResult().getPrimaryValue().getId();

        } catch (APIException ex) {
            Logger.getLogger(IntegritySession.class
                    .getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
    public void readPickField(String fieldName, Map<String, Field> targetMap, String entryName,
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

    /**
     * Fills a value list with all field values provided by the workitem
     *
     * @param wi
     * @param values
     */
    public static void fillFieldValues(WorkItem wi, List<NameValuePair> values) {
        Iterator docFields = wi.getFields();
        while (docFields.hasNext()) {
            Field fld = (Field) docFields.next();
            // out.println(fld.getName() + "" + fld.getModelType().toString());

            String fldName = fld.getName();
            if (fld.getDataType() != null) {
                String value = null;
                String type = "String";  // default type
                if (fld.getDataType().endsWith("String")) {
                    value = fld.getValueAsString();
                } else if (fld.getDataType().endsWith("Date")) {
                    DateFormat format = new SimpleDateFormat(appDateFormat, Locale.ENGLISH);
                    value = format.format(fld.getDateTime());
                    type = "Date";
                } else if (fld.getDataType().endsWith("Item")) {
                    com.mks.api.response.Item item = fld.getItem();
                    String modelType = item.getModelType();
                    if (modelType.endsWith("User")
                            || modelType.endsWith("Project")
                            || modelType.endsWith("Type")
                            || modelType.endsWith("State")) {
                        value = fld.getItem().getId();
                    }
                } else {
                    value = fld.getValueAsString();
                }
                values.add(new NameValuePair(fldName, type, value));
            }
        }
        values.add(new NameValuePair("nodeid", "String", wi.getId()));
    }

    public static void fillFieldValues(WorkItem wi, List<NameValuePair> values, String fieldList) {

        for (String fldName : fieldList.split(",")) {
            Field fld = wi.getField(fldName);
            if (fld.getDataType() != null) {
                String value = null;
                String type = "String";  // default type
                if (fld.getDataType().endsWith("String")) {
                    value = fld.getValueAsString();
                } else if (fld.getDataType().endsWith("Date")) {
                    DateFormat format = new SimpleDateFormat(appDateFormat, Locale.ENGLISH);
                    value = format.format(fld.getDateTime());
                    type = "Date";
                } else if (fld.getDataType().endsWith("Item")) {
                    com.mks.api.response.Item item = fld.getItem();
                    String modelType = item.getModelType();
                    if (modelType.endsWith("User")
                            || modelType.endsWith("Project")
                            || modelType.endsWith("Type")
                            || modelType.endsWith("State")) {
                        value = fld.getItem().getId();
                    }
                } else {
                    value = fld.getValueAsString();
                }
                values.add(new NameValuePair(fldName, type, value));
            } else {
                values.add(new NameValuePair(fldName, "String", ""));
            }
        }
    }

    /**
     * Returns the users from a particular group with AA command (W&D)
     *
     * @param GroupName
     * @return
     * @throws JSONException
     * @throws APIException
     */
    public JSONObject getUsersFromGroup(String GroupName) throws JSONException, APIException {
        JSONArray jsonArr = new JSONArray();
        try {
            // connect();
            // aa groups --members Managers
            Command cmd = new Command(Command.AA, "groups");
            cmd.addOption(new Option("members"));
            cmd.addSelection(GroupName);
            Response response = execute(cmd);
            WorkItem group = response.getWorkItem(GroupName);
            ResponseUtil.printResponse(response, 1, System.out);
            ItemList wii = (ItemList) group.getField("members").getList();
            Iterator members = wii.getItems();
            while (members.hasNext()) {
                Item item = (Item) members.next();
                JSONObject jsonObj = new JSONObject();
                String fullname = getFullName(item.getId());
                jsonObj.put("id", item.getId());
                jsonObj.put("fullname", fullname == null ? "-" : fullname);
                jsonObj.put("modeltype", item.getModelType());
                jsonArr.put(jsonObj);
            }
            // release();
        } catch (APIException ex) {
            ExceptionHandler eh = new ExceptionHandler(ex);
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, eh.getMessage(), eh);
            throw new APIException(ex);
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("hostname", connection.getHostname());
        mainObj.put("port", connection.getPort());
        mainObj.put("user", connection.getUser());
        mainObj.put("group", GroupName);
        mainObj.put("members", jsonArr);
        return mainObj;
    }
    
    /**
     * Returns the users from a particular group with AA command (W&D)
     *
     * @param GroupName
     * @return
     * @throws JSONException
     * @throws APIException
     */
    public JSONObject getMKSDomainGroups() throws JSONException, APIException {
        JSONArray jsonArr = new JSONArray();
        try {
            // connect();
            // aa groups 
            Command cmd = new Command(Command.AA, "groups");
            Response response = execute(cmd);
            ResponseUtil.printResponse(response, 1, System.out);
            WorkItemIterator groups = response.getWorkItems();
            while (groups.hasNext()) {
                Item item = (Item) groups.next();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", item.getId());
                jsonObj.put("modeltype", item.getModelType());
                jsonArr.put(jsonObj);
            }
            // release();
        } catch (APIException ex) {
            ExceptionHandler eh = new ExceptionHandler(ex);
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, eh.getMessage(), eh);
            throw new APIException(ex);
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("hostname", connection.getHostname());
        mainObj.put("port", connection.getPort());
        mainObj.put("user", connection.getUser());
        mainObj.put("group", "");
        mainObj.put("members", jsonArr);
        return mainObj;
    }    

    private String getFullName(String userName) throws APIException {
        // im users --fields=name,fullname
        try {
            Command cmd = new Command(Command.IM, "users");

            cmd.addOption(new Option("fields", "name,fullname"));
            cmd.addSelection(userName);
            Response response = execute(cmd);
            WorkItem user = response.getWorkItem(userName);
            return user.getField("fullname").getValueAsString();
        } catch (APIException ex) {
            ExceptionHandler eh = new ExceptionHandler(ex);
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, eh.getMessage(), eh);
            throw new APIException(ex);
        }
    }

    /**
     * Returns the allowed Categories for a specified node type
     *
     * @param typeName
     * @return
     * @throws APIException
     * @throws IOException
     * @throws JSONException
     */
    public JSONArray getAllowedNodeCategories(String typeName) throws APIException, IOException, JSONException {
        JSONArray jsonArr = new JSONArray();
        try {
            // connect();
            Command cmd = new Command(Command.IM, "types");
            cmd.addOption(new Option("fields", "fieldRelationships"));
            cmd.addSelection(typeName);
            Response response = execute(cmd);
            WorkItem group = response.getWorkItem(typeName);
            ResponseUtil.printResponse(response, 1, System.out);
            ItemList wii = (ItemList) group.getField("fieldRelationships").getList();
            com.mks.api.response.Item refItemType = wii.getItem("Referenced Item Type");

            ItemList targetFields = (ItemList) refItemType.getField("targetFields").getList();
            Iterator relationships = targetFields.getItems();
            while (relationships.hasNext()) {
                com.mks.api.response.Item item = (com.mks.api.response.Item) relationships.next();
                if (item.getId().equals("Category")) {
                    String cliSpec = item.getField("cliSpec").getValueAsString();
                    // Referenced Item2 Type=Shared Meeting Minutes Entry:Category=Entry,ToDo,Agenda,Decision,Info
                    if (cliSpec.contains("=Shared ")) {
                        String entries = cliSpec.split("=")[2];
                        for (String entry : entries.split(",")) {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put(entry, entry);
                            jsonArr.put(jsonObj);
                        }
                    }
                }
            }
            // release();
        } catch (APIException ex) {
            ExceptionHandler eh = new ExceptionHandler(ex);
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, eh.getMessage(), eh);
            throw new APIException(ex);
        }
        return jsonArr;
    }

    /**
     * Returns the allowed Categories for a specified node type
     *
     * @param typeName
     * @return
     * @throws APIException
     * @throws IOException
     * @throws JSONException
     */
    public JSONArray getAllowedStates(String typeName) throws APIException, IOException, JSONException {
        Map<String, String> states = new LinkedHashMap<>();
        JSONArray jsonArr = new JSONArray();
        try {
            // connect();
            Command cmd = new Command(Command.IM, "types");
            cmd.addOption(new Option("fields", "stateTransitions"));
            cmd.addSelection(typeName);
            Response response = execute(cmd);
            WorkItem group = response.getWorkItem(typeName);
            // ResponseUtil.printResponse(response, 1, System.out);
            ListIterator wii = group.getField("stateTransitions").getList().listIterator();
            while (wii.hasNext()) {
                Item state = (Item) wii.next();
                ListIterator wit = state.getField("targetStates").getList().listIterator();
                while (wit.hasNext()) {
                    Item targetState = (Item) wit.next();
                    states.put(targetState.getId(), targetState.getId());
                }
            }

            for (String state : states.keySet()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put(state, state);
                jsonArr.put(jsonObj);
            }

//            com.mks.api.response.Item sourceStates = wii.getItem("Referenced Item Type");
//
//            ItemList targetFields = (ItemList) sourceStates.getField("targetFields").getList();
//            Iterator relationships = targetFields.getItems();
//            while (relationships.hasNext()) {
//                com.mks.api.response.Item item = (com.mks.api.response.Item) relationships.next();
//                if (item.getId().equals("Category")) {
//                    String cliSpec = item.getField("cliSpec").getValueAsString();
//                    // Referenced Item2 Type=Shared Meeting Minutes Entry:Category=Entry,ToDo,Agenda,Decision,Info
//                    if (cliSpec.contains("=Shared ")) {
//                        String entries = cliSpec.split("=")[2];
//                        for (String entry : entries.split(",")) {
//                            JSONObject jsonObj = new JSONObject();
//                            jsonObj.put(entry, entry);
//                            jsonArr.put(jsonObj);
//                        }
//                    }
//                }
//            }
            // release();
        } catch (APIException ex) {
            ExceptionHandler eh = new ExceptionHandler(ex);
            Logger.getLogger(IntegritySession.class.getName()).log(Level.SEVERE, eh.getMessage(), eh);
            throw new APIException(ex);
        }
        return jsonArr;
    }
}
