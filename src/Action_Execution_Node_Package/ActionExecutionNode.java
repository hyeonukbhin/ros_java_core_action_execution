package Action_Execution_Node_Package;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

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

import action_execution_msgs.Command_list;

class SharedPackageInfo{
	action_execution_msgs.Command_list CommandList;
	Vector<action_execution_msgs.Command_result> CommandResultListInput=new Vector<action_execution_msgs.Command_result>();
}

class ResultInfo{
	String PackageName;
	int Error;
}

public class ActionExecutionNode implements NodeMain{
	
	private ActionExecution mActionExecution;
	private PackageActionSet mPackageActionSet=new PackageActionSet();
	private ConcurrentMap<Integer, SharedPackageInfo> mSharedPackageTable = new ConcurrentHashMap<Integer, SharedPackageInfo>();
	private MessageFactory mMessageFactory;
	private Publisher<action_execution_msgs.Command_result_list> mPubToInterface;
	
	
	
	@Override
	public void onStart(ConnectedNode connectedNode) {
		final int Q_SIZE_MAX=100;	
		
		mActionExecution=new ActionExecution(connectedNode);		
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
		
		String pubTopicToInterface="command_result_list";
	    mPubToInterface=connectedNode.newPublisher(pubTopicToInterface, action_execution_msgs.Command_result_list._TYPE);
	   
		
		String interfaceSub_topicName="command_list";
		Subscriber<action_execution_msgs.Command_list> interfaceSub=connectedNode.newSubscriber(interfaceSub_topicName, action_execution_msgs.Command_list._TYPE);
	    interfaceSub.addMessageListener(new MessageListener<action_execution_msgs.Command_list>() {
			@Override
			public void onNewMessage(action_execution_msgs.Command_list commandList) {
				System.out.println("#ActionExecutionNode[Command_list]");				
				
				commandList=mActionExecution.getFilledPackageIdMsg(commandList);
	
				if(mActionExecution.getCommandListFormatCheck(commandList)==true)
				{

					PackageActionSet.Action command = mPackageActionSet.getAction(commandList.getCommandType());
					if(PackageActionSet.Action.PACKAGE_ON ==command)
					{
						action_execution_msgs.Command_result_list commandResultListMsg=mActionExecution.setOnCommand(commandList);
						mPubToInterface.publish(commandResultListMsg);
					}
					else if(PackageActionSet.Action.PACKAGE_OFF ==command)
					{
						action_execution_msgs.Command_result_list commandResultListMsg=mActionExecution.setOffCommand(commandList);
						mPubToInterface.publish(commandResultListMsg);
						//mActionExecution.onDestroyAllPackage();
					}
					else if(PackageActionSet.Action.PACKAGE_EXECUTION==command)
					{
						boolean activeState=mActionExecution.isActiveAllPackage(commandList);
						if(activeState==false) //It is impossible to execute some package
						{
							action_execution_msgs.Command_result_list commandResultListMsg=mActionExecution.onSendActiveFailMsg(commandList);
							mPubToInterface.publish(commandResultListMsg);
						}
						else
						{
							SharedPackageInfo sharedPackageInfo=new SharedPackageInfo();
							sharedPackageInfo.CommandList=commandList;
							sharedPackageInfo.CommandResultListInput=mActionExecution.setExecutionCommand(commandList);
							
							
							if(sharedPackageInfo.CommandList.getPackageList().size()==sharedPackageInfo.CommandResultListInput.size())
							{
								action_execution_msgs.Command_result_list resultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
								resultListMsg.setId(sharedPackageInfo.CommandList.getId());
								resultListMsg.setCommandResultList(sharedPackageInfo.CommandResultListInput);
								mPubToInterface.publish(resultListMsg);
							}
							else
							{
								mSharedPackageTable.put(commandList.getId(), sharedPackageInfo);
							}
						}
					}
					
				}
				else
				{
					sendErrorMessageToInterface(commandList, PackageActionSet.Action.ERROR_TOPIC_NULL.getIDAction());
				}
				
				
			}
		},Q_SIZE_MAX);
	  
		
		
	
	    String packageSubTopic="command_result";
	    Subscriber<action_execution_msgs.Command_result> packageSub=connectedNode.newSubscriber(packageSubTopic, action_execution_msgs.Command_result._TYPE);
	    packageSub.addMessageListener(new MessageListener<action_execution_msgs.Command_result>() {
			@Override
			public void onNewMessage(action_execution_msgs.Command_result message) {
				System.out.println("#ActionExecutionNode[Command_result]");
				SharedPackageInfo sharedPackageInfo=mSharedPackageTable.get(message.getId());
				//패키지에서 아이디 잘 못 올려보내면 없는 id로 테이블에서 검색하게 되서 널값나옴. 추후에 처리 필요함.
				if(sharedPackageInfo != null)
				{
					sharedPackageInfo.CommandResultListInput.add(message);
					if(sharedPackageInfo.CommandList.getPackageList().size()==sharedPackageInfo.CommandResultListInput.size())
					{
						action_execution_msgs.Command_result_list resultListMsg=mMessageFactory.newFromType(action_execution_msgs.Command_result_list._TYPE);
						resultListMsg.setId(sharedPackageInfo.CommandList.getId());
						resultListMsg.setCommandResultList(sharedPackageInfo.CommandResultListInput);
						mPubToInterface.publish(resultListMsg);
						mSharedPackageTable.remove(sharedPackageInfo.CommandList.getId());
					}
					else
					{
						mSharedPackageTable.put(message.getId(), sharedPackageInfo);
					} 
				}
				
				
			}
		},10);
	  
	     	  
	  
	}
	
	@Override
	public void onShutdown(Node node) {
		System.out.println("ActionExecutionNode-->onShutdown");
		
	}

	@Override
	public void onShutdownComplete(Node node) {
		// TODO Auto-generated method stub
		System.out.println("ActionExecutionNode-->onShutdownComplete");
		
	}

	@Override
	public void onError(Node node, Throwable throwable) {
		// TODO Auto-generated method stub
		System.out.println("ActionExecutionNode-->onError");
		
	}

	//It is essential to set the package name and node name
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("Action_Execution_Node_Package/ActionExecutionNode");
	}
	
	private void sendErrorMessageToInterface(action_execution_msgs.Command_list commandListMsg, int errorId)
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
		mPubToInterface.publish(commandResultList);
	}
	



}
