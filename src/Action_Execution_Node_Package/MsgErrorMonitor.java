package Action_Execution_Node_Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;

import Action_Execution_Node_Package.ActionExecution.MsgInfo;
import PKG_Manager_Package.ProcessStateDef;
import Package_Info_Package.PackageInfo;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;
import action_execution_msgs.Command_result;

public class MsgErrorMonitor {
	
	
	
	private MessageFactory mMessageFactory;
	
	
	public MsgErrorMonitor(ConnectedNode connectedNode) {
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
	}
	
	
	
	public ArrayList<String> getTimeOutPkgNameList(MsgInfo msgInfo)
	{
		ArrayList<String> timeOutList=new ArrayList<String>();
		long timeDiff=System.currentTimeMillis()-msgInfo.StartTime;
		
		for(action_execution_msgs.Package packageMsg: msgInfo.CommandList.getPackageList())
		{
			String pkgName=packageMsg.getPackageName();
			System.out.println(pkgName+"::diff: "+timeDiff);
			int timeOut=getExecutionTimeOut(pkgName);
			if((timeOut!=-1) && timeOut<timeDiff)
			{
				if(isInResultList(pkgName, msgInfo.CommandResultListInput)==false)
				{
					timeOutList.add(pkgName);
				}
			}	
		}
		
		return timeOutList;
			
	}
	 


	private boolean isInResultList(String pkgName, Vector<Command_result> commandResultListInput)
	{
		int numOfResult=commandResultListInput.size();
		for(int index=0; index<numOfResult; index++)
		{
			if(commandResultListInput.get(index).getPackageName().equals(pkgName))
				return true;
		}
		
		return false;
	}
	
	
	
	private int getExecutionTimeOut(String pkgName)
	{
		PackageInfoManager packageInfo=PackageInfoManager.getInstance();
		PackageInfoDef.RootType type=packageInfo.getType(pkgName);
		if(type.equals(PackageInfoDef.RootType.COMMON))
		{
			PackageInfo.CommonPackageInfo info=packageInfo.getCommonPkgInfo(pkgName);
			return info.executionTimeOut;
		}
		else if(type.equals(PackageInfoDef.RootType.LAUNCH))
		{
			PackageInfo.LaunchPackageInfo info=packageInfo.getLaunchPkgInfo(pkgName);
			return info.executionTimeOut;
		}
		
		return -1;
		
	}
	
	
	
	

}
