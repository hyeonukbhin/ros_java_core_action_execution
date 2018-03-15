package Test_Node_Package;


import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageFactory;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;


public class InterfaceLayer implements NodeMain{
	
	private ConnectedNode mNode;
	ParameterTree mParams;
	
@Override
public void onStart(ConnectedNode connectedNode) 
{
	mNode=connectedNode;
	String topicName="command_list";
    final Publisher publisher=connectedNode.newPublisher(topicName, action_execution_msgs.Command_list._TYPE);
	connectedNode.executeCancellableLoop(new CancellableLoop() {
    int id=1; 
    
    

   
   // CommonPackageInfo mPackageInfo; 

	     
    @Override
	      
    protected void setup() {
    	
    	

	      
    }
 
      @Override
      protected void loop() throws InterruptedException {

    	  NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
          MessageFactory messageFactory = nodeConfiguration.getTopicMessageFactory();
          
         
          

              
          /*    
          String sentence=new String();
          Scanner scan = new Scanner(System.in);    
          System.out.println("Interface Layer(taskName):");
          sentence = scan.nextLine();   // execution, on, Off        
          
          
          String command_type="execution";
          action_execution_msgs.Command_list commandListMsg=messageFactory.newFromType(action_execution_msgs.Command_list._TYPE);
         commandListMsg.setCommandType(command_type);
         commandListMsg.setId(id);

         List<action_execution_msgs.Package> packageList=new Vector<action_execution_msgs.Package>();
          action_execution_msgs.Package packageMsg=messageFactory.newFromType(action_execution_msgs.Package._TYPE);
          
          packageMsg.setPackageName("ros_robot_expression_skills");      
          List<action_execution_msgs.Data> dataList=new Vector<action_execution_msgs.Data>();
            
         
          action_execution_msgs.Data data1=messageFactory.newFromType(action_execution_msgs.Data._TYPE);
          data1.setDataName("current_mode");
          List<String> values1=new Vector<String>();
          values1.add("speech");
          data1.setDataValueList(values1);
          dataList.add(data1);
          
          action_execution_msgs.Data data2=messageFactory.newFromType(action_execution_msgs.Data._TYPE);
          data2.setDataName("next_mode");
          List<String> values2=new Vector<String>();
          values2.add("reactive"); //none
          data2.setDataValueList(values2);
          dataList.add(data2);
          
          action_execution_msgs.Data data3=messageFactory.newFromType(action_execution_msgs.Data._TYPE);
          data3.setDataName("target");
          List<String> values3=new Vector<String>();
          values3.add("speech"); 
          values3.add("gesture"); 
          values3.add("face"); 
          data3.setDataValueList(values3);
          dataList.add(data3);
          
          action_execution_msgs.Data data4=messageFactory.newFromType(action_execution_msgs.Data._TYPE);
          data4.setDataName("user_personality_E");
          List<String> values4=new Vector<String>();
          values4.add("low"); 
          data4.setDataValueList(values4);
          dataList.add(data4);
          
          action_execution_msgs.Data data5=messageFactory.newFromType(action_execution_msgs.Data._TYPE);
          data5.setDataName("user_personality_ES");
          List<String> values5=new Vector<String>();
          values5.add("low"); 
          data5.setDataValueList(values5);
          dataList.add(data5);
          

          action_execution_msgs.Data data6=messageFactory.newFromType(action_execution_msgs.Data._TYPE);
          data6.setDataName("robot_sentence");
          List<String> values6=new Vector<String>();
          values6.add(sentence);
          data6.setDataValueList(values6);
          dataList.add(data6);
  
            
      packageMsg.setDataList(dataList);
      packageList.add(packageMsg);
      commandListMsg.setPackageList(packageList);  
      publisher.publish(commandListMsg);
        
  */
         
       Thread.sleep(500);
   
        id++;
	  


      }
    });
	
	String subFromMangerTopic="command_result_list";
	    Subscriber<action_execution_msgs.Command_result_list> interfaceSub2=connectedNode.newSubscriber(subFromMangerTopic, action_execution_msgs.Command_result_list._TYPE);
	    interfaceSub2.addMessageListener(new MessageListener<action_execution_msgs.Command_result_list>() {
			@Override
			public void onNewMessage(action_execution_msgs.Command_result_list message) {
				System.out.println("#InterfaceLayer[Recieve Msg] --> Command_result_list (Id:)"+message.getId());
			}
		});
	

}

@Override
public void onShutdown(Node node) {
	// TODO Auto-generated method stub
	
}

@Override
public void onShutdownComplete(Node node) {
	System.out.println("#InterfaceLayer--> onShutdown");
}

@Override
public void onError(Node node, Throwable throwable) {
	// TODO Auto-generated method stub
	
}

@Override
public GraphName getDefaultNodeName() {
	return GraphName.of("Test_Node_Package/InterfaceLayer");
}
}
