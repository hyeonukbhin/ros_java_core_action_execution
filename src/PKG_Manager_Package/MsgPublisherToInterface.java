package PKG_Manager_Package;

import java.util.Hashtable;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;

public class MsgPublisherToInterface {
	
	
	private static MsgPublisherToInterface mInstance;
	private MessageFactory mMessageFactory;
	private ConnectedNode mConnectedNode;
	private Hashtable<String, Publisher<action_execution_msgs.Package>> mPublisherTable=new Hashtable<String, Publisher<action_execution_msgs.Package>>();
	
	public MsgPublisherToInterface(ConnectedNode connectedNode) {
		mConnectedNode=connectedNode;
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
		mMessageFactory = nodeConfiguration.getTopicMessageFactory();
	}
	
	public static MsgPublisherToInterface getInstance(ConnectedNode connectedNode)
	{
		if(mInstance==null)
		{
			mInstance=new MsgPublisherToInterface(connectedNode);
		}
		
		return mInstance;
	}
	
	public boolean onCreatePublisher(String id, String interfaceTopicName)
	{
		try{
			if(isPublisher(id)==false)
			{
				Publisher<action_execution_msgs.Package> publisher=mConnectedNode.newPublisher(interfaceTopicName, action_execution_msgs.Package._TYPE);
				mPublisherTable.put(id, publisher);
			}
		
		}catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean onDestroyPublisher(String id)
	{
		try{
			if(isPublisher(id)==true)
				mPublisherTable.remove(id);
		}catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean onSendMsg(String id, action_execution_msgs.Package msg)
	{
		try
		{
			if(isPublisher(id)==true)
			{
				Publisher<action_execution_msgs.Package> publisher=mPublisherTable.get(id);
				publisher.setLatchMode(true);
				publisher.publish(msg);	
				/*
				if(publisher.hasSubscribers()==false || publisher == null)
					return false;
				else
					publisher.publish(msg);	
				*/
			}
			else
			{
				return false;
			}
				
					
		}
		catch(Exception e)
		{
			return false;
		}
		
		
		return true;
		
	}
	
	private boolean isPublisher(String id)
	{
		try{
			if(mPublisherTable.containsKey(id))
				return true;
			
		}catch(Exception e)
		{
			return false;
		}
		
		return false;
	}
	

}
