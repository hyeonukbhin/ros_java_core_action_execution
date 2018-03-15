package PKG_Manager_Package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;

import PKG_Manager_Package.ProcessStateDef.State;
import Package_Info_Package.PackageInfo;
import Package_Info_Package.PackageInfo.SubNodeInfo;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;
import action_execution_msgs.Package;


public class CommonTypeManager extends IControlFunction{
	
	private Hashtable<String, Process> mProcessTable;
	private PackageInfoManager mCommonInfoManager;
	
	private static CommonTypeManager mInstance;
	private InterfaceTypeManaer mInterfaceManager;
	
	private final int COMMAND_TIME=500;
	

	
	CommonTypeManager() {
		mProcessTable=new Hashtable<String, Process>();
		mInterfaceManager=InterfaceTypeManaer.getInstance();
		mCommonInfoManager=PackageInfoManager.getInstance();
	}
	
	static public CommonTypeManager getInstance()
	{
		if(mInstance==null)
			mInstance=new CommonTypeManager();
		
		return mInstance;
		
	}

	@Override
	public boolean onInitialize(ConnectedNode connectedNode) {	
		
		if((mInterfaceManager.onInitialize(connectedNode)==false) || (mCommonInfoManager.isLoadConfigFile()==false))
			return false;
		
		return true;
	}
	
	@Override
	public boolean onBoot(String id) {
		
		try{
			PackageInfo.CommonPackageInfo info=mCommonInfoManager.getCommonPkgInfo(id);
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

		PackageInfo.CommonPackageInfo info=mCommonInfoManager.getCommonPkgInfo(id);
		State nodeState=onGetAtciveState(id);
		
		if(nodeState.equals(State.PROCESS_ON))
		{			
			return true;
		}	
		else if(nodeState.equals(State.PROCESS_OFF))
		{
			if(mInterfaceManager.onStart(info.interfaceId)==false)
				return false;
			
			int numOfSub=info.subNodeInfoList.size();
			for(int index=0; index<numOfSub; index++)
			{
				Thread.sleep(COMMAND_TIME);
				PackageInfo.SubNodeInfo nodeInfo=info.subNodeInfoList.get(index);
				Process nProcess=setCommand(nodeInfo.runCommand);
				mProcessTable.put(nodeInfo.id, nProcess);
			}
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
	public boolean onExecute(String id, Package msg) {
		try{
			PackageInfo.CommonPackageInfo info=mCommonInfoManager.getCommonPkgInfo(id);
			
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
			PackageInfo.CommonPackageInfo info=mCommonInfoManager.getCommonPkgInfo(id);
			State nodeState=onGetAtciveState(id);
			
			if(nodeState != State.PROCESS_OFF)
			{
				if(mInterfaceManager.onDestroy(info.interfaceId)==true)
				{
					int numOfSub=info.subNodeInfoList.size();
					for(int index=0; index<numOfSub; index++)
					{
						PackageInfo.SubNodeInfo nodeInfo=info.subNodeInfoList.get(index);
						if(setKill(nodeInfo.killCommandType, nodeInfo.id, nodeInfo.nodeName)==false)
							return false;
					}
						
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
			PackageInfo.CommonPackageInfo info=mCommonInfoManager.getCommonPkgInfo(id);
			
			if((mInterfaceManager.onDestroy(info.interfaceId)==true) && (onDestroy(id)==true))
			{
				if((mInterfaceManager.onStart(info.interfaceId)==true) && (onStart(id)==true))
					return onGetAtciveState(id).equals(State.PROCESS_ON);;
			}
		}catch(Exception e)
		{
			return false;
		}
		
		return false;
	}

	//SubNode + InterfaceNode Check 
	@Override
	public State onGetAtciveState(String id) {
		
		try{
			PackageInfo.CommonPackageInfo info=mCommonInfoManager.getCommonPkgInfo(id);
			
			int numOfCheckedNode=0;			
			for(SubNodeInfo subNodeInfo : info.subNodeInfoList)
			{
				if(isActiveNode(subNodeInfo.nodeName)==true)
					numOfCheckedNode=numOfCheckedNode+1;
			}
			
			State interfaceState=mInterfaceManager.onGetAtciveState(info.interfaceId);
			if(interfaceState.equals(State.PROCESS_OFF) || interfaceState.equals(State.NONE_TAG))
			{
				if(numOfCheckedNode==0)
					return State.PROCESS_OFF;
			}
			
			if(interfaceState.equals(State.PROCESS_ON) || interfaceState.equals(State.NONE_TAG))
			{
				if(numOfCheckedNode==info.subNodeInfoList.size())
					return State.PROCESS_ON;
			}	
			
		}catch(Exception e)
		{
			return State.PROCESS_ERROR;
		}
		
		return State.PROCESS_ERROR;
	}
	
	private boolean setKill(String killCommandType, String id, String nodeName)
	{
		try{
			
			if(killCommandType.equals(PackageInfoDef.KillCommandType.NODE.getStr()))
			{
				setNodeKill(nodeName);
			}
			else if(killCommandType.equals(PackageInfoDef.KillCommandType.PROCESS.getStr()) && mProcessTable.containsKey(id))
			{
				setProcessKill(mProcessTable.get(id));
			}
			
			Thread.sleep(COMMAND_TIME);
			
			if(isActiveNode(nodeName)==false)
			{
				mProcessTable.remove(id);
				return true;
			}
			
		}catch(Exception e)
		{
			return false;
		}
		
		return false;
	}


	
}
