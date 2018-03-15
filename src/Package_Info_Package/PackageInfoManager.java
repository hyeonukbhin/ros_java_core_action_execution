package Package_Info_Package;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class PackageInfoManager { 
	
	private final String FILE_NAME="Config/ActionExecutionConfig.json";
	
	private static PackageInfoManager mInstance=new PackageInfoManager();	
	
	//key: Id, value: rootType={common, launch, base, interface} (in Json)
	private HashMap<String, PackageInfoDef.RootType> mCategoryMap;
	
	//key: Id, value: packageInfo={id, boot, runCommand, ...} (in Json)
	private HashMap<String, PackageInfo.BaseProgramInfo> mBaseProgramInfo;
	private HashMap<String, PackageInfo.LaunchPackageInfo> mLaunchPkgInfo;
	private HashMap<String, PackageInfo.CommonPackageInfo> mCommonPkgInfo;
	private HashMap<String, PackageInfo.InterfacePackageInfo> mInterfacePkgInfo;
	
	private JSONObject mFileObject;	
	private boolean mFileLoad;
	
	public PackageInfoManager() 
	{
		mFileObject=new JSONObject();
		mCategoryMap=new HashMap<String, PackageInfoDef.RootType>();
		mBaseProgramInfo=new HashMap<String, PackageInfo.BaseProgramInfo>();
		mLaunchPkgInfo=new HashMap<String, PackageInfo.LaunchPackageInfo>();
		mCommonPkgInfo=new HashMap<String, PackageInfo.CommonPackageInfo>();
		mInterfacePkgInfo=new HashMap<String, PackageInfo.InterfacePackageInfo>();
		
		mFileLoad=onLoadFile(FILE_NAME);
	}
	
	public static PackageInfoManager getInstance()
	{
		if(mInstance==null)
		{
			mInstance=new PackageInfoManager();
		}
		
		return mInstance;
	}
	
	public boolean isLoadConfigFile()
	{
		return mFileLoad;
	}
	
	public PackageInfoDef.RootType getType(String id)
	{
		return mCategoryMap.get(id);
	}
	
	public PackageInfo.CommonPackageInfo getCommonPkgInfo(String id)
	{
		return mCommonPkgInfo.get(id);
	}
	
	public PackageInfo.LaunchPackageInfo getLaunchPkgInfo(String id)
	{
		return mLaunchPkgInfo.get(id);
	}
	
	
	public PackageInfo.BaseProgramInfo getProgramInfo(String id)
	{
		return mBaseProgramInfo.get(id);
	}
	
	public PackageInfo.InterfacePackageInfo getInterfaceInfo(String id)
	{
		return mInterfacePkgInfo.get(id);
	}
	
	public ArrayList<String> getBootIdList()
	{
		ArrayList<String> bootIdList=new ArrayList<String>();
		ArrayList<String> idList=new ArrayList<String>(mCategoryMap.keySet());
		
		for(String ID : idList)
		{
			PackageInfoDef.RootType type=mCategoryMap.get(ID);
			
			if(type.equals(PackageInfoDef.RootType.COMMON)==true)
			{
				PackageInfo.CommonPackageInfo info=mCommonPkgInfo.get(ID);
				if(info.boot==true)
					bootIdList.add(ID);
			}
			else if(type.equals(PackageInfoDef.RootType.LAUNCH)==true)
			{
				PackageInfo.LaunchPackageInfo info=mLaunchPkgInfo.get(ID);
				if(info.boot==true)
					bootIdList.add(ID);
			}
			else if(type.equals(PackageInfoDef.RootType.PROGRAM)==true)
			{
				PackageInfo.BaseProgramInfo info=mBaseProgramInfo.get(ID);
				if(info.boot==true)
					bootIdList.add(ID);
			}
		
			
		}
		
		return bootIdList;
	}
	
	private PackageInfo.CommonPackageInfo getCommonPkgInfo(JSONObject pkgObject)
	{
		PackageInfo.CommonPackageInfo info=new PackageInfo.CommonPackageInfo();
		info.id=pkgObject.get(PackageInfoDef.ID).toString();
		info.boot=Boolean.valueOf(pkgObject.get("Boot").toString()).booleanValue(); 
		info.interfaceId=pkgObject.get("InterfaceId").toString();
		
		info.loadTimeOut=Integer.valueOf(pkgObject.get("LoadTimeOut").toString()).intValue();
		info.executionTimeOut=Integer.valueOf(pkgObject.get("ExecutionTimeOut").toString()).intValue();
		
		JSONArray preconditionList=(JSONArray)pkgObject.get("Precondition");
		int sizeOfPrecondition=preconditionList.size();
		for(int indexOfPkg=0; indexOfPkg<sizeOfPrecondition; indexOfPkg++)
		{
			info.precondition.add(preconditionList.get(indexOfPkg).toString());
		}
		
		JSONArray effectList=(JSONArray)pkgObject.get("Effect");
		int sizeOfEffect=effectList.size();
		for(int indexOfPkg=0; indexOfPkg<sizeOfEffect; indexOfPkg++)
		{
			info.effect.add(effectList.get(indexOfPkg).toString());
		}
		
		JSONArray subNodeList=(JSONArray)pkgObject.get("SubNodeInfo");
		int sizeOfSubNode=subNodeList.size();
		for(int indexOfNode=0; indexOfNode<sizeOfSubNode; indexOfNode++)
		{
			PackageInfo.SubNodeInfo subNode=new PackageInfo.SubNodeInfo();
			JSONObject subNodeObj=(JSONObject)subNodeList.get(indexOfNode);
			subNode.id=subNodeObj.get("Id").toString();
			subNode.nodeName=subNodeObj.get("GraphName").toString();
			subNode.runCommand=subNodeObj.get("RunCommand").toString();
			subNode.killCommandType=subNodeObj.get("KillCommand").toString();
			info.subNodeInfoList.add(subNode);
		}
		
		return info;
		
		
	}
	
	private PackageInfo.LaunchPackageInfo getLaunchPkgInfo(JSONObject pkgObject)
	{
		PackageInfo.LaunchPackageInfo info=new PackageInfo.LaunchPackageInfo();
		info.id=pkgObject.get(PackageInfoDef.ID).toString();
		info.boot=Boolean.valueOf(pkgObject.get("Boot").toString()).booleanValue(); 
		info.runCommand=pkgObject.get("RunCommand").toString();
		info.interfaceId=pkgObject.get("InterfaceId").toString();
		
						
		info.loadTimeOut=Integer.valueOf(pkgObject.get("LoadTimeOut").toString()).intValue();
		info.executionTimeOut=Integer.valueOf(pkgObject.get("ExecutionTimeOut").toString()).intValue();
		
		JSONArray preconditionList=(JSONArray)pkgObject.get("Precondition");
		int sizeOfPrecondition=preconditionList.size();
		for(int indexOfPkg=0; indexOfPkg<sizeOfPrecondition; indexOfPkg++)
		{
			info.precondition.add(preconditionList.get(indexOfPkg).toString());
		}
		
		JSONArray effectList=(JSONArray)pkgObject.get("Effect");
		int sizeOfEffect=effectList.size();
		for(int indexOfPkg=0; indexOfPkg<sizeOfEffect; indexOfPkg++)
		{
			info.effect.add(effectList.get(indexOfPkg).toString());
		}
		
		JSONArray subNodeList=(JSONArray)pkgObject.get("SubNodeGraphName");
		int sizeOfSubNode=subNodeList.size();
		for(int indexOfNode=0; indexOfNode<sizeOfSubNode; indexOfNode++)
		{
			info.subNodeGraphNameList.add(subNodeList.get(indexOfNode).toString());
		}
		
		return info;
		
	}
	
	
	
	private PackageInfo.BaseProgramInfo getBaseProgramInfo(JSONObject pkgObject)
	{
		PackageInfo.BaseProgramInfo info=new PackageInfo.BaseProgramInfo();
		info.id=info.id=pkgObject.get(PackageInfoDef.ID).toString();
		info.processName=pkgObject.get("ProcessName").toString();
		info.boot=Boolean.valueOf(pkgObject.get("Boot").toString()).booleanValue(); 
		info.loadTimeOut=Integer.valueOf(pkgObject.get("LoadTimeOut").toString()).intValue();
		info.runCommand=pkgObject.get("RunCommand").toString();
		
		JSONArray subProcessList=(JSONArray)pkgObject.get("SubProcess");
		int sizeOfSubProcess=subProcessList.size();
		for(int indexOfSub=0; indexOfSub<sizeOfSubProcess; indexOfSub++)
		{
			PackageInfo.SubProcess subProcess=new PackageInfo.SubProcess();
			JSONObject subProcessObj=(JSONObject)subProcessList.get(indexOfSub);
			subProcess.processName=subProcessObj.get("ProcessName").toString();
			subProcess.numOfprocess=Integer.valueOf(subProcessObj.get("NumOfProcess").toString()).intValue();
			info.subProcessList.add(subProcess);
		}
		
		JSONArray preconditionList=(JSONArray)pkgObject.get("Precondition");
		int sizeOfPrecondition=preconditionList.size();
		for(int indexOfPkg=0; indexOfPkg<sizeOfPrecondition; indexOfPkg++)
		{
			info.precondition.add(preconditionList.get(indexOfPkg).toString());
		}
		
		JSONArray effectList=(JSONArray)pkgObject.get("Effect");
		int sizeOfEffect=effectList.size();
		for(int indexOfPkg=0; indexOfPkg<sizeOfEffect; indexOfPkg++)
		{
			info.effect.add(effectList.get(indexOfPkg).toString());
		}

		return info;
	}
	
	private PackageInfo.InterfacePackageInfo getInterfacePkgInfo(JSONObject pkgObject)
	{
		PackageInfo.InterfacePackageInfo info=new PackageInfo.InterfacePackageInfo();
		info.id=pkgObject.get(PackageInfoDef.ID).toString();
		info.nodeName=pkgObject.get("GraphName").toString();
		info.topicName=pkgObject.get("TopicName").toString();
		info.loadTimeOut=Integer.valueOf(pkgObject.get("LoadTimeOut").toString()).intValue();
		info.runCommand=pkgObject.get("RunCommand").toString();
		info.killCommandType=pkgObject.get("KillCommand").toString();		
		
		return info;
		
		
	}
	
	
	private boolean onLoadMaps(JSONObject fileObject)
	{
		ArrayList<PackageInfoDef.RootType> typeList=PackageInfoDef.getList();
		int sizeOfType=typeList.size();
		for(int indexOfType=0; indexOfType<sizeOfType; indexOfType++)
		{
			String pkgType=typeList.get(indexOfType).getStr();
			JSONArray subCategoryList=(JSONArray)mFileObject.get(pkgType);
			int sizeOfSub=subCategoryList.size();
			for(int indexOfSub=0; indexOfSub<sizeOfSub; indexOfSub++)
			{
				JSONObject packageObj=(JSONObject)subCategoryList.get(indexOfSub);
				String id=packageObj.get(PackageInfoDef.ID).toString();
				
				if(pkgType.equals(PackageInfoDef.RootType.COMMON.getStr())==true)
				{
					PackageInfo.CommonPackageInfo info=getCommonPkgInfo(packageObj);
					mCommonPkgInfo.put(id, info);
					mCategoryMap.put(id, PackageInfoDef.RootType.COMMON);
				}
				else if(pkgType.equals(PackageInfoDef.RootType.LAUNCH.getStr())==true)
				{
					PackageInfo.LaunchPackageInfo info=getLaunchPkgInfo(packageObj);
					mLaunchPkgInfo.put(id, info);
					mCategoryMap.put(id, PackageInfoDef.RootType.LAUNCH);
				}
				else if(pkgType.equals(PackageInfoDef.RootType.PROGRAM.getStr())==true)
				{
					PackageInfo.BaseProgramInfo info=getBaseProgramInfo(packageObj);
					mBaseProgramInfo.put(id, info);
					mCategoryMap.put(id, PackageInfoDef.RootType.PROGRAM);
				}
				else if(pkgType.equals(PackageInfoDef.RootType.INTERFACE.getStr())==true)
				{
					PackageInfo.InterfacePackageInfo info=getInterfacePkgInfo(packageObj);
					mInterfacePkgInfo.put(id, info);
					mCategoryMap.put(id, PackageInfoDef.RootType.INTERFACE);
				}
				else
				{
			        System.out.println("PackageInfoManager::onLoadMaps()...Fail!");
					return false;
				}
					
			}
		}
		
		return true;
		
	}
	

	
	private boolean onLoadFile(String fileName)
	{
		boolean result;
		try {
			JSONParser parser=new JSONParser();
			Object obj=parser.parse(new FileReader(fileName));	
			mFileObject=(JSONObject)obj;
			result=onLoadMaps(mFileObject);
	
		} catch (Exception e) {
			System.out.println("PackageInfoManager::onLoadFile()...Fail!");
			result=false;
		}
		
		return result;
	}
	
	
	


}
