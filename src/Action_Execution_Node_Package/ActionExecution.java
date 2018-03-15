package Action_Execution_Node_Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;

import PKG_Manager_Package.*;
import PKG_Manager_Package.ProcessStateDef.State;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;
import Package_Info_Package.PackagePlanner;




public class ActionExecution {
	
	class MsgInfo{
		long StartTime;
		action_execution_msgs.Command_list CommandList;
		Vector<action_execution_msgs.Command_result> CommandResultListInput=new Vector<action_execution_msgs.Command_result>();
		
	}
	
	
	private PackageInfoManager mPackageInfo=PackageInfoManager.getInstance();	
	PackagePlanner mPlanner=PackagePlanner.getInstance();
	private MessageFactory mMessageFactory;
	
	private Map<String,  PackageInfoDef.RootType> mActivePackageTable = Collections.synchronizedMap(new HashMap<String,  PackageInfoDef.RootType>());
	private Map<Integer, MsgInfo> mMsgInfoTable = Collections.synchronizedMap(new HashMap<Integer, MsgInfo>());
	
	private ControlFunctionFactory mFactory = new ControlFunctionFactory();
	
	public ActionExecution(ConnectedNode connectedNode) 
	{
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
		
		IControlFunction manager=mFactory.getInstanceFromType(PackageInfoDef.RootType.COMMON);	
		manager.onInitialize(connectedNode);
		
		manager=mFactory.getInstanceFromType(PackageInfoDef.RootType.LAUNCH);	
		manager.onInitialize(connectedNode);
		
		manager=mFactory.getInstanceFromType(PackageInfoDef.RootType.PROGRAM);	
		manager.onInitialize(connectedNode);
		
		

	}
	
	public action_execution_msgs.Command_list getFilledPackageIdMsg(action_execution_msgs.Command_list message)
	{
		int id=message.getId();
		
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			message.getPackageList().get(numOfPackage).setId(id);
		}
		
