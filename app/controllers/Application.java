package controllers;

import play.*;
import play.mvc.*;
import utils.GtfsEntitiesCache;
import utils.IdValuePair;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import models.*;

@With(Secure.class)
public class Application extends Controller {

	@Before
	static void initSession() throws Throwable {
		
	    if(Security.isConnected()) {
	    	renderArgs.put("user", Security.connected());
	    	
	    	Account account = Account.find("username = ?", Security.connected()).first();
	        
	    	renderArgs.put("agencyName", entities.agencyMap.get(account.agencyId));
	        renderArgs.put("agencyId", account.agencyId);
        }
        else {
        	
        	if(Account.count() == 0) {
        		Account account = new Account("admin", "admin", "admin@test.com", true, null);
	        	account.save();
	        }
        	
        	Secure.login();
        }
    }
	
	public static GtfsEntitiesCache entities = new GtfsEntitiesCache();
	
	private static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory jf = new JsonFactory();

    private static String toJson(Object pojo, boolean prettyPrint)
    throws JsonMappingException, JsonGenerationException, IOException {
        StringWriter sw = new StringWriter();
        JsonGenerator jg = jf.createJsonGenerator(sw);
        if (prettyPrint) {
            jg.useDefaultPrettyPrinter();
        }
        mapper.writeValue(jg, pojo);
        return sw.toString();
    }

    public static void index() {
    	
    	String agencyId = renderArgs.get("agencyId").toString();
    	
    	List<Alert> activeAlerts = Alert.findActiveAlerts(agencyId);
    	
        render(activeAlerts);
    }
    
    public static void create() {
        render();
    }
    
    public static void edit(Long id) {
        render(id);
    }
    
    public static void changePassword(String currentPassword, String newPassword) {
        
        if(Security.isConnected())
        {
            if(currentPassword != null && newPassword != null)
            {
                Boolean changed = Account.changePassword(Security.connected(), currentPassword, newPassword);
                
                if(changed)
                    Application.passwordChanged();
                else
                {
                    Boolean badPassword = true;
                    render(badPassword);
                }
            }   
            else
                render();
        }
        else
            Application.index();
    }
    
    public static void passwordChanged() {
        
        render();
    }
    

}