package Test_Node_Package;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import Action_Execution_Node_Package.ActionExecutionNode;




public class RosRun {

	public static void main(String[] args) {
		/*
		
		 String[] interfaceLayer={"Test_Node_Package.InterfaceLayer"};	 
		 String[] actionExecutionPKG={"Action_Execution_Node_Package.ActionExecutionNode"};
		 RosRun rosRun=new RosRun();
		 try {
	         org.ros.RosRun.main(interfaceLayer);
	         //org.ros.RosRun.main(actionExecutionPKG);
	          }  catch(Exception e) {
	   
	          }
		*/
		
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
		
	}
		
	
		


}