		return message;
	}
	
	
	//MSG from Agent Check--> 에러 없음: true; 에러 있음: false;
	public boolean getCommandListFormatCheck(action_execution_msgs.Command_list message)
	{
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			int errorID=getTopicCheckResult(message);
			if(errorID!=ProcessStateDef.State.NONE_ERROR.getInt())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public action_execution_msgs.Command_result_list setLoadBoot()
	{
		action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		final int commandListId=-1;
		
		ArrayList<String> packageIdList=mPackageInfo.getBootIdList();
		for(String packageName : packageIdList)
		{
			PackageInfoDef.RootType packageType=mPackageInfo.getType(packageName);
			IControlFunction manager=mFactory.getInstanceFromType(packageType);	
			if(manager.onBoot(packageName)==false)
			{
				action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
				commandResultMsg=getCommandResultMsg(commandListId, packageName, false, ProcessStateDef.State.PROCESS_ERROR.getInt());
				commandResultListInput.add(commandResultMsg);
			}
			else
			{
				mActivePackageTable.put(packageName, packageType);
				
			}
			
		}
	
		commandResultListMsg.setId(commandListId);
		commandResultListMsg.setCommandResultList(commandResultListInput);
		
		return commandResultListMsg;
	}
	
	public action_execution_msgs.Command_result_list setOnCommand(action_execution_msgs.Command_list message)
	{
		action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		int commandListId=message.getId();
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();		
			PackageInfoDef.RootType packageType=mPackageInfo.getType(packageName);
			
			IControlFunction manager=mFactory.getInstanceFromType(packageType);	
			if(mPlanner.onPreStartPkg(mActivePackageTable,packageType, packageName)==true)
			{
				if(manager.onStart(packageName)==true)
				{
					mActivePackageTable.put(packageName, packageType);
					commandResultMsg=getCommandResultMsg(commandListId, packageName, true, ProcessStateDef.State.NONE_ERROR.getInt());
				}
				else
				{					
					commandResultMsg=getCommandResultMsg(commandListId, packageName, false, ProcessStateDef.State.PROCESS_ERROR.getInt());
				}
			}
			else
			{
				commandResultMsg=getCommandResultMsg(commandListId, packageName, false, ProcessStateDef.State.PROCESS_ERROR.getInt());
			}
			
			commandResultListInput.add(commandResultMsg);
				
		}  
		
		commandResultListMsg.setId(commandListId);
		commandResultListMsg.setCommandResultList(commandResultListInput);
		
		return commandResultListMsg;
		
	}
	
	public Vector<action_execution_msgs.Command_result> setExecutionCommand(action_execution_msgs.Command_list message)
	{
		Vector<action_execution_msgs.Command_result> commandResultList=new Vector<action_execution_msgs.Command_result>();
		int commandListId=message.getId();
		
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			action_execution_msgs.Package packageMsg= message.getPackageList().get(numOfPackage);
			String packageName=packageMsg.getPackageName();
			PackageInfoDef.RootType packageType=mPackageInfo.getType(packageMsg.getPackageName());

			IControlFunction manager=mFactory.getInstanceFromType(packageType);	
			
			if((mPlanner.onPreStartPkg(mActivePackageTable,packageType,packageName)==true) && (manager.onExecute(packageName, packageMsg)==true))
			{
				if(mActivePackageTable.containsKey(packageName)==false)
					mActivePackageTable.put(packageName, packageType);
				
			}
			else
			{
				commandResultMsg=getCommandResultMsg(commandListId, packageName, false,ProcessStateDef.State.PROCESS_ERROR.getInt());
				commandResultList.add(commandResultMsg);
			}
			
			
		}
		
		return commandResultList;
		
	}
	
	public action_execution_msgs.Command_result_list setOffCommand(action_execution_msgs.Command_list message)
	{
		action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		int commandListId=message.getId();
		
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();
			PackageInfoDef.RootType packageType=mPackageInfo.getType(message.getPackageList().get(numOfPackage).getPackageName());
			
			
			IControlFunction manager=mFactory.getInstanceFromType(packageType);	

			if((mPlanner.onEffDestroyPkg(mActivePackageTable,packageType, packageName)==false) || (manager.onDestroy(packageName)==false))
			{
				if(mActivePackageTable.containsKey(packageName)==false)
					mActivePackageTable.put(packageName, packageType);
				
				
				commandResultMsg=getCommandResultMsg(commandListId, packageName, false,ProcessStateDef.State.PROCESS_ERROR.getInt());
				commandResultListInput.add(commandResultMsg);
			}
			else
			{
			
				mActivePackageTable.remove(packageName);
				commandResultMsg=getCommandResultMsg(commandListId, packageName, true, ProcessStateDef.State.NONE_ERROR.getInt());
				commandResultListInput.add(commandResultMsg);
				setDeleteMsgInfo(packageName);
				
				
			}
		
		}
		commandResultListMsg.setId(message.getId());
		commandResultListMsg.setCommandResultList(commandResultListInput);
		
		return commandResultListMsg;
		
	}	
	
	public action_execution_msgs.Command_result_list setRecoveryCommand(action_execution_msgs.Command_list message)
	{
		action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		int commandListId=message.getId();
		
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();
			PackageInfoDef.RootType packageType=mPackageInfo.getType(packageName);
			
			if((mPlanner.onPreDestroyPkg(mActivePackageTable,packageType, packageName)==false))
			{
				commandResultMsg=getCommandResultMsg(commandListId, packageName, false,ProcessStateDef.State.PROCESS_ERROR.getInt());
				
			}
			else
			{
				IControlFunction manager=mFactory.getInstanceFromType(packageType);	
				mActivePackageTable.put(packageName, packageType);
				if((mPlanner.onPreStartPkg(mActivePackageTable,packageType, packageName)==true))
				{
					if(manager.onRecovery(packageName)==true)
					{
						commandResultMsg=getCommandResultMsg(commandListId, packageName, true,ProcessStateDef.State.NONE_ERROR.getInt());
						mActivePackageTable.put(packageName, packageType);
					}
					else
					{
						commandResultMsg=getCommandResultMsg(commandListId, packageName, false,ProcessStateDef.State.PROCESS_ERROR.getInt());
					}
					
				}
				else
				{
					commandResultMsg=getCommandResultMsg(commandListId, packageName, false,ProcessStateDef.State.PROCESS_ERROR.getInt());		
				}
			}
			commandResultListInput.add(commandResultMsg);
			
		}
		commandResultListMsg.setId(message.getId());
		commandResultListMsg.setCommandResultList(commandResultListInput);
		
		return commandResultListMsg;
		
	}
	
	
	public boolean isActivePackage(String packageName)
	{
		try{
			
			PackageInfoDef.RootType packageType=mPackageInfo.getType(packageName);	
			IControlFunction manager=mFactory.getInstanceFromType(packageType);
			State state=manager.onGetAtciveState(packageName);
			if(state.equals(State.PROCESS_ON))
			{
				mActivePackageTable.put(packageName, packageType);
				return true;
			}
			else if(state.equals(State.PROCESS_OFF))
			{
				if(mActivePackageTable.containsKey(packageName)==true)
				{	
					mActivePackageTable.remove(packageName);
					setDeleteMsgInfo(packageName);	
				}
					
			}
		}
		catch(Exception e)
		{
			return false;
		}
		
		return false;
	}

	public boolean onDestroyAllPackage()
	{
		Set<String> keyList=mActivePackageTable.keySet();
		 java.util.Iterator<String> iterator= keyList.iterator();
		 try {
			 while(iterator.hasNext()==true)
			 {
				 String packageName=iterator.next();
				 PackageInfoDef.RootType packageType=mActivePackageTable.get(packageName);
				 
				 IControlFunction manager=mFactory.getInstanceFromType(packageType);
				 manager.onDestroy(packageName);				
			 }
			
		 } 
		 catch (Exception e) {
			 return false;
			}
		 
		return true;
	}
	
	public ArrayList<String> getActivePkgList()
	{ 
		try {
			ArrayList<String> activeList=new ArrayList<String>(mActivePackageTable.keySet());  
			return activeList;
			
		 } 
		 catch (Exception e) {
			 return null;
			}

	}
	

	
	private action_execution_msgs.Command_result getCommandResultMsg(int id, String packageName, boolean sucecess, int error)
	{
		action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
		commandResultMsg.setId(id);
		commandResultMsg.setErrorId(error);
		commandResultMsg.setPackageName(packageName);
		commandResultMsg.setSuccess(sucecess);
		
		return commandResultMsg;
	}
	
	//Interface에서 기본적인 구성을 갖춘 토픽을 내려보냈는지 체크
	private int getTopicCheckResult(action_execution_msgs.Command_list message)
	{
		//1) timeStep==null
		int id=message.getId();
		if(id<=0)
		{
			System.out.println("getTopicCheckResult(): timeStep==null");
			return ProcessStateDef.State.TOPIC_ERROR.getInt();
		}
		
		//2) commandType==null
		String commandType=message.getCommandType();
		CommandDef.Type command = CommandDef.getCommandType(commandType);
		if(command==null)
		{
			System.out.println("getTopicCheckResult(): commandType==null");
			return ProcessStateDef.State.TOPIC_ERROR.getInt();
		}
		
		//3) package==null	
		if(message.getPackageList().size()==0)
		{
			System.out.println("getTopicCheckResult(): package==null");
			return ProcessStateDef.State.TOPIC_ERROR.getInt();
		}
		
		return ProcessStateDef.State.NONE_ERROR.getInt();
	}
	
	public void setMsgInfo(action_execution_msgs.Command_list commandList, Vector<action_execution_msgs.Command_result> CommandResultListInput)
	{
		MsgInfo info=new MsgInfo();
		info.StartTime=System.currentTimeMillis(); 
		info.CommandList=commandList;
		info.CommandResultListInput=CommandResultListInput;
		mMsgInfoTable.put(commandList.getId(), info);
	}
	
	//Is it a common msg from Interface-> Yes: true, No={error, lated msg}: false 
	public void setMsgInfo(action_execution_msgs.Command_result commandResult)
	{
		int id=commandResult.getId();
		//ugentMsg=false;
		if(mMsgInfoTable.containsKey(id)==true)
		{
			MsgInfo info=mMsgInfoTable.get(id);
			info.CommandResultListInput.add(commandResult);
			mMsgInfoTable.replace(id, info);
		}
		else //ugentMsg=true
		{
			MsgInfo info = new MsgInfo();
			info.CommandList=getCommandListMsg(CommandDef.ERROR_COMMAND_ID,"null",commandResult.getPackageName());
			info.CommandResultListInput.add(commandResult);
			info.StartTime=System.currentTimeMillis(); 
			mMsgInfoTable.put(CommandDef.ERROR_COMMAND_ID, info);
		}
		
	}
	
	public boolean isCompleteCommand(int commandId)
	{
		try{
			MsgInfo info=mMsgInfoTable.get(commandId);
			if(info.CommandList.getPackageList().size()==info.CommandResultListInput.size())
				return true;
		}catch(Exception e)
		{
			
		}
		
		
		return false;
	}
	
	public action_execution_msgs.Command_result_list getCommandResultListMsg(int commandId)
	{
		action_execution_msgs.Command_result_list resultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		resultListMsg.setId(commandId);
		resultListMsg.setCommandResultList(mMsgInfoTable.get(commandId).CommandResultListInput);
		
		return resultListMsg;
	}
	
	public boolean setRemoveMsgInfo(int commandId)
	{
		if(mMsgInfoTable.containsKey(commandId)==true)
		{
			mMsgInfoTable.remove(commandId);
			return true;
		}
		
		return false;
		
	}
	
	
	public action_execution_msgs.Command_list getCommandListMsg(int id, String commandType, String pkgName)
	{
		action_execution_msgs.Command_list commandListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_list._TYPE);
		commandListMsg.setId(id);
		commandListMsg.setCommandType(commandType);
		
		Vector<action_execution_msgs.Package> packageMsgList=new Vector<action_execution_msgs.Package>();
		action_execution_msgs.Package packageMsg=mMessageFactory.newFromType(action_execution_msgs.Package._TYPE);
		packageMsg.setId(id);
		packageMsg.setPackageName(pkgName);
		packageMsgList.add(packageMsg);
		commandListMsg.setPackageList(packageMsgList);
		
		return commandListMsg;
	}
	
	public ArrayList<Integer> getMsgInfoIdList()
	{
		try {
			ArrayList<Integer> idList=new ArrayList<Integer>(mMsgInfoTable.keySet());
			
			return idList;
			
		 } 
		 catch (Exception e) {
			 return null;
			}
	}
	
	private boolean setDeleteMsgInfo(String packageName)
	{
		try{
			ArrayList<Integer> commandIdList=getMsgInfoIdList();
			for(int id: commandIdList)
			{
				MsgInfo msgInfo=mMsgInfoTable.get(id);
				for(action_execution_msgs.Package packageMsg:msgInfo.CommandList.getPackageList())
				{
					if(packageMsg.getPackageName().equals(packageName))
						mMsgInfoTable.remove(id);
				}
			}
		}
		catch(Exception e){
			return false;
		}
		
		return true;
		
	}
	
	public void setDeleteMsgInfo(int commandId)
	{
		mMsgInfoTable.remove(commandId);
	}
	
	public MsgInfo getMsgInfo(int id)
	{
		return mMsgInfoTable.get(id);
	}
	
	
	
	
	
	

}
