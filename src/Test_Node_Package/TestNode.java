package Test_Node_Package;

import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;

import PKG_Manager_Package.BaseProgramManager;
import PKG_Manager_Package.CommonTypeManager;
import PKG_Manager_Package.ControlFunctionFactory;
import PKG_Manager_Package.IControlFunction;
import PKG_Manager_Package.LaunchTypeManager;
import Package_Info_Package.PackageInfo;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;
import Package_Info_Package.PackageInfo.CommonPackageInfo;
import Package_Info_Package.PackageInfo.LaunchPackageInfo;

public class TestNode implements NodeMain{

	@Override
	public void onStart(ConnectedNode connectedNode) {
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
        MessageFactory messageFactory = nodeConfiguration.getTopicMessageFactory();
		action_execution_msgs.Package msg=messageFactory.newFromType(action_execution_msgs.Package._TYPE);
		msg.setId(1);
		
		
		String id="ros_motor_driver_test";
		ControlFunctionFactory managerP = new ControlFunctionFactory();
		IControlFunction manager=managerP.getInstanceFromType(PackageInfoDef.RootType.LAUNCH);
		manager.onInitialize(connectedNode);
		
	
		
		
		
		try{
			Thread.sleep(3000);
			//LaunchTypeManager manager=new LaunchTypeManager();
			System.out.println("onStart_Command: "+manager.onStart(id));
			//System.out.println("onExecute: "+manager.onExecute(id, msg));
			
			Thread.sleep(10000);
			
			System.out.println("onRecovery: "+ manager.onRecovery(id));
			
			Thread.sleep(7000);
			
			//Thread.sleep(10000);
		}catch(Exception e)
		{
			
		}
		
		
		System.out.println("onDestroy_Command: "+manager.onDestroy(id));
		
		
	}

	@Override
	public void onShutdown(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdownComplete(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Node node, Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("Test_Node_Package/TestNode");
	}

}
