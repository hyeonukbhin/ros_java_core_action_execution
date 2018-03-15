package PKG_Manager_Package;

import org.ros.node.ConnectedNode;

import PKG_Manager_Package.ProcessStateDef.State;
import Package_Info_Package.PackageInfoDef;
import Package_Info_Package.PackageInfoManager;
import action_execution_msgs.Package;

public class ControlFunctionFactory{
	
	private static CommonTypeManager mCommonManager;
	private static LaunchTypeManager mLaunchManager;
	private static BaseProgramManager mProgramManager;
	
	private static IControlFunction mFactory;
	
	public ControlFunctionFactory() {
		mCommonManager=CommonTypeManager.getInstance();
		mLaunchManager=LaunchTypeManager.getInstance();
		mProgramManager=BaseProgramManager.getInstance();
	}
	
	static public IControlFunction getInstance()
	{
		if(mFactory==null)
			mFactory=new LaunchTypeManager();
		
		return mFactory;
		
		
	}
	
	public IControlFunction getInstanceFromType(PackageInfoDef.RootType type){
		if(type.equals(PackageInfoDef.RootType.COMMON))
			mFactory=mCommonManager;
		else if(type.equals(PackageInfoDef.RootType.LAUNCH))
			mFactory=LaunchTypeManager.getInstance();
		else if(type.equals(PackageInfoDef.RootType.PROGRAM))
			mFactory=mProgramManager;
		
		return mFactory;
	}

	

}
