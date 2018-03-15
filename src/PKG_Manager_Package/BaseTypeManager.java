package PKG_Manager_Package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import Action_Execution_Node_Package.PackageInfoManager;


public class BaseTypeManager {
	
	private PackageInfoManager mPackageInfo=new PackageInfoManager();
	private Hashtable<String, Process> mProcessTable=new Hashtable<String, Process>();

	public BaseTypeManager() {
	
	}
	
	public boolean onStartPackage(String packageName)
	{
		String runCommand=mPackageInfo.getPackageRunCommand(mPackageInfo.BASE_TYPE, packageName);
		try {
			Process process=Runtime.getRuntime().exec(runCommand);
			mProcessTable.put(packageName, process);
		}  catch (Exception e) {
			return false;
			}
	 
		
		return true;
	}
	
	public boolean onDestroyPackage(String packageName)
	{
		String killType=mPackageInfo.getPackageKillCommand(mPackageInfo.BASE_TYPE, packageName);
		if(killType.equals(PackageInfoManager.NODE_KILL)==true)
		{
			if(mProcessTable.containsKey(packageName)==true)
			{
				try {
					String graphName=mPackageInfo.getPackageGraphName(mPackageInfo.BASE_TYPE, packageName);
					String killCommand="rosnode kill /"+graphName;
					Runtime.getRuntime().exec(killCommand);		
					
					return true;
					
				} catch (IOException e) {
					return false;
				}
			}
			else
			{
				return false;
			}
			
		}
		else if(killType.equals(PackageInfoManager.PROCESS_KILL)==true)
		{
			if(mProcessTable.containsKey(packageName)==true)
			{
				Process process=mProcessTable.get(packageName);
				process.destroy();				
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
	public void setDeleteTable(String packageName)
	{
		mProcessTable.remove(packageName);
	}
	public boolean getActiveState(String packageName)
	{
		String graphName="/";
		graphName=graphName+mPackageInfo.getPackageGraphName(mPackageInfo.BASE_TYPE, packageName);

		try {
			Process process=Runtime.getRuntime().exec("rosnode list");
			BufferedReader screen=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String screenStr=new String();
			Hashtable<String,Boolean> screenNodeMap=new Hashtable<String,Boolean>();
			while((screenStr=screen.readLine())!=null)
			{
				screenNodeMap.put(screenStr, true);
			}
			
			if(screenNodeMap.containsKey(graphName)==true)
			{
				screen.close();
				return true;
			}

		} catch (Exception e) {

			return false;
		}
		
		return false;

	}
	
	
	

}
