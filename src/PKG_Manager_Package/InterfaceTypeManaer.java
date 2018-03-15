package PKG_Manager_Package;

import java.util.Hashtable;

import org.ros.node.ConnectedNode;

import PKG_Manager_Package.ProcessStateDef.State;
import Package_Info_Package.PackageInfo;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;
import action_execution_msgs.Package;

public class InterfaceTypeManaer extends IControlFunction{
	
	private Hashtable<String, Process> mProcessTable;
	private MsgPublisherToInterface mPubToInterface;
	private PackageInfoManager mInterfaceInfoManager;
	
	public static String NONE_TAG="NONE";	
	private final int COMMAND_TIME=500;
	
	private static InterfaceTypeManaer mInstance;
	
	public InterfaceTypeManaer() {

	}
	
	static public InterfaceTypeManaer getInstance()
	{
		if(mInstance==null)
			mInstance=new InterfaceTypeManaer();
		
		return mInstance;
		
	}

	@Override
	public boolean onInitialize(ConnectedNode connectedNode) {
		mPubToInterface=new MsgPublisherToInterface(connectedNode);
		mInterfaceInfoManager=PackageInfoManager.getInstance();
		mProcessTable=new Hashtable<String, Process>();
		
		return mInterfaceInfoManager.isLoadConfigFile();
	}

	@Override
	public boolean onBoot(String id) {
		return true;
	}
	
	
	@Override
	public boolean onStart(String id) {
		try{			
			PackageInfo.InterfacePackageInfo info=mInterfaceInfoManager.getInterfaceInfo(id);
			State nodeState=onGetAtciveState(id);
			
			if(nodeState.equals(State.NONE_TAG))
				return true;
			
			if(nodeState.equals(State.PROCESS_ON))
			{
				return true;
			}	
			else if(nodeState.equals(State.PROCESS_OFF))
			{
				Process process=setCommand(info.runCommand);
				mProcessTable.put(info.id, process);				
				Thread.sleep(info.loadTimeOut);
				System.out.println("Interface::onStart: ON");
				return onGetAtciveState(id).equals(State.PROCESS_ON);
			}
			else if(nodeState.equals(State.PROCESS_ERROR))
			{
				return onRecovery(id);
			}
		}catch(Exception e)
		{
			return false;
		}
		
		
		return false;
	}

	@Override
	public boolean onExecute(String id, Package msg) {
		try{
			PackageInfo.InterfacePackageInfo info=mInterfaceInfoManager.getInterfaceInfo(id);
			State nodeState=onGetAtciveState(id);
			
			if(nodeState.equals(State.NONE_TAG)==true)
				return true;
			
			if(onStart(id)==true)
			{
				return mPubToInterface.onSendMsg(info.id, msg);
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
			PackageInfo.InterfacePackageInfo info=mInterfaceInfoManager.getInterfaceInfo(id);
			State nodeState=onGetAtciveState(id);
			
			if(nodeState.equals(State.NONE_TAG)==true)
				return true;
		
			if(nodeState != State.PROCESS_OFF)
			{
				boolean result=setKill(info.killCommandType, info.id, info.nodeName);
				if(result==true)
				{
					mPubToInterface.onDestroyPublisher(info.id);
				}
				Thread.sleep(COMMAND_TIME);

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
			PackageInfo.InterfacePackageInfo info=mInterfaceInfoManager.getInterfaceInfo(id);
			State nodeState=onGetAtciveState(id);
			
			if(nodeState.equals(State.NONE_TAG)==true)
				return true;
			
			if(onDestroy(id)==true)
			{
				return onStart(id);
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
			if(id.equals(NONE_TAG)==true)
				return State.NONE_TAG;
			
			PackageInfo.InterfacePackageInfo info=mInterfaceInfoManager.getInterfaceInfo(id);
			if(isActiveNode(info.nodeName)==true)
			{
				if(mPubToInterface.onCreatePublisher(info.id, info.topicName)==true)
					return State.PROCESS_ON;
			}
			else
			{
				return State.PROCESS_OFF;
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
