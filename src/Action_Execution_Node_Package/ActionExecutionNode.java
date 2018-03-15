package Action_Execution_Node_Package;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageFactory;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import Action_Execution_Node_Package.ActionExecution.MsgInfo;
import PKG_Manager_Package.ProcessStateDef;


public class ActionExecutionNode implements NodeMain{
	
	private MessageFactory mMessageFactory;
	private ActionExecution mActionExecution;
	private MsgErrorMonitor mErrorMonitor;
	private CommandDef mPackageActionSet=new CommandDef();
	
	private Publisher<action_execution_msgs.Command_result_list> mPubToAgent;
	
	private Log mLog;

	@Override
	public void onStart(ConnectedNode connectedNode) {
		final int Q_SIZE_MAX=100;	
		
			
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
		
		mActionExecution=new ActionExecution(connectedNode);	   
		mErrorMonitor=new MsgErrorMonitor(connectedNode);
		//mLog = connectedNode.getLog();
		
		String pubTopicToInterface="command_result_list";
	    mPubToAgent=connectedNode.newPublisher(pubTopicToInterface, action_execution_msgs.Command_result_list._TYPE);
	  
	    synchronized (mActionExecution){
	    	action_execution_msgs.Command_result_list bootResultMsg=mActionExecution.setLoadBoot();
	    	 if(bootResultMsg.getCommandResultList().isEmpty()==false)
	 	    {
	 	    	mPubToAgent.publish(bootResultMsg);
	 	    	System.out.println("#[Boot] Fail...");
	 	    }
	 	    else
	 	    {
	 	    	System.out.println("#[Boot] Success...");
	 	    }
		}
	    
	   
	 
	  
	    final CancellableLoop stateCheckLoop = new CancellableLoop() {
	    	@Override 
	    	protected void loop(){
	    	 
	    		try{ 
	    			onCheckTimeOut();
	     
	    			Thread.sleep(5000);
	    			
	    			onCheckRecoveryPkg();
	
	    		}catch(Exception e)
	    		{
	    			System.out.println("Node::Initailize Error()...");
	    			
	    		}
	 
	    		
	    		
	    	}
	    };
		
	    
	    
		String interfaceSub_topicName="command_list";
		Subscriber<action_execution_msgs.Command_list> interfaceSub=connectedNode.newSubscriber(interfaceSub_topicName, action_execution_msgs.Command_list._TYPE);
	    interfaceSub.addMessageListener(new MessageListener<action_execution_msgs.Command_list>() {
			@Override
			public void onNewMessage(action_execution_msgs.Command_list commandList) {
				System.out.println("#ActionExecutionNode[Command_list]");				
				synchronized (mActionExecution){
					commandList=mActionExecution.getFilledPackageIdMsg(commandList);
		
					if(mActionExecution.getCommandListFormatCheck(commandList)==true)
					{
	
						CommandDef.Type command = mPackageActionSet.getCommandType(commandList.getCommandType());
						if(CommandDef.Type.PACKAGE_ON ==command)
						{
							action_execution_msgs.Command_result_list commandResultListMsg=mActionExecution.setOnCommand(commandList);
							mPubToAgent.publish(commandResultListMsg);
						}
						else if(CommandDef.Type.PACKAGE_OFF ==command)
						{
							action_execution_msgs.Command_result_list commandResultListMsg=mActionExecution.setOffCommand(commandList);
							mPubToAgent.publish(commandResultListMsg);
							
						}	
						else if(CommandDef.Type.PACKAGE_EXECUTION==command)
						{
							mActionExecution.setMsgInfo(commandList, mActionExecution.setExecutionCommand(commandList));
							if(mActionExecution.isCompleteCommand(commandList.getId()))
							{
								action_execution_msgs.Command_result_list msg=mActionExecution.getCommandResultListMsg(commandList.getId());
								mPubToAgent.publish(msg);
								mActionExecution.setDeleteMsgInfo(commandList.getId());
							}
							
						}
						else if(CommandDef.Type.PACKAGE_RECOVERY==command)
						{
							action_execution_msgs.Command_result_list commandResultListMsg=mActionExecution.setRecoveryCommand(commandList);
							mPubToAgent.publish(commandResultListMsg);
						}
						
					}
					else
					{
						sendErrorMessageToAgent(commandList, ProcessStateDef.State.PROCESS_ERROR.getInt());
					}
				}
				
			}
		},Q_SIZE_MAX);
	  
		
		
	
	    String packageSubTopic="command_result";
	    Subscriber<action_execution_msgs.Command_result> packageSub=connectedNode.newSubscriber(packageSubTopic, action_execution_msgs.Command_result._TYPE);
	    packageSub.addMessageListener(new MessageListener<action_execution_msgs.Command_result>() {
			@Override
			public void onNewMessage(action_execution_msgs.Command_result message) {
				System.out.println("#ActionExecutionNode[Command_result]");
				synchronized (mActionExecution){
					mActionExecution.setMsgInfo(message);
					if(mActionExecution.isCompleteCommand(message.getId()))
					{
						action_execution_msgs.Command_result_list msg=mActionExecution.getCommandResultListMsg(message.getId());
						mPubToAgent.publish(msg);
						mActionExecution.setDeleteMsgInfo(message.getId());
					}
				}
				
			}
		},10);
	    
	    System.out.println("Load Complete");
	    connectedNode.executeCancellableLoop(stateCheckLoop);    	  
	  
	}
	
