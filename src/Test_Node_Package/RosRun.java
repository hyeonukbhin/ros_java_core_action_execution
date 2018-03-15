package Test_Node_Package;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.commons.logging.Log;
import org.mockito.internal.util.RemoveFirstLine;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.MessageFactory;
import org.ros.message.MessageSerializationFactory;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.namespace.NodeNameResolver;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeListener;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import Action_Execution_Node_Package.ActionExecutionNode;
import Action_Execution_Node_Package.CommandDef;
import PKG_Manager_Package.BaseProgramManager;
import PKG_Manager_Package.ProcessStateDef;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;






public class RosRun {
	


	public static void main(String[] args) throws IOException, InterruptedException {
		
		RosRun rosRun=new RosRun();
	
		
		 String[] interfaceLayer={"Test_Node_Package.InterfaceLayer"};	 
		 String[] actionExecutionPKG={"Action_Execution_Node_Package.ActionExecutionNode"};
		 
		 try {
	         //org.ros.RosRun.main(interfaceLayer);
	         org.ros.RosRun.main(actionExecutionPKG);
	
	          }  catch(Exception e) {
	   
	          }
		
		
		/*
		try {
			Runtime rt = Runtime.getRuntime();
			//Process interfaceProcess=rt.exec("rosrun care_robot_expression_gui CareRobotExpressionGUI.py");
			//Process interfaceProcess=rt.exec("rosrun gesture_profile_generator GeneratorNode.py");
			Process interfaceProcess=rt.exec("roslaunch gazebo_care_robot care_robot.launch");
			
			Thread.sleep(10000);
			//OutputStream stdin = interfaceProcess.getOutputStream();
			//BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
			//String sentence="^C";
			//writer.write(sentence);
			//writer.flush();
			//Runtime.getRuntime().exec("kill -f -SIGINT "+Integer.toString(pid));
			
			interfaceProcess.destroy();
			System.out.println("end result:");

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		try {
            //Process p = Runtime.getRuntime().exec("xterm");
			//Process p = new ProcessBuilder("xterm -hold -e \"echo Hello My World\"").start();
		
			Process p = Runtime.getRuntime().exec("sh /home/kist/rosjava_core/workspace/Action_Execution_Node_Package/Config/test.sh");
            Thread.sleep(5000);
            p.destroy();
            System.out.println("end result:");
        }
        catch (Exception io) {
            io.printStackTrace();
        }
        */
		
	}
		
	public boolean remove(Map<String,  PackageInfoDef.RootType> map)
	{
		if(map.containsKey("a"))
			map.remove("a");
		
		return true;
	}
		


}
