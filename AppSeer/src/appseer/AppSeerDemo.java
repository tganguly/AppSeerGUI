package appseer;

import java.util.List;

import appseer.ManifestFetcher.Component;
import appseer.VulnerabilityFinder.MethodDeclaration;

public class AppSeerDemo {
	
	//Application to analyze
	private static String appName;
	
	//Component types to look for
	private static Component.COMPONENT_TYPE[] componentTypes = {Component.COMPONENT_TYPE.A, Component.COMPONENT_TYPE.S};
	
	//MethodDeclaration objects for source and sink methods
	private static MethodDeclaration onStartCmd = new MethodDeclaration("public", "int", "onStartCommand", "Intent i, int j, int k");
	private static MethodDeclaration onHandleInt = new MethodDeclaration("protected abstract", "void", "onHandleIntent", "Intent i");
	private static MethodDeclaration startFg = new MethodDeclaration("public final", "void", "startForeground", "int id, Notification notification");
			
	public static void main(String[] args) {
		
		List<Component> exportedServices;
		MethodDeclaration source, sink;
		ManifestFetcher fetcher;
		HierarchyBuilder builder;
		VulnerabilityFinder vf;
		
		if(args.length != 5){
			System.out.println("Usage: java -cp classPath appseer.AppSeerDemo appName SDK_ROOT ANDROID_ROOT OUTPUT_DIR JADX_OUT");
			return;
		}
		
		appName = args[0];
		
		//MODULE 2: ManifestFetcher
		fetcher = new ManifestFetcher(appName, componentTypes, args[1], args[2], args[4]);
		try{
			fetcher.parseXML();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		//Output: list of exported services
		System.out.println("###### Components analysis for Application: "+appName+" ######\n");
		fetcher.dumpExportedComponents();
		
		//Check if the fetcher managed to extract the main activity, too
		exportedServices = fetcher.getExportedComponentsOfType(Component.COMPONENT_TYPE.S);
		Component mainActivity = fetcher.isMainActivityPresent() ? fetcher.getExportedComponentsOfType(Component.COMPONENT_TYPE.A).get(0) : null;
		
		//MODULE 3: Source-sink search
		//Create a HierarchyBuilder object for the given app
		builder = new HierarchyBuilder(appName);
		//Create a VulnerabilityFinder object for the given app
		vf = new VulnerabilityFinder(appName, args[3]);
		vf.writeMainActivityToOutput(mainActivity);
		
		if(exportedServices.size() > 0){
		
			System.out.println("\n\n###### Vulnerability analysis for Application: "+appName+" ######\n");
			
			//Analyze every retrieved component
			for(Component c : exportedServices){
				
				//Build the hierarchy of the current service
				builder.setSourceFile(c.getName());
				builder.buildHierarchy();
				//b.dumpHierarchy();
				
				//Set source method according to the top class in the hierarchy
				source = builder.getTopClass().equals("Service") ? onStartCmd : onHandleInt;
				sink = startFg;
				vf.setMethods(source, sink);
				
				vf.setHierarchy(builder.getHierarchy(), c.requiresPermission());
				
				//Perform the analysis
				vf.evaluateComponent();
				
			}
			
		}
		
		vf.closeOutput();
		
	}
	
}
