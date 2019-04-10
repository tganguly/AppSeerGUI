package appseer;

import java.util.List;

import appseer.VulnerabilityFinder.MethodDeclaration;

public class FileCursor {
	
	private List<String> hierarchy;
	private int state;
	
	//File we are currently pointing in
	private String currFile;
	private int currIndex;
	private MethodDeclaration currMethod;
	
	public FileCursor(List<String> H, MethodDeclaration source){
		
		this.hierarchy = H;
		this.state = VulnerabilityFinder.VULNERABLE;
		this.currIndex = hierarchy.size()-1;
		this.currFile = hierarchy.get(currIndex);
		this.currMethod = source;
		
	}
	
	/**
	 * 
	 * Getter methods
	 * 
	 */
	public String getFile(){
		return this.currFile;
	}
	
	public int getState(){
		return this.state;
	}
	
	public MethodDeclaration getMethod(){
		return this.currMethod;
	}
	
	public List<String> getHierarchy(){
		return this.hierarchy;
	}
	
	public int getIndex(){
		return this.currIndex;
	}
	
	/**
	 * 
	 * Primitives to traverse the hierarchy.
	 * 
	 */
	
	public void climbHierarchy(){
		this.currIndex--;
		this.currFile = this.hierarchy.get(currIndex);
	}
	
	public void downHierarchy(){		
		this.currIndex++;
		this.currFile = this.hierarchy.get(currIndex);
	}
	
	/**
	 * Setter method to modify the method the FileCursor is currently looking for in this.currFile
	 * 
	 * @param newMethod
	 */
	
	public void setCurrMethod(MethodDeclaration newMethod){
		this.currMethod = newMethod;
	}
	
	public void setState(int newState){
		this.state = newState;
	}

}
