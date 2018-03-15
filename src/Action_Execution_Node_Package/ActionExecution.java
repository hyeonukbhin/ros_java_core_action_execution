package Action_Execution_Node_Package;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;

import action_execution_msgs.Data;
import PKG_Manager_Package.BaseTypeManager;
import PKG_Manager_Package.CommonTypeManager;
import PKG_Manager_Package.LaunchTypeManager;

public class ActionExecution {
	
	private PackageInfoManager mPackageInfo=new PackageInfoManager();
	private PackageActionSet mActionSet=new PackageActionSet();
	private BaseTypeManager mBaseTypeManager=new BaseTypeManager();
	private LaunchTypeManager 	mLaunchTypeManager;
	private CommonTypeManager mCommonTypeManager;
	private MessageFactory mMessageFactory;
	private Hashtable<String, String> mActivePackageTable=new Hashtable<String, String>();
	
	public ActionExecution(ConnectedNode connectedNode) 
	{
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
		mLaunchTypeManager=new LaunchTypeManager(connectedNode);
		mCommonTypeManager=new CommonTypeManager(connectedNode);
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
	
	//에러 없음: true; 에러 있음: false;
	public boolean getCommandListFormatCheck(action_execution_msgs.Command_list message)
	{
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			int errorID=getTopicCheckResult(message);
			if(errorID!=PackageActionSet.Action.NONE_ERROR.getIDAction())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public action_execution_msgs.Command_result_list setOnCommand(action_execution_msgs.Command_list message)
	{
		action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();	
			int packageId=message.getId();
			String type=mPackageInfo.getPackageType(message.getPackageList().get(numOfPackage).getPackageName());
			

			if(type.equals(mPackageInfo.LAUNCH_TYPE))
			{
				if(mLaunchTypeManager.getOnActiveState(packageName)==true)
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_ALREADY.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
				else
				{
					try {
						mLaunchTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.LAUNCH_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mLaunchTypeManager.getOnActiveState(packageName);
						if(active==true){
							mActivePackageTable.put(packageName, type);
							commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
						else
						{
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				}
			}
			else if(type.equals(mPackageInfo.COMMON_TYPE))
			{
				if(mCommonTypeManager.getOnActiveState(packageName)==true)
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_ALREADY.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
				else
				{
					try {
						mCommonTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.COMMON_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mCommonTypeManager.getOnActiveState(packageName);
						if(active==true){
							mActivePackageTable.put(packageName, type);
							commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
						else
						{
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			else if(type.equals(mPackageInfo.BASE_TYPE))
			{
				
				if(mBaseTypeManager.getActiveState(packageName)==true)
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_ALREADY.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
				else
				{
					try {
						mBaseTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.BASE_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mBaseTypeManager.getActiveState(packageName);
						if(active==true)
						{
							mActivePackageTable.put(packageName, type);
							commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
						else
						{
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}

		}
		commandResultListMsg.setId(message.getId());
		commandResultListMsg.setCommandResultList(commandResultListInput);
		return commandResultListMsg;
		
	}
	
	public Vector<action_execution_msgs.Command_result> setExecutionCommand(action_execution_msgs.Command_list message)
	{
		Vector<action_execution_msgs.Command_result> commandResultList=new Vector<action_execution_msgs.Command_result>();
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			int packageId=message.getId();
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();	
			String type=mPackageInfo.getPackageType(message.getPackageList().get(numOfPackage).getPackageName());
			

			if(type.equals(mPackageInfo.LAUNCH_TYPE))
			{
				if(mLaunchTypeManager.getOnActiveState(packageName)==false)
				{
					//패키지 실행
					try {
						mLaunchTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.LAUNCH_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mLaunchTypeManager.getOnActiveState(packageName);				
						if(active==true){
							//메시지 전달
							mActivePackageTable.put(packageName, type);
							action_execution_msgs.Package packageMsg= message.getPackageList().get(numOfPackage);
							if(mLaunchTypeManager.onSendMSGToNode(packageName,packageMsg)==false)
							{
								commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
								commandResultList.add(commandResultMsg);
							}
						
						}
						else
						{
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
							commandResultList.add(commandResultMsg);
						}
	
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else//메시지 전달
				{
					//메시지 전달
					action_execution_msgs.Package packageMsg= message.getPackageList().get(numOfPackage);
					if(mLaunchTypeManager.onSendMSGToNode(packageName,packageMsg)==false)
					{
						commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
						commandResultList.add(commandResultMsg);
					}
				}
			}
			else if(type.equals(mPackageInfo.COMMON_TYPE))
			{
				if(mCommonTypeManager.getOnActiveState(packageName)==false)
				{
					//패키지 실행
					try {
						mCommonTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.COMMON_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mCommonTypeManager.getOnActiveState(packageName);				
						if(active==true){
							//메시지 전달
							mActivePackageTable.put(packageName, type);
							action_execution_msgs.Package packageMsg= message.getPackageList().get(numOfPackage);
							if(mCommonTypeManager.onSendMSGToNode(packageName,packageMsg)==false)
							{
								commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
								commandResultList.add(commandResultMsg);
							}
						
						}
						else
						{
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
							commandResultList.add(commandResultMsg);
						}
	
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else//메시지 전달
				{
					//메시지 전달
					action_execution_msgs.Package packageMsg= message.getPackageList().get(numOfPackage);
					if(mCommonTypeManager.onSendMSGToNode(packageName,packageMsg)==false)
					{
						commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
						commandResultList.add(commandResultMsg);
					}
				}
			}
			else if(type.equals(mPackageInfo.BASE_TYPE))
			{
				
				if(mBaseTypeManager.getActiveState(packageName)==true)
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());
					commandResultList.add(commandResultMsg);
				}
				else
				{
					try {
						mBaseTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.BASE_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mBaseTypeManager.getActiveState(packageName);
						if(active==true)
						{
							mActivePackageTable.put(packageName, type);
							commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());
							commandResultList.add(commandResultMsg);
						}
						else
						{
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
							commandResultList.add(commandResultMsg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}

		}
		
		return commandResultList;
		
	}
	
	public boolean isActiveAllPackage(action_execution_msgs.Command_list message)
	{
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();	
			int packageId=message.getId();
			String type=mPackageInfo.getPackageType(message.getPackageList().get(numOfPackage).getPackageName());

			if(type.equals(mPackageInfo.LAUNCH_TYPE))
			{
				
			}
			else if(type.equals(mPackageInfo.COMMON_TYPE))
			{
				if(mCommonTypeManager.getOnActiveState(packageName)==false)
				{
					try {
						mCommonTypeManager.onStartPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.COMMON_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mCommonTypeManager.getOnActiveState(packageName);
						if(active==true){
							mActivePackageTable.put(packageName, type);
						}
						else
						{
							return false;
						}
					} catch (InterruptedException e) {
						return false;
					}					
				}
			}
			else if(type.equals(mPackageInfo.BASE_TYPE))
			{
				
			}
		}
		return true;
	}
	
   public action_execution_msgs.Command_result_list onSendActiveFailMsg(action_execution_msgs.Command_list message)
   {
	   action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
	   Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		
	   for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();	
			int packageId=message.getId();
			String type=mPackageInfo.getPackageType(message.getPackageList().get(numOfPackage).getPackageName());

			if(type.equals(mPackageInfo.LAUNCH_TYPE))
			{
				
			}
			else if(type.equals(mPackageInfo.COMMON_TYPE))
			{
				if(mCommonTypeManager.getOnActiveState(packageName)==false)
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_CREATE_PKG.getIDAction());
					commandResultListInput.add(commandResultMsg);									
				}
				else
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
			}
			else if(type.equals(mPackageInfo.BASE_TYPE))
			{
				
			}
		}
	  
	   commandResultListMsg.setId(message.getId());
	   commandResultListMsg.setCommandResultList(commandResultListInput);
	   return commandResultListMsg;
	
   }
	
	public action_execution_msgs.Command_result_list setOffCommand(action_execution_msgs.Command_list message)
	{
		action_execution_msgs.Command_result_list commandResultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		Vector<action_execution_msgs.Command_result> commandResultListInput=new Vector<action_execution_msgs.Command_result>();
		for(int numOfPackage=0; numOfPackage<message.getPackageList().size(); numOfPackage++)
		{
			int packageId=message.getId();
			String packageName=message.getPackageList().get(numOfPackage).getPackageName();
			action_execution_msgs.Command_result commandResultMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			String type=mPackageInfo.getPackageType(message.getPackageList().get(numOfPackage).getPackageName());
			
			if(type.equals(mPackageInfo.LAUNCH_TYPE))
			{
				
				if(mLaunchTypeManager.getOffActiveState(packageName)==true)
				{
					try {
						mLaunchTypeManager.onDestroyPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.LAUNCH_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mLaunchTypeManager.getOffActiveState(packageName);
						if(active==false)
						{
							mActivePackageTable.remove(packageName);
							commandResultMsg=getCommandResultMsg(packageId,packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());							
							commandResultListInput.add(commandResultMsg);
							mLaunchTypeManager.setDeleteTable(packageName);
						}
						else
						{
							
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_DESTROY_PKG.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_ALREADY.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
			}
			if(type.equals(mPackageInfo.COMMON_TYPE))
			{
				
				if(mCommonTypeManager.getOffActiveState(packageName)==true)
				{
					try {
						mCommonTypeManager.onDestroyPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.COMMON_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mCommonTypeManager.getOffActiveState(packageName);
						if(active==false)
						{
							mActivePackageTable.remove(packageName);
							commandResultMsg=getCommandResultMsg(packageId,packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());							
							commandResultListInput.add(commandResultMsg);
							mCommonTypeManager.setDeleteTable(packageName);
						}
						else
						{
							
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_DESTROY_PKG.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_ALREADY.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
			}
			else if(type.equals(mPackageInfo.BASE_TYPE))
			{
				if(mBaseTypeManager.getActiveState(packageName)==true)
				{
					try {
						mBaseTypeManager.onDestroyPackage(packageName);
						int timeOut=mPackageInfo.getTimeOut(mPackageInfo.BASE_TYPE, packageName);
						Thread.sleep(timeOut);
						boolean active=mBaseTypeManager.getActiveState(packageName);
						if(active==false)
						{
							mActivePackageTable.remove(packageName);
							commandResultMsg=getCommandResultMsg(packageId, packageName, true, PackageActionSet.Action.NONE_ERROR.getIDAction());							
							commandResultListInput.add(commandResultMsg);
							mBaseTypeManager.setDeleteTable(packageName);
						}
						else
						{
							
							commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_DESTROY_PKG.getIDAction());
							commandResultListInput.add(commandResultMsg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
				{
					commandResultMsg=getCommandResultMsg(packageId, packageName, false, PackageActionSet.Action.ERROR_ALREADY.getIDAction());
					commandResultListInput.add(commandResultMsg);
				}
				
			}

		}
		
		commandResultListMsg.setId(message.getId());
		commandResultListMsg.setCommandResultList(commandResultListInput);
		return commandResultListMsg;
		
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
			System.out.println("getTopicCheckResult: timeStep==null");
			return PackageActionSet.Action.ERROR_ID_NULL.getIDAction();
		}
		
		//2) commandType==null
		String commandType=message.getCommandType();
		PackageActionSet.Action command = mActionSet.getAction(commandType);
		if(command==null)
		{
			System.out.println("getTopicCheckResult: commandType==null");
			return PackageActionSet.Action.ERROR_COMMAND_NULL.getIDAction();
		}
		
		//3) package==null	
		if(message.getPackageList().size()==0)
		{
			System.out.println("getTopicCheckResult: package==null");
			return PackageActionSet.Action.ERROR_PACKAGE_NAME_NULL.getIDAction();
		}
		
		return PackageActionSet.Action.NONE_ERROR.getIDAction();
	}
	

	public boolean onDestroyAllPackage()
	{
		Set<String> keyList=mActivePackageTable.keySet();
		 java.util.Iterator<String> iterator= keyList.iterator();
		 try {
			 while(iterator.hasNext()==true)
			 {
				 String packageName=iterator.next();
				 String type=mActivePackageTable.get(packageName);
				 
					if(type.equals(mPackageInfo.LAUNCH_TYPE))
					{
						
						if(mLaunchTypeManager.getOffActiveState(packageName)==true)
						{
							mLaunchTypeManager.onDestroyPackage(packageName);
							int timeOut=mPackageInfo.getTimeOut(mPackageInfo.LAUNCH_TYPE, packageName);
							Thread.sleep(timeOut);
								
						}
					
					}
					if(type.equals(mPackageInfo.COMMON_TYPE))
					{
						
						if(mCommonTypeManager.getOffActiveState(packageName)==true)
						{
							mCommonTypeManager.onDestroyPackage(packageName);
							int timeOut=mPackageInfo.getTimeOut(mPackageInfo.COMMON_TYPE, packageName);
						
								Thread.sleep(timeOut);
						
						}
					}
					else if(type.equals(mPackageInfo.BASE_TYPE))
					{
						if(mBaseTypeManager.getActiveState(packageName)==true)
						{
							mCommonTypeManager.onDestroyPackage(packageName);
							int timeOut=mPackageInfo.getTimeOut(mPackageInfo.BASE_TYPE, packageName);
							Thread.sleep(timeOut);
						}					
					}
					}
			
		 } 
		 catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
		return true;
	}
	

}