	@Override
	public void onShutdown(Node node) {
		System.out.println("ActionExecutionNode-->onShutdown");
		synchronized (mActionExecution){
			mActionExecution.onDestroyAllPackage();
		}
		
	}

	@Override
	public void onShutdownComplete(Node node) {
		synchronized (mActionExecution){
			mActionExecution.onDestroyAllPackage();
		}
		System.out.println("ActionExecutionNode-->onShutdownComplete");
		
	}

	@Override
	public void onError(Node node, Throwable throwable) {
		System.out.println("ActionExecutionNode-->onError");
		synchronized (mActionExecution){
			mActionExecution.onDestroyAllPackage();
		}
		
	}

	//It is essential to set the package name and node name
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("Action_Execution_Node_Package/ActionExecutionNode");
	}
	
	private void onCheckRecoveryPkg()
	{
		synchronized (mActionExecution){		        		
    		for(String pkgName: mActionExecution.getActivePkgList())
    		{
    			if(mActionExecution.isActivePackage(pkgName)==false)
    			{	        				
    				action_execution_msgs.Command_list commandListMsg=mActionExecution.getCommandListMsg(CommandDef.ERROR_COMMAND_ID, CommandDef.Type.PACKAGE_RECOVERY.toString(), pkgName);
    				action_execution_msgs.Command_result_list resultListMsg=mActionExecution.setRecoveryCommand(commandListMsg);
    				
    				if(resultListMsg.getCommandResultList().get(0).getSuccess()==false)
    				{
    					mPubToAgent.publish(resultListMsg);
    				}
    				else
    				{
    					System.out.println("Recovery 성공");
    				}
    				
    			}
    		}
		}
	}
	
	private void onCheckTimeOut()
	{
		synchronized (mActionExecution) {
			for(int commandId: mActionExecution.getMsgInfoIdList())
			{
				MsgInfo msgInfo=mActionExecution.getMsgInfo(commandId);
				ArrayList<String> timeOutList=mErrorMonitor.getTimeOutPkgNameList(msgInfo);
				int numOfResult=timeOutList.size()+msgInfo.CommandResultListInput.size();
				if(numOfResult==msgInfo.CommandList.getPackageList().size())
				{
					sendErrorMessageToAgent(commandId, msgInfo.CommandResultListInput, timeOutList, ProcessStateDef.State.PROCESS_ERROR.getInt());
					mActionExecution.setDeleteMsgInfo(commandId);
				}
				
			}
			
		}
	}
	
	private void sendErrorMessageToAgent(action_execution_msgs.Command_list commandListMsg, int errorId)
	{
		List<action_execution_msgs.Command_result> commandResultListInput = new ArrayList<action_execution_msgs.Command_result>();
		action_execution_msgs.Command_result_list commandResultList=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		for(int indexOfPackage=0; indexOfPackage<commandListMsg.getPackageList().size(); indexOfPackage++)
		{
			action_execution_msgs.Command_result commandResult =mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			commandResult.setErrorId(errorId);
			commandResult.setSuccess(false);
			commandResult.setPackageName(commandListMsg.getPackageList().get(indexOfPackage).getPackageName());
			commandResultListInput.add(commandResult);
		}
		commandResultList.setCommandResultList(commandResultListInput);
		commandResultList.setId(commandListMsg.getId());
		mPubToAgent.publish(commandResultList);
	}
	
	private void sendErrorMessageToAgent(int commandId, List<action_execution_msgs.Command_result> resultListMsg, ArrayList<String> timeOutNameList, int errorId)
	{
		List<action_execution_msgs.Command_result> commandResultListInput = resultListMsg;
		action_execution_msgs.Command_result_list commandResultList=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
		
		for(String pkgName: timeOutNameList)
		{
			action_execution_msgs.Command_result commandResult =mMessageFactory.newFromType(action_execution_msgs.Command_result._TYPE);
			commandResult.setErrorId(errorId);
			commandResult.setSuccess(false);
			commandResult.setPackageName(pkgName);
			commandResultListInput.add(commandResult);
		}

		commandResultList.setCommandResultList(commandResultListInput);
		commandResultList.setId(commandId);
		mPubToAgent.publish(commandResultList);
	}
	
	
	
	
	



}
