package PKG_Manager_Package;

import java.util.ArrayList;
import java.util.Hashtable;

import org.ros.internal.message.GenerateInterfaces;
import org.ros.node.ConnectedNode;

import PKG_Manager_Package.ProcessStateDef.State;
import Package_Info_Package.PackageInfo;
import Package_Info_Package.PackageInfoManager;
import Package_Info_Package.PackageInfo.SubProcess;

public class BaseProgramManager extends IControlFunction{
	

	private PackageInfoManager mProcessInfoManager;
	private Hashtable<String, Process> mProcessTable=new Hashtable<String, Process>();
	private static BaseProgramManager mInstance=new BaseProgramManager();
	
	private final int COMMAND_TIME=500;
	
	BaseProgramManager() {
		// TODO Auto-generated constructor stub
	}
	
	static public BaseProgramManager getInstance()
	{
		if(mInstance==null)
			mInstance=new BaseProgramManager();
		
		return mInstance;
	}

	@Override
	public boolean onInitialize(ConnectedNode connectedNode) {
		mProcessInfoManager=PackageInfoManager.getInstance();
		
		return true;
	}

	@Override
	public boolean onBoot(String id) {
		try{
			PackageInfo.BaseProgramInfo processInfo=mProcessInfoManager.getProgramInfo(id);
			boolean bootType=processInfo.boot;
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
			PackageInfo.BaseProgramInfo processInfo=mProcessInfoManager.getProgramInfo(id);
			State nodeState=onGetAtciveState(id);
			
			
			if(nodeState.equals(State.PROCESS_ON))
			{				
				return true;
			}
			else if(nodeState.equals(State.PROCESS_OFF))
			{
				Process process=setCommand(processInfo.runCommand);
				mProcessTable.put(id, process);
				Thread.sleep(processInfo.loadTimeOut);
				
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
		
		return onStart(id);
	}

	@Override
	public boolean onDestroy(String id) {
		try{
			State nodeState=onGetAtciveState(id);
			if(nodeState != State.PROCESS_OFF)
			{
				setProcessKill(mProcessTable.get(id));
				mProcessTable.remove(id);
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
		
		final String PROCESS_LIST_CHECK="ps -e | grep";
	
		try{
			PackageInfo.BaseProgramInfo processInfo=mProcessInfoManager.getProgramInfo(id);
			ArrayList<SubProcess> subProcessList=processInfo.subProcessList;
			int numOfSubProcess=subProcessList.size();
			int numOfOffProcess=0;
			for(int index=0; index<numOfSubProcess; index++)
			{
				SubProcess subprocess=subProcessList.get(index);
				String command=PROCESS_LIST_CHECK+" "+subprocess.processName;
				int numOfWorkingprocess=getConsoleScreen(command).size();
				if(numOfWorkingprocess != subprocess.numOfprocess)
				{
					if(numOfWorkingprocess==0)
						numOfOffProcess=numOfOffProcess+1;
					else
						return State.PROCESS_ERROR;
				}	
			}
			
			if(numOfOffProcess==numOfSubProcess)
				return State.PROCESS_OFF;
			else	
				return State.PROCESS_ON;
			

		}catch(Exception e)
		{
			return State.PROCESS_ERROR;
		}
		
	}


	
	

}
