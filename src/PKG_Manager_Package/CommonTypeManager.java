package PKG_Manager_Package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;

import Action_Execution_Node_Package.PackageInfoManager;

public class CommonTypeManager {
	private MessageFactory mMessageFactory;
	private ConnectedNode mConnectedNode;
	private PackageInfoManager mPackageInfo=new PackageInfoManager();
	private Hashtable<String, Process> mProcessTable=new Hashtable<String, Process>();
	private Hashtable<String, Publisher<action_execution_msgs.Package>> mPublisherTable=new Hashtable<String, Publisher<action_execution_msgs.Package>>();
	private final String INTERFACE_ID="_interface";
	
	public CommonTypeManager(ConnectedNode connectedNode)
	{
		mConnectedNode=connectedNode;
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
	}
	
	public boolean getOnActiveState(String packageName)
	{
		try {
			Vector<String> nodeGraphNameList=mPackageInfo.getNodeGraphNameList(mPackageInfo.COMMON_TYPE, packageName);
			System.out.println("nodeName: "+nodeGraphNameList.size());
			String interfaceGraphName=mPackageInfo.getInterfaceGraphName(mPackageInfo.COMMON_TYPE, packageName);
			System.out.println("interfaceGraphName: "+interfaceGraphName);
			nodeGraphNameList.add(interfaceGraphName);
					
			Process process=Runtime.getRuntime().exec("rosnode list");
			BufferedReader screen=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String screenStr=new String();
			Hashtable<String,Boolean> screenNodeMap=new Hashtable<String,Boolean>();
			while((screenStr=screen.readLine())!=null)
			{
				screenNodeMap.put(screenStr, true);
			}

			for(int numOfNode=0; numOfNode<nodeGraphNameList.size(); numOfNode++)
			{
				String graphName="/"+nodeGraphNameList.get(numOfNode);
				if(screenNodeMap.containsKey(graphName)==false)
				{
					screen.close();
					return false;
				}
				
			}
			screen.close();
			System.out.println("true: ");
			return true;
			
		} catch (Exception e) {

			return false;
		}
	
	}
	
	public boolean onStartPackage(String packageName)
	{
		String runInterfaceCommand=mPackageInfo.getInterfaceRunCommand(mPackageInfo.COMMON_TYPE, packageName);
		String interfaceName=packageName+INTERFACE_ID;		
		String interfaceTopicName=mPackageInfo.getInterfaceTopicName(mPackageInfo.COMMON_TYPE, packageName);
	
		Publisher<action_execution_msgs.Package> publisher=mConnectedNode.newPublisher(interfaceTopicName, action_execution_msgs.Package._TYPE);
		mPublisherTable.put(interfaceName, publisher);

		try {
			Process interfaceProcess=Runtime.getRuntime().exec(runInterfaceCommand);
			mProcessTable.put(interfaceName, interfaceProcess);
			
			Vector<String> nodeGraphNameList=mPackageInfo.getNodeGraphNameList(mPackageInfo.COMMON_TYPE, packageName);
			Vector<String> nodeRunCommandList=mPackageInfo.getNodeRunCommandList(mPackageInfo.COMMON_TYPE,packageName);
			
			for(int numOfNode=0; numOfNode<nodeGraphNameList.size(); numOfNode++)
			{
				String nodeRunCommand=nodeRunCommandList.get(numOfNode);
				String nodeName=nodeGraphNameList.get(numOfNode);
				Process packageProcess=Runtime.getRuntime().exec(nodeRunCommand);
				mProcessTable.put(nodeName, packageProcess);
				Thread.sleep(300);
			}
			
			
		}  catch (Exception e) {
			return false;
			}
	 
		
		return true;
	}
	
	public boolean getOffActiveState(String packageName)
	{
		try {
			Vector<String> nodeGraphNameList=mPackageInfo.getNodeGraphNameList(mPackageInfo.COMMON_TYPE, packageName);
			String interfaceGraphName=mPackageInfo.getInterfaceGraphName(mPackageInfo.COMMON_TYPE, packageName);
			nodeGraphNameList.add(interfaceGraphName);
					
			Process process=Runtime.getRuntime().exec("rosnode list");
			BufferedReader screen=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String screenStr=new String();
			Hashtable<String,Boolean> screenNodeMap=new Hashtable<String,Boolean>();
			while((screenStr=screen.readLine())!=null)
			{
				screenNodeMap.put(screenStr, true);
				System.out.println("screen: "+ screenStr);
			}

			for(int numOfNode=0; numOfNode<nodeGraphNameList.size(); numOfNode++)
			{
				String graphName="/"+nodeGraphNameList.get(numOfNode);
				if(screenNodeMap.containsKey(graphName)==true)
				{
					screen.close();
					return true;
				}
				
			}
			screen.close();
			return false;
			
		} catch (Exception e) {

			return false;
		}
	
	}
	
	public boolean onDestroyPackage(String packageName)
	{
		String interfaceKillCommand=mPackageInfo.getInterfaceKillCommand(mPackageInfo.COMMON_TYPE, packageName);
		if(interfaceKillCommand.equals(mPackageInfo.NODE_KILL)==true)
		{
			String interfaceGraphName=mPackageInfo.getInterfaceGraphName(mPackageInfo.COMMON_TYPE, packageName);
			if(setNodeKill(interfaceGraphName)==false)
			{
				return false;
			}
		}
		else
		{
			String interfaceName=packageName+INTERFACE_ID;	
			if(setProcessKill(interfaceName)==false)
			{
				return false;
			}
		}
		
		Vector<String> nodeKillTypeList=mPackageInfo.getNodeKillCommandList(mPackageInfo.COMMON_TYPE, packageName);
		Vector<String> nodeGraphNameList=mPackageInfo.getNodeGraphNameList(mPackageInfo.COMMON_TYPE, packageName);
		
		for(int numOfNode=0; numOfNode<nodeKillTypeList.size(); numOfNode++)
		{
			String killType=nodeKillTypeList.get(numOfNode);
			String graphName=nodeGraphNameList.get(numOfNode);
			if(killType.equals(PackageInfoManager.NODE_KILL)==true)
			{
				if(setNodeKill(graphName)==false)
				{
					return false;
				}
			}
			else if(killType.equals(PackageInfoManager.PROCESS_KILL)==true)
			{
				if(setProcessKill(graphName)==false)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void setDeleteTable(String packageName)
	{
		Vector<String> nodeGraphNameList=mPackageInfo.getNodeGraphNameList(packageName, mPackageInfo.COMMON_TYPE);
		mProcessTable.remove(packageName+INTERFACE_ID);
		
		for(int numOfNode=0; numOfNode<nodeGraphNameList.size(); numOfNode++)
		{
			mProcessTable.remove(nodeGraphNameList.get(numOfNode));
		}
	}
	
	private boolean setNodeKill(String nodeName)
	{
		if(mProcessTable.containsKey(nodeName)==true)
		{
			try {
				String killCommand="rosnode kill /"+nodeName;
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
	
	private boolean setProcessKill(String name)
	{
		if(mProcessTable.containsKey(name)==true)
		{
			Process process=mProcessTable.get(name);
			process.destroy();				
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean onSendMSGToNode(String nodeName, action_execution_msgs.Package packageMsg)
	{
		Publisher<action_execution_msgs.Package> publisher=mPublisherTable.get(nodeName+INTERFACE_ID);
		if(publisher.hasSubscribers()==false || publisher == null)
		{
			return false;
		}
		{
			publisher.publish(packageMsg);
			return true;
		}
		
	}

}
