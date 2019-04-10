package appseer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ManifestFetcher {
	
	//Constants
	public static String APP_ROOT;
	public static String PKG_ROOT;
	public static final String RESOURCES = "resources/";
	public static final String SOURCES = "sources/";
	public static final String MANIFEST = "AndroidManifest.xml";
	/*These variables contain the path to the Android source code. SDK_ROOT is the location of every java source used by every
	 *standard application. ANDROID_ROOT is the path to the entire AOSP source, whose classes are sometimes used by system apps.
	 */
	public static String SDK_ROOT;// = "/Users/Vincenzo/Library/Android/sdk/sources/android-26/";
	public static String ANDROID_ROOT;// = "/Users/Vincenzo/Desktop/Android/packages/apps/";
	private HashMap<String, String> systemApps = new HashMap<>();
	
	private String appName;
	private static String pkgName;
	
	//Manifest File
	private File manifest;
	
	//SAX fields
	private SAXParserFactory factory;
    private SAXParser saxParser;
    private ManifestHandler userhandler;
	
	//List of exported components requested
	List<Component> exportedComponents;
	
	public ManifestFetcher(String appName, Component.COMPONENT_TYPE[] components, String sdk, String android, String jadx){
		
		this.appName = appName;
		populateHashMap();
		//Locations of Android and AOSP source files
		SDK_ROOT = sdk;
		ANDROID_ROOT = android;
		//Location where the obfuscated source codes and app resources from the reverse engineering step are stored.
		APP_ROOT = jadx+"/"+this.appName+"/";
		
		if(!isSystemApp())
			PKG_ROOT = APP_ROOT+SOURCES;
		else
			PKG_ROOT = ANDROID_ROOT+firstUpperLetter(this.appName)+"/src/com/android/"+this.appName+"/";
		
		manifest = new File(APP_ROOT+RESOURCES+MANIFEST);
		
		//File exists check performed in bash
		/*if(!manifest.exists()){
			System.out.println("Error, AndroidManifest.xml for "+appName+" not found.");
			System.exit(-1);
		}*/
		
		try{
			
	        factory = SAXParserFactory.newInstance();
	        saxParser = factory.newSAXParser();
	        userhandler = new ManifestHandler(components);
	        
		} catch (Exception e){
			
			System.out.println(e.getMessage() + " - aborting");
			
		}
		
		exportedComponents = new ArrayList<Component>();
		
	}
	
	public void populateHashMap(){
		
		this.systemApps.put("settings", "Settings");
		this.systemApps.put("contacts", "Contacts");
		this.systemApps.put("phone", "Phone");
		this.systemApps.put("browser", "Browser");
		this.systemApps.put("camera", "Camera2");
		this.systemApps.put("browser", "Browser2");
		//this.systemApps.put("chrome", "Chrome");
		this.systemApps.put("maps", "Maps");
		this.systemApps.put("photos", "Photos");
		this.systemApps.put("music", "Music2");
		this.systemApps.put("videos", "Videos");
		//this.systemApps.put("youtube", "YouTube");
		
	}
	
	/**
	 * 
	 * Utility method to check if the given app is a system app
	 * 
	 */
	public boolean isSystemApp(){
		
		return systemApps.containsKey(this.appName);
		
	}
	
	
	public String firstUpperLetter(String s){
		
		return this.systemApps.get(s);
		
	}
	
	/**
	 * 
	 * Parse Manifest file
	 * @throws IOException 
	 * @throws SAXException 
	 * 
	 */
	
	public void parseXML() throws SAXException, IOException{
		
		saxParser.parse(manifest, userhandler);  
		
	}
	
	/**
	 * 
	 * Add newComp to the list of exported components
	 * 
	 * @param newComp
	 */
	
	public Component addNewComponent(Component newComp){
		this.exportedComponents.add(newComp);
		return exportedComponents.get(exportedComponents.size()-1);
	}
	
	/**
	 * 
	 * Returns the last added component to populate its intent filter(s)
	 * 
	 * @return
	 */
	
	public Component getLastAddedComponent(){
		return this.exportedComponents.get(exportedComponents.size()-1);
	}
	
	/**
	 * 
	 * Return the list of exported components of a given type
	 * 
	 *
	 */
	public List<Component> getExportedComponentsOfType(Component.COMPONENT_TYPE t){
		List<Component> l = new ArrayList<>();
		for(Component c : this.exportedComponents){
			if(c.getType().compareTo(t) == 0)
				l.add(c);
		}
		return l;
	}
	
	/**
	 * 
	 * Checks the presence of the main activity in the component list
	 * 
	 * @return
	 */
	
	public boolean isMainActivityPresent(){
		
		return getExportedComponentsOfType(Component.COMPONENT_TYPE.A).size() > 0;
		
	}
	
	/**
	 * 
	 * Dumps exportedComponents
	 *
	 */
	public void dumpExportedComponents(){
		
		if(exportedComponents.size() > 0){
			
			System.out.println("Found "+(exportedComponents.size()-1)+" exported components:");
			for(Component c : exportedComponents){
				if(c.getType() != Component.COMPONENT_TYPE.A)
					System.out.println(c.toString());
			}
			
		} else 
			System.out.println("No exported components found.");
	}
	
	/**
	 * 
	 * Getter for pkgName 
	 *
	 *
	 */
	public static String getPackageName(){
		return pkgName;
	}
	
	/**
	 * 
	 * Model for a manifest component
	 *
	 */
	public static class Component{
		
		/**
		 * 
		 *A -> Activity, R -> Receiver, P -> Provider, S -> Service
		 *
		 */
		public enum COMPONENT_TYPE{A, R, P, S};
		
		private COMPONENT_TYPE type;
		private String fullName;
		private List<HashMap<String, ArrayList<String>>> intentFilter;
		private String permission;
		
		
		public Component(COMPONENT_TYPE type, String name, String permission){
			
			this.type = type;
			this.fullName = name;
			this.permission = permission;
			this.intentFilter = new ArrayList<>();
					
		}
		
		/**
		 * 
		 * Creates a new hashmap in the intent filters of this component and returns its key
		 * @return
		 */
		public int addIntentFilter(){
			
			int key = intentFilter.size();
			intentFilter.add(key, new HashMap<String, ArrayList<String>>());
			
			return key;
			
		}
		
		/**
		 * Adds a new filter of type filterType to the intent filter at key
		 * 
		 * @param key
		 * @param filterType
		 * @param filterValue
		 */
		
		public void addFilter(int key, String filterType, String filterValue){
			
			HashMap<String, ArrayList<String>> currFilter = this.intentFilter.get(key);
			ArrayList<String> filterList = currFilter.get(filterType);
			if(filterList == null)
				filterList = new ArrayList<String>();
			
			filterList.add(filterValue);
			currFilter.put(filterType, filterList);
			
		}
		
		/**
		 * Getter methods
		 * 
		 */
		public String getName(){
			return this.fullName;
		}
		
		public boolean requiresPermission(){
			return (this.permission != null);
		}
		
		public Component.COMPONENT_TYPE getType(){
			return this.type;
		}
		
		@Override
		public String toString(){
			
			StringBuffer signature = new StringBuffer("Component:\n");
			signature.append("\tName: "+this.fullName+"\n");
			signature.append("\tType: "+this.type.name()+"\n");
			signature.append("\tPermission: "+((this.permission == null) ? "none" : this.permission)+"\n");
			signature.append("\tIntent Filters:");
			
			if(intentFilter.size() == 0)
				signature.append(" none\n");
			else{
				signature.append("\n");
				for(HashMap<String, ArrayList<String>> m : intentFilter){
					
					for(String s : m.keySet()){
						for(String v : m.get(s))
							signature.append("\t\t"+s+" : "+v+"\n");
					}
					
				}
				
			}
			
			return signature.toString();
			
		}
		
	}

	private class ManifestHandler extends DefaultHandler{
		
		//Constants for tags
		private static final String MANIFEST = "manifest";
		private static final String ACTIVITY = "activity";
		private static final String ACTIVITY_ALIAS = "activity-alias";
		private static final String RECEIVER = "receiver";
		private static final String PROVIDER = "provider";
		private static final String SERVICE = "service";
		private static final String INTENT_FILTER = "intent-filter";
		private static final String INTENT_ACTION = "action";
		private static final String INTENT_CATEGORY = "category";
		
		//Constants for attributes
		private static final String PACKAGE = "package";
		private static final String ANDROID = "android:";
		private static final String ANDROID_NAME = ANDROID+"name";
		private static final String ANDROID_EXPORTED = ANDROID+"exported";
		private static final String ANDROID_ENABLED = ANDROID+"enabled";
		private static final String ANDROID_PERMISSION = ANDROID+"permission";
		
		//Constant for Intent.ACTION_MAIN and Category Launcher
		private static final String ACTION_MAIN = "android.intent.action.MAIN";
		private static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
		
		//Boolean values to look only for the requested component types
		private boolean findActivities;
		private boolean findReceivers;
		private boolean findProviders;
		private boolean findServices;
		
		//Boolean values to track which component has been found
		private boolean bActivity;
		private boolean bReceiver;
		private boolean bProvider;
		private boolean bService;
		
		//Boolean value to track if a component or an intent filter are being added
		private boolean bComponent;		
		private boolean bIntentFilter;
		
		//Need to store the attribute values of each activity found until the main one
		private String name;
		private String permission;
		private boolean isMainAction;
		private boolean isMainCategory;
		
		//Current tags values
		private String enabled;
		private String exported;
		private String action;
		private String category;
		private Component.COMPONENT_TYPE c;
		
		//Current index of the intent filter being populated
		private int currFilter;
		private Component currentComponent;
		
		public ManifestHandler(Component.COMPONENT_TYPE[] types){
			
			super();
			
			for(Component.COMPONENT_TYPE c : types){
				
				if(c.compareTo(Component.COMPONENT_TYPE.A) == 0)
					findActivities = true;
				else if(c.compareTo(Component.COMPONENT_TYPE.R) == 0)
					findReceivers = true;
				else if(c.compareTo(Component.COMPONENT_TYPE.P) == 0)
					findProviders = true;
				else if(c.compareTo(Component.COMPONENT_TYPE.S) == 0)
					findServices = true;
			}
			
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			
			c = null;
			
			if(qName.equalsIgnoreCase(MANIFEST))
				pkgName = attributes.getValue(PACKAGE);
		      
			if (findActivities && (qName.equalsIgnoreCase(ACTIVITY) || qName.equals(ACTIVITY_ALIAS))) {	
				c = Component.COMPONENT_TYPE.A;
				bActivity = true;
			} else if (findReceivers && qName.equalsIgnoreCase(RECEIVER)) {
				c = Component.COMPONENT_TYPE.R;
				bReceiver = true;
			} else if (findProviders && qName.equalsIgnoreCase(PROVIDER)) {
				c = Component.COMPONENT_TYPE.P;
				bProvider = true;
			} else if (findServices && qName.equalsIgnoreCase(SERVICE)) {
				c = Component.COMPONENT_TYPE.S;
				bService = true;
			}
			
			/*If a new component is found, add to components list if:
			 * 1) android:exported == true
			 * 2) android:exported is null but an intent filter is defined
			 * 3) it is the main activity
			 */
			if(!bComponent && (bActivity || bReceiver || bProvider || bService)){
				
				name = attributes.getValue(ANDROID_NAME);
				exported = attributes.getValue(ANDROID_EXPORTED);
				enabled = attributes.getValue(ANDROID_ENABLED);
				permission = attributes.getValue(ANDROID_PERMISSION);
				
				if(name != null){
					
					currentComponent = new Component(c, name, permission);
					bComponent = true;
				
				}
				
			}
				
			if(bComponent && qName.equalsIgnoreCase(INTENT_FILTER)){
				currFilter = currentComponent.addIntentFilter();
				bIntentFilter = true;
			}
				
			if(bIntentFilter && qName.equalsIgnoreCase(INTENT_ACTION)){
					
				action = attributes.getValue(ANDROID_NAME);
				if(bActivity && action.equals(ACTION_MAIN)){
					isMainAction = true;
					currentComponent.addFilter(currFilter, INTENT_ACTION, action);
				}
				
			}
				
			if(bIntentFilter && qName.equalsIgnoreCase(INTENT_CATEGORY)){
				category = attributes.getValue(ANDROID_NAME);
				if(bActivity && category.equals(CATEGORY_LAUNCHER)){
					isMainCategory = true;
					currentComponent.addFilter(currFilter, INTENT_CATEGORY, category);
				}
			
			}
			
	   }
		   
	   @Override
	   public void endElement(String uri, String localName, String qName) throws SAXException {
		   
		 //bReceiver = bProvider = bService = false;
		  
		  if(bService && qName.equalsIgnoreCase(SERVICE)){
			  
			  if((enabled == null || (enabled != null && enabled.equals("true"))) &&
					  ((exported == null && bIntentFilter == true) || (exported != null && exported.equals("true"))))
				  addNewComponent(currentComponent);
			  
			  bService = false;
			  bIntentFilter = false;
			  enabled = null;
			  exported = null;	
			  bComponent = false;
			  
		  }
		  
		  if(bActivity && qName.equals(ACTIVITY)){
			  
			  if(isMainAction && isMainCategory){
				  addNewComponent(currentComponent);
				  findActivities = false; //since we found the main activity, no need to look for another one
			  }

			  bActivity = false;
			  bIntentFilter = false;
			  bComponent = false;
			  
		  }
	      
	      
	   }
		
	}
}
