package Action_Execution_Node_Package;

import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class PackageInfoManager { 
	
	private final String FILE_NAME="Config/ActionExecutionConfig.json";
	private JSONObject mFileObject=new JSONObject();	
	private String mPackageTypeList[]={"CommonPackage", "LaunchPackage", "BasePackage"};
	
	private final String NONE_OPTION="NONE";
	public static final String LAUNCH_TYPE="LaunchPackage";
	public static final String BASE_TYPE="BasePackage";
	public static final String COMMON_TYPE="CommonPackage";
	
	public static final String NODE_KILL="NODE_KILL";
	public static final String PROCESS_KILL="PROCESS_KILL";
	
	public PackageInfoManager() 
	{
		onLoadFile(FILE_NAME);
	}

	
	
	public String getPackageType(String name)
	{
		JSONArray launchTypePackageList=(JSONArray)mFileObject.get(LAUNCH_TYPE);
		if(launchTypePackageList != null)
		{
			for(int indexOfPackage=0; indexOfPackage<launchTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)launchTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					return LAUNCH_TYPE;
				}		 
			}
		}
		
		
		JSONArray baseTypePackageList=(JSONArray)mFileObject.get(BASE_TYPE);
		if(baseTypePackageList != null)
		{
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					return BASE_TYPE;
				}		 
			}
		}
		

		JSONArray commonTypePackageList=(JSONArray)mFileObject.get(COMMON_TYPE);
		if(commonTypePackageList != null)
		{
			for(int indexOfPackage=0; indexOfPackage<commonTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)commonTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					return COMMON_TYPE;
				}		 
			}
		}
		

		return null;
	}

	
	public String getPackageRunCommand(String packageType, String name)
	{
		if(packageType.equals(BASE_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(BASE_TYPE);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String runCommand=packageObj.get("PackageRunCommand").toString();
					if(runCommand.equals(NONE_OPTION)==false)
					{
						return runCommand;
					}
					else
					{
						//option->NONE
						
					}
					
				}		 
			}
		}
		else if(packageType.equals(LAUNCH_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(LAUNCH_TYPE);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String runCommand=packageObj.get("PackageRunCommand").toString();
					return runCommand;
					
				}		 
			}
		}
		
		return null;
	}
	
	public String getInterfaceTopicName(String packageType, String name)
	{
	   if(packageType.equals(LAUNCH_TYPE)==true || packageType.equals(COMMON_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(packageType);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String topicName=packageObj.get("InterfaceTopicName").toString();
					return topicName;
					
				}		 
			}
		}
		
		return null;
	}
	
	public String getInterfaceRunCommand(String packageType, String name)
	{
	   if(packageType.equals(LAUNCH_TYPE)==true || packageType.equals(COMMON_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(packageType);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String runCommand=packageObj.get("InterfaceRunCommand").toString();
					return runCommand;
					
				}		 
			}
		}
		
		return null;
	}
	
	public String getInterfaceGraphName(String packageType, String name)
	{
	   if(packageType.equals(LAUNCH_TYPE)==true || packageType.equals(COMMON_TYPE)==true )
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(packageType);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String graphName=packageObj.get("InterfaceGraphName").toString();
					return graphName;
					
				}		 
			}
		}
		
		return null;
	}

	
	public String getPackageKillCommand(String packageType, String name)
	{
		if(packageType.equals(BASE_TYPE)==true || packageType.equals(LAUNCH_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(packageType);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String killCommand=packageObj.get("PackageKillCommand").toString();
					return killCommand;
					
				}		 
			}
		}
		return null;
	}
	
	public String getInterfaceKillCommand(String packageType, String name)
	{
		if(packageType.equals(LAUNCH_TYPE)==true || packageType.equals(COMMON_TYPE))
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(packageType);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String killCommand=packageObj.get("InterfaceKillCommand").toString();
					return killCommand;
					
				}		 
			}
		}
		return null;
	}
	
	public String getPackageGraphName(String packageType, String name)
	{
		if(packageType.equals(BASE_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(BASE_TYPE);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String graphName=packageObj.get("PackageGraphName").toString();
					if(graphName.equals(NONE_OPTION)==false)
					{
						return graphName;
					}
					else
					{
						//option->NONE
						
					}
					
				}		 
			}
		}
		
		return null;
	}
	
	public int getTimeOut(String packageType, String name)
	{
		if(packageType.equals(BASE_TYPE)==true || packageType.equals(LAUNCH_TYPE)==true || packageType.equals(COMMON_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(packageType);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					String timeOut=packageObj.get("TimeOut").toString();
					return Integer.parseInt(timeOut);
				}		 
			}
		}
		
		return 0;
	}
	
	public Vector<String> getSubscriberNodeGraphNameList(String packageType, String name)
	{
		Vector<String> resultList=new Vector<String>();
		if(packageType.equals(LAUNCH_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(LAUNCH_TYPE);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					JSONArray SubscriberList=(JSONArray)packageObj.get("SubscriberList");
					for(int indexOfSubscriber=0; indexOfSubscriber<SubscriberList.size(); indexOfSubscriber++)
					{
						JSONObject subscriberObj=(JSONObject)SubscriberList.get(indexOfSubscriber);
						resultList.add(subscriberObj.get("NodeGraphName").toString());
					}
					
				}		 
			}
		}
		return resultList;
	}
	
	public Vector<String> getNodeGraphNameList(String packageType, String name)
	{
		Vector<String> resultList=new Vector<String>();
		if(packageType.equals(COMMON_TYPE)==true)
		{
			JSONArray commonTypePackageList=(JSONArray)mFileObject.get(COMMON_TYPE);
			for(int indexOfPackage=0; indexOfPackage<commonTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)commonTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					JSONArray nodeList=(JSONArray)packageObj.get("NodeList");
					for(int indexOfNode=0; indexOfNode<nodeList.size(); indexOfNode++)
					{
						JSONObject nodeObj=(JSONObject)nodeList.get(indexOfNode);
						resultList.add(nodeObj.get("NodeGraphName").toString());
					}
					
				}		 
			}
		}
		return resultList;
	}
	
	public Vector<String> getNodeRunCommandList(String packageType, String name)
	{
		Vector<String> resultList=new Vector<String>();
		if(packageType.equals(COMMON_TYPE)==true)
		{
			JSONArray commonTypePackageList=(JSONArray)mFileObject.get(COMMON_TYPE);
			for(int indexOfPackage=0; indexOfPackage<commonTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)commonTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					JSONArray SubscriberList=(JSONArray)packageObj.get("NodeList");
					for(int indexOfSubscriber=0; indexOfSubscriber<SubscriberList.size(); indexOfSubscriber++)
					{
						JSONObject subscriberObj=(JSONObject)SubscriberList.get(indexOfSubscriber);
						resultList.add(subscriberObj.get("NodeRunCommand").toString());
					}
					
				}		 
			}
		}
		return resultList;
	}
	
	public Vector<String> getNodeKillCommandList(String packageType, String name)
	{
		Vector<String> resultList=new Vector<String>();
		if(packageType.equals(COMMON_TYPE)==true)
		{
			JSONArray commonTypePackageList=(JSONArray)mFileObject.get(COMMON_TYPE);
			for(int indexOfPackage=0; indexOfPackage<commonTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)commonTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					JSONArray SubscriberList=(JSONArray)packageObj.get("NodeList");
					for(int indexOfSubscriber=0; indexOfSubscriber<SubscriberList.size(); indexOfSubscriber++)
					{
						JSONObject subscriberObj=(JSONObject)SubscriberList.get(indexOfSubscriber);
						resultList.add(subscriberObj.get("NodeKillCommand").toString());
					}
					
				}		 
			}
		}
		return resultList;
	}
	

/*	
//DUMMPY
	public Vector<String> getDependencyList(String packageType, String name)
	{
		Vector<String> resultList=new Vector<String>();
		if(packageType.equals(BASE_TYPE)==true)
		{
			JSONArray baseTypePackageList=(JSONArray)mFileObject.get(BASE_TYPE);
			for(int indexOfPackage=0; indexOfPackage<baseTypePackageList.size(); indexOfPackage++)  
			{
				JSONObject packageObj=(JSONObject)baseTypePackageList.get(indexOfPackage);
				if(packageObj.get("PackageName").toString().equals(name))
				{
					JSONArray dependencyLiist=(JSONArray)packageObj.get("Dependency");
					for(int indexOfDependency=0; indexOfDependency<dependencyLiist.size(); indexOfDependency++)
					{
						resultList.add(dependencyLiist.get(indexOfDependency).toString());
					}
					
				}		 
			}
		}
		return resultList;
	}
*/	
	
	
	private void onLoadFile(String fileName)
	{
		try {
			JSONParser parser=new JSONParser();
			Object obj=parser.parse(new FileReader(fileName));	
			mFileObject=(JSONObject)obj;
	
		} catch (Exception e) {
			System.out.println("File Load Error: "+fileName);
		}
	}

}
