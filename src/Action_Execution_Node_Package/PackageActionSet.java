package Action_Execution_Node_Package;

import java.util.Hashtable;

public class PackageActionSet {
	
	public static enum Action{
		PACKAGE_ON(0),PACKAGE_OFF(1), PACKAGE_EXECUTION(2), PACKAGE_STOP(3),		

		NONE_ERROR(100), ERROR_CREATE_PKG(101),ERROR_DESTROY_PKG(102), ERROR_DATA_NULL(103), 
		ERROR_PKG(104),
		
		ERROR_TOPIC_NULL(200), ERROR_ID_NULL(201), ERROR_COMMAND_NULL(202), ERROR_PACKAGE_NAME_NULL(203),
		ERROR_ALREADY(300),
		
		ERROR_PACKAGE_PLAY(400);

		
		private int value;
		
		Action(int value)
		{
			this.value = value;
		}
		
		public int getIDAction(){
			return value;
		}
	}
	
	private Hashtable<String, PackageActionSet.Action> mActionTable;

	
	public PackageActionSet() {
		mActionTable=new Hashtable<String, PackageActionSet.Action>();
		onCreateCommandType();
	}
	
	private void onCreateCommandType()
	{
		//key=taskName, value=action
		mActionTable.put("on", PackageActionSet.Action.PACKAGE_ON);
		mActionTable.put("off", PackageActionSet.Action.PACKAGE_OFF);
		mActionTable.put("execution", PackageActionSet.Action.PACKAGE_EXECUTION);
		mActionTable.put("stop", PackageActionSet.Action.PACKAGE_STOP);
		
	}
	

	
	public PackageActionSet.Action getAction(String commandType)
	{		
		return mActionTable.get(commandType);
	}
	
	

}
