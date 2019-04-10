package appseer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HierarchyBuilder {
	
	//Constant sink classes
	private static final String serviceClass = "Service";
	private static final String intentServiceClass = "IntentServiceClass";
	private static final String serviceClassPath = "android/app/Service.java";
	private static final String intentServiceClassPath = "android/app/IntentService.java";

	//Constant root directories
	private String pkgRoot;
	private String sdkRoot;
	private static List<String> androidSdk = new ArrayList<>();
	
	//Hierarchy
	private List<String> hierarchy;
	
	//Regex patterns
	private static final Pattern pkgPattern = Pattern.compile("^package (.*);");
	private static final Pattern importPattern = Pattern.compile("^import (.*);");
	private static final Pattern classPattern = Pattern.compile("^((public|private|protected|package|static|final|abstract) )*class (.*)\\{");
	private static final Pattern extendsPattern = Pattern.compile("(.*) extends ([._a-zA-Z0-9]*) .*");
	private Matcher pkgMatcher;
	private Matcher importMatcher;
	private Matcher classMatcher;
	private Matcher extendsMatcher;
	
	private String appName;
	
	//Filename whose hierarchy is being built
	private String source;
	private String sourcePath;
	
	//appName is null if we are analyzing an sdk java file
	public HierarchyBuilder(String appName){
		
		pkgRoot = ManifestFetcher.PKG_ROOT;
		sdkRoot = ManifestFetcher.SDK_ROOT;
		androidSdk.add("android");
		androidSdk.add("java");
		androidSdk.add("org");
		
		this.appName = appName;
		
		pkgMatcher = pkgPattern.matcher("");
		importMatcher = importPattern.matcher("");
		classMatcher = classPattern.matcher("");
		extendsMatcher = extendsPattern.matcher("");
		
	}
	
	/**
	 * 
	 * Setter for source file to reuse the same Builder throughout the same app
	 * 
	 */
	public void setSourceFile(String newSource){
		
		this.source = getPathFromPackage(newSource)+".java";
		this.sourcePath = (appName.equals("system")) ? sdkRoot + this.source : pkgRoot + this.source;
		this.hierarchy = new ArrayList<>();
	
	}
	
	/**
	 * 
	 * Utility method to convert a dot separated path into a slash separated one used to look for the desired file
	 * 
	 * @param line
	 * @return
	 */
	public String getPathFromPackage(String line){
		
		if(line.charAt(0) == '.')
			line = line.substring(1);
		
		String pkgPath = line.replace('.', '/');
		return pkgPath;
		
	}
	
	/**
	 * 
	 * Utility method to check if file is in the directory pointed by path
	 * 
	 * @param file
	 * @param path
	 * @return
	 */
	public boolean isInDirectory(String file, String path){
		
		File directory = new File(path);
		for(File f : directory.listFiles()){
			
			if(f.getName().equals(file))
				return true;
			
		}
		
		return false;
		
	}
	
	/**
	 * 
	 * Calls getSuperClassFromSource with the given source
	 * 
	 */
	public int buildHierarchy(){
		
		int r = getSuperClassFromSource(this.sourcePath);
		hierarchy.add(sourcePath);
		return r;
		
	}
	
	/**
	 * Recursively find the hierarchy of a given source
	 * 
	 * 
	 * @param currClass: absolute path of the starting java source
	 * @return 1 if hierarchy is completed, 0 otherwise
	 * 
	 */
	public int getSuperClassFromSource(String currClass){
		
		String pkgName = null, superClass, line, accessLevel = null, classLine = null;
		List<String> imports = new ArrayList<>();
		
		//Start reading the file line by line until the class declaration is found
		File currFile = new File(currClass);
		FileReader fr;
		BufferedReader br;
		
		try{
			
			fr = new FileReader(currFile);
			br = new BufferedReader(fr);
			
			while((line = br.readLine()) != null){
				
				pkgMatcher.reset(line);
				importMatcher.reset(line);
				classMatcher.reset(line);
				
				if(pkgMatcher.matches()){
					
					pkgName = pkgMatcher.group(1);
					
				} else if(importMatcher.matches()){

					imports.add(importMatcher.group(1));
			
				} else if(classMatcher.matches()){
					
					accessLevel = classMatcher.group(2);
					classLine = classMatcher.group(3);
					//System.out.println("Analyzing class "+accessLevel+" "+classLine);
					break;
	
				}
				
			}
			
		} catch(Exception e){
			
			System.out.println(e.getMessage() + " - aborting");
			return 0;
			
		}
		
		//Check if currClass extends a SuperClass, and grab its name if so
		String currClassFile;
		
		if(classLine != null){
		
			extendsMatcher.reset(classLine);
			superClass = (extendsMatcher.matches()) ? extendsMatcher.group(2) : null;
			currClassFile = (extendsMatcher.matches()) ? extendsMatcher.group(1)+".java" : null;
			
			//if superClass is null, can return 0
			if(superClass == null){
				try{
					br.close();
					fr.close();
				} catch (Exception e){
					System.out.println(e.getMessage()+" - aborting.");
				}
				return 0;
			}
			
			//if superClass contains a dot, it is as if a new import statement is added
			if(superClass.contains(".")){

				imports.add(superClass);
				String[] steps = superClass.split("\\.");
				superClass = steps[steps.length-1];
				
			}
			
				 
		} else {
			
			System.out.println("Class definition not found, exiting.");
			try{
				fr.close();
				br.close();
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
			return -1;
			
		}
		
		//If the current class is final and it is not the lowest in the hierarchy, it can't be part of a hierarchy leading to a Service
		if(!currClassFile.equals(this.source) && accessLevel != null && accessLevel.contains("final")){
			
			try{
				fr.close();
				br.close();
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
			return 0;
			
		}
		
		//If superClass is either Service or IntentService, we stop
		if(superClass.equals(serviceClass) || superClass.equals(intentServiceClass)){
			
			if(superClass.equals(serviceClass))
				hierarchy.add(sdkRoot+serviceClassPath);
			else if(superClass.equals(intentServiceClass))
				hierarchy.add(sdkRoot+intentServiceClassPath);
			
			try{
				fr.close();
				br.close();
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
			return 1;
			
		}
		
		//Look for superClass.java in package folder
		String superClassFile = superClass+".java";
		String pkgPath = pkgRoot+getPathFromPackage(pkgName)+"/";
		
		//Need to exclude the case in which superClass has the same name of currClass, because they cannot be found in the same package if so 
		if(!superClassFile.equals(currClassFile) && isInDirectory(superClassFile, pkgPath)){
			
			if(getSuperClassFromSource(pkgPath+superClassFile) == 1){
				
				hierarchy.add(pkgPath+superClassFile);
				try{
					fr.close();
					br.close();
				} catch (Exception e){
					System.out.println(e.getMessage());
				}
				return 1;
				
			}
			
		}
		
		//Look for superClass.java in all the import statements that end with superClass
		String[] steps;
		String importedClass, importedClassPath, root;
		
		for(String i : imports){
			
			steps = i.split("\\.");
			//2 special cases: if the imports are of the kind (android|java|org).chromium.*, or com.android.*, apps are importing classes from the Android source, so we need to use sdkRoot
			if((androidSdk.contains(steps[0]) && !steps[1].equals("chromium")) || (steps[0].equals("com") && steps[1].equals("android")))
				root = sdkRoot;
			else
				root = pkgRoot;
			
			importedClassPath = root+getPathFromPackage(i)+".java";
			importedClass = steps[steps.length-1]+".java";
			
			if(importedClass.equals(superClassFile)){
				
				if(getSuperClassFromSource(importedClassPath) == 1){
					hierarchy.add(importedClassPath);
					try{
						fr.close();
						br.close();
					} catch (Exception e){
						System.out.println(e.getMessage());
					}
					return 1;
				}
				
			}
			
		}
		
		return 0;
		
	}
	
	
	/**
	 * 
	 * Prints the computed hierarchy for the given source
	 * 
	 */
	public void dumpHierarchy(){
		
		System.out.println("Dump class hierarchy for " + sourcePath + ":\n");
		
		for(String s : hierarchy)
			System.out.println(s);
		
	}
	
	/**
	 * 
	 * Returns the computed hierarchy
	 * 
	 */
	public List<String> getHierarchy(){
		return this.hierarchy;
	}
	
	/*
	 * Returns the name of the first class of the built hierarchy
	 *
	 */
	public String getTopClass(){
		
		String topClass = this.getHierarchy().get(0);
		String[] steps = topClass.split("/");
		String className = steps[steps.length-1];
		return className.substring(0, className.length()-5);
		
	}
}
