package PKG_Manager_Package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;


public abstract class IControlFunction {	
	
	//public final String PROCESS_KILL="PROCESS_KILL";
	//public final String NODE_KILL="NODE_KILL";

	public Process setCommand(String command)
	{
		try {
			return Runtime.getRuntime().exec(new String[] { "bash", "-c", command });
		} catch (IOException e) {
			return null;
		}
		
	}
	
	public ArrayList<String>  getConsoleScreen(String command)
	{	
		ArrayList<String> strList=new ArrayList<String>();
		try {
			String src=new String();
			Process process = Runtime.getRuntime().exec(new String[] { "bash", "-c", command });
			BufferedReader screen=new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			while((src=screen.readLine())!=null)
			{
				strList.add(src);
			}
			
			screen.close();
			process.destroy();
		} catch (IOException e) {
			return null;
		}
		
		return strList;
	}
	
	public boolean setProcessKill(Process process)
	{
		try {
			
			process.getInputStream().close();
			process.destroy();	
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public boolean setNodeKill(String nodeName)
	{
		try{
			String killCommand="rosnode kill /"+nodeName;
			Runtime.getRuntime().exec(new String[] { "bash", "-c", killCommand});			
		}catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean isActiveNode(String nodeName)
	{
		final String NODE_LIST_CHECK="rosnode list | grep "+nodeName;
		ArrayList<String> nodeList=getConsoleScreen(NODE_LIST_CHECK);
		
		int numOfNode=nodeList.size();
		for(int index=0; index<numOfNode; index++)
		{
			if(nodeList.get(index).equals("/"+nodeName))
				return true;
		}
		
		return false;
	}
	
	abstract public boolean onInitialize(ConnectedNode connectedNode);
	abstract public boolean onBoot(String id);
	abstract public boolean onStart(String id);
	abstract public boolean onExecute(String id, action_execution_msgs.Package msg);
	abstract public boolean onDestroy(String id);
	abstract public boolean onRecovery(String id);
	abstract public ProcessStateDef.State onGetAtciveState(String id);
	
}
