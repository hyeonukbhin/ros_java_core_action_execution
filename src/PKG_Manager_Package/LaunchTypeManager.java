package PKG_Manager_Package;


import java.util.ArrayList;
import java.util.Hashtable;

import org.omg.CORBA.TIMEOUT;
import org.ros.node.ConnectedNode;

import PKG_Manager_Package.ProcessStateDef.State;
import Package_Info_Package.PackageInfo;
import Package_Info_Package.PackageInfoManager;
import Package_Info_Package.PackageInfo.InterfacePackageInfo;
import Package_Info_Package.PackageInfo.SubNodeInfo;



public class LaunchTypeManager extends IControlFunction{

	private Hashtable<String, Process> mProcessTable;
	private PackageInfoManager mLaunchInfoManager;
	
	private static LaunchTypeManager mInstance=new LaunchTypeManager();
	private InterfaceTypeManaer mInterfaceManager;
	
	private final int COMMAND_TIME=500;
	
	public LaunchTypeManager() {
		mProcessTable=new Hashtable<String, Process>();
		mInterfaceManager=InterfaceTypeManaer.getInstance();
	}
	
	static public LaunchTypeManager getInstance()
	{
		if(mInstance==null)
			mInstance=new LaunchTypeManager();
		
		return mInstance;
	}


	@Override
	public boolean onInitialize(ConnectedNode connectedNode) {
		
		mLaunchInfoManager=PackageInfoManager.getInstance();
		
		return true;
	}
	
	@Override
	public boolean onBoot(String id) {
		try{
			PackageInfo.LaunchPackageInfo info=mLaunchInfoManager.getLaunchPkgInfo(id);
			boolean bootType=info.boot;
			if(bootType==true)
			{
				return onStart(id);
			}
		}catch(Exception e)
		{
			
		}
		
		return true;
	}

	
	@Override
	public boolean onStart(String id) {
		try{
			PackageInfo.LaunchPackageInfo info=mLaunchInfoManager.getLaunchPkgInfo(id);
			State nodeState=onGetAtciveState(id);
			
			if(nodeState.equals(State.PROCESS_ON))
			{				
				return true;
			}	
			else if(nodeState.equals(State.PROCESS_OFF))
			{
				if(mInterfaceManager.onStart(info.interfaceId)==false)
					return false;
				
				Process process=setCommand(info.runCommand);
				mProcessTable.put(info.id, process);
				
				Thread.sleep(info.loadTimeOut);
				
				return onGetAtciveState(id).equals(State.PROCESS_ON);
			}
			else 
			{
				return onRecovery(id);
			}
		}catch(Exception e)
		{
			return false;
		}
		
	}
	@Override
	public boolean onExecute(String id, action_execution_msgs.Package msg) {
		
		try{
			PackageInfo.LaunchPackageInfo info=mLaunchInfoManager.getLaunchPkgInfo(id);
			
			if(onStart(id)==true)
			{
				return mInterfaceManager.onExecute(info.interfaceId, msg);
			}
			
		}catch(Exception e)
		{
			return false;
		}
		
		return false;
	}
	@Override
	public boolean onDestroy(String id) {
		try{
			PackageInfo.LaunchPackageInfo info=mLaunchInfoManager.getLaunchPkgInfo(id);
			State nodeState=onGetAtciveState(id);
			if(nodeState != State.PROCESS_OFF)
			{
				if(mInterfaceManager.onDestroy(info.interfaceId)==true)
				{
					setProcessKill(mProcessTable.get(info.id));	
					mProcessTable.remove(id);
					Thread.sleep(COMMAND_TIME);
				}
				
			}

		}catch(Exception e)
		{
			return false;
		}
		
		return onGetAtciveState(id).equals(State.PROCESS_OFF);
	}
	@Override
	public boolean onRecovery(String id) {
		try{
			PackageInfo.LaunchPackageInfo info=mLaunchInfoManager.getLaunchPkgInfo(id);
			if((mInterfaceManager.onDestroy(info.interfaceId)==true) && (onDestroy(id)==true))
			{
				if((mInterfaceManager.onStart(info.interfaceId)==true) && (onStart(id)==true))
					return true;
			}
			
		}catch(Exception e)
		{
			return false;
		}
		
		return false;
	}
	@Override
	public State onGetAtciveState(String id) {
		
		try{
			int numOfCheckedNode=0;
			PackageInfo.LaunchPackageInfo info=mLaunchInfoManager.getLaunchPkgInfo(id);
		
			for(String subNodeName:info.subNodeGraphNameList)
			{
				if(isActiveNode(subNodeName)==true)
					numOfCheckedNode=numOfCheckedNode+1;
			}
						
			State interfaceState=mInterfaceManager.onGetAtciveState(info.interfaceId);
			if(interfaceState.equals(State.PROCESS_OFF) || interfaceState.equals(State.NONE_TAG))
			{
				if(numOfCheckedNode==0)
					return State.PROCESS_OFF;
			}
			
			if(interfaceState.equals(State.PROCESS_ON)||interfaceState.equals(State.NONE_TAG))
			{
				if(numOfCheckedNode==info.subNodeGraphNameList.size())
					return State.PROCESS_ON;
			}

		}catch(Exception e)
		{
			return State.PROCESS_ERROR;
		}
		
		return State.PROCESS_ERROR;

	}


	
	
	
}
