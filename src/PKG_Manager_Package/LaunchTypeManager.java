package PKG_Manager_Package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;

import Action_Execution_Node_Package.PackageInfoManager;



public class LaunchTypeManager {
	private MessageFactory mMessageFactory;
	private ConnectedNode mConnectedNode;
	private PackageInfoManager mPackageInfo=new PackageInfoManager();
	private Hashtable<String, Process> mProcessTable=new Hashtable<String, Process>();
	private Hashtable<String, Publisher<action_execution_msgs.Package>> mPublisherTable=new Hashtable<String, Publisher<action_execution_msgs.Package>>();
	private final String INTERFACE_ID="_interface";
	
	
	public LaunchTypeManager(ConnectedNode connectedNode){
		mConnectedNode=connectedNode;
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
	}
	
	public boolean onStartPackage(String packageName)
	{
		String runInterfaceCommand=mPackageInfo.getInterfaceRunCommand(mPackageInfo.LAUNCH_TYPE, packageName);
		String interfaceName=packageName+INTERFACE_ID;		
		String interfaceTopicName=mPackageInfo.getInterfaceTopicName(mPackageInfo.LAUNCH_TYPE, packageName);
	
		Publisher<action_execution_msgs.Package> publisher=mConnectedNode.newPublisher(interfaceTopicName, action_execution_msgs.Package._TYPE);
		mPublisherTable.put(interfaceName, publisher);
	
		String packageRunCommand=mPackageInfo.getPackageRunCommand(mPackageInfo.LAUNCH_TYPE, packageName);		
		try {
			Process interfaceProcess=Runtime.getRuntime().exec(runInterfaceCommand);
			mProcessTable.put(interfaceName, interfaceProcess);
			Process packageProcess=Runtime.getRuntime().exec(packageRunCommand);
			mProcessTable.put(packageName, packageProcess);
		}  catch (Exception e) {
			return false;
			}
	 
		
		return true;
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
	
	public boolean onDestroyPackage(String packageName)
	{
		String killType=mPackageInfo.getPackageKillCommand(mPackageInfo.LAUNCH_TYPE, packageName);
		if(killType.equals(PackageInfoManager.NODE_KILL)==true)
		{
			
			if(setNodeKill(packageName)==false)
			{
				return false;
			}
			
			String interfaceGraphName=mPackageInfo.getInterfaceGraphName(mPackageInfo.LAUNCH_TYPE, packageName);
			if(setNodeKill(interfaceGraphName)==false)
			{
				return false;
			}
		}
		else if(killType.equals(PackageInfoManager.PROCESS_KILL)==true)
		{
			
			if(setProcessKill(packageName)==false)
			{
				return false;
			}
			if(setProcessKill(packageName+INTERFACE_ID)==false)
			{
				return false;
			}
		}
		
		return true;
	}
	
	
	
	public boolean getOnActiveState(String packageName)
	{
		try {
			Vector<String> nodeGraphNameList=mPackageInfo.getSubscriberNodeGraphNameList(mPackageInfo.LAUNCH_TYPE, packageName);
			String interfaceGraphName=mPackageInfo.getInterfaceGraphName(mPackageInfo.LAUNCH_TYPE, packageName);
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
			
			return true;
			
		} catch (Exception e) {

			return false;
		}
	
	}
	
	public boolean getOffActiveState(String packageName)
	{
		try {
			Vector<String> nodeGraphNameList=mPackageInfo.getSubscriberNodeGraphNameList(mPackageInfo.LAUNCH_TYPE, packageName);
			String interfaceGraphName=mPackageInfo.getInterfaceGraphName(mPackageInfo.LAUNCH_TYPE, packageName);
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
	
	public void setDeleteTable(String name)
	{
		mProcessTable.remove(name);
		mProcessTable.remove(name+INTERFACE_ID);
	}
	
	private boolean setNodeKill(String name)
	{
		if(mProcessTable.containsKey(name)==true)
		{
			try {
				String graphName=mPackageInfo.getPackageGraphName(mPackageInfo.BASE_TYPE, name);
				String killCommand="rosnode kill /"+graphName;
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
}
