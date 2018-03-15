package Package_Info_Package;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import PKG_Manager_Package.ControlFunctionFactory;
import PKG_Manager_Package.IControlFunction;
import PKG_Manager_Package.ProcessStateDef.State;


public class PackagePlanner{
	private ControlFunctionFactory mFactory;
	private PackageInfoManager mPkgInfoManager;
	private static PackagePlanner mInstance;

	
	PackagePlanner() {
		mPkgInfoManager=PackageInfoManager.getInstance();
		mFactory = new ControlFunctionFactory();
	}
	
	static public PackagePlanner getInstance()
	{
		if(mInstance==null)
			mInstance=new PackagePlanner();
		
		return mInstance;
		
	}
	
	public boolean onPreStartPkg(Map<String,  PackageInfoDef.RootType> map, PackageInfoDef.RootType type, String id)
	{
		try{
			
			Queue<String> orderList=getPreconditionList(type, id);
			while(orderList.isEmpty()==false)
			{
				String preStartName=orderList.poll();
				PackageInfoDef.RootType preStartType=mPkgInfoManager.getType(preStartName);
				IControlFunction manager=mFactory.getInstanceFromType(preStartType);
				if(manager.onGetAtciveState(preStartName).equals(State.PROCESS_OFF))
				{
					if(manager.onStart(preStartName)==false)
						return false;
					
					map.put(preStartName, preStartType);
				}
				
			}
		}catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean onPreDestroyPkg(Map<String,  PackageInfoDef.RootType> map,PackageInfoDef.RootType type, String id)
	{
		try{
			Queue<String> orderList=getPreconditionList(type, id);
			while(orderList.isEmpty()==false)
			{
				String preStartName=orderList.poll();
				PackageInfoDef.RootType preStartType=mPkgInfoManager.getType(preStartName);
				IControlFunction manager=mFactory.getInstanceFromType(preStartType);
				if(manager.onGetAtciveState(preStartName).equals(State.PROCESS_ON))
				{
					if(manager.onDestroy(preStartName)==false)
						return false;
					
					if(map.containsKey(preStartName)==true)
						map.remove(preStartName);
				}
				
			}
		}catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	

	
	public boolean onEffDestroyPkg(Map<String,  PackageInfoDef.RootType> map, PackageInfoDef.RootType type,String id)
	{
		try{

			Queue<String> orderList=getEffectList(type, id);
			while(orderList.isEmpty()==false)
			{
				String preStartName=orderList.poll();
				PackageInfoDef.RootType preStartType=mPkgInfoManager.getType(preStartName);
				IControlFunction manager=mFactory.getInstanceFromType(preStartType);
				if(map.containsKey(preStartName)==true)
				{
					if(manager.onGetAtciveState(preStartName).equals(State.PROCESS_ON)==true)
					{
						if(manager.onDestroy(preStartName)==false)
						{
							return false;
						}
						else
						{
							map.remove(preStartName);
						}
							
					}
				}
				
			}
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	private Queue<String> getPreconditionList(PackageInfoDef.RootType type, String id)
	{
		Queue<String> queue=new LinkedList<String>();
		ArrayList<String> list=getPrecondition(type, id);
		int sizeOfList=list.size();
		for(int index=0; index<sizeOfList; index++)
		{
			queue.add(list.get(index));
		}
		
		return queue;
	}
	
	private Queue<String> getEffectList(PackageInfoDef.RootType type, String id)
	{
		Queue<String> queue=new LinkedList<String>();
		ArrayList<String> list=getEffect(type, id);
		int sizeOfList=list.size();
		for(int index=0; index<sizeOfList; index++)
		{
			queue.add(list.get(index));
		}
		
		return queue;
	}

	private ArrayList<String> getPrecondition(PackageInfoDef.RootType type, String id)
	{
		ArrayList<String> list=new ArrayList<String>();
		
		if(type.equals(PackageInfoDef.RootType.COMMON)==true)
		{
			PackageInfo.CommonPackageInfo info=mPkgInfoManager.getCommonPkgInfo(id);
			list=info.precondition;
		}
		else if(type.equals(PackageInfoDef.RootType.LAUNCH)==true)
		{
			PackageInfo.LaunchPackageInfo info=mPkgInfoManager.getLaunchPkgInfo(id);
			list=info.precondition;
		}
		else if(type.equals(PackageInfoDef.RootType.PROGRAM)==true)
		{
			PackageInfo.BaseProgramInfo info=mPkgInfoManager.getProgramInfo(id);
			list=info.precondition;
		}
		
		return list;
	}
	
	private ArrayList<String> getEffect(PackageInfoDef.RootType type, String id)
	{
		ArrayList<String> list=new ArrayList<String>();
		
		if(type.equals(PackageInfoDef.RootType.COMMON)==true)
		{
			PackageInfo.CommonPackageInfo info=mPkgInfoManager.getCommonPkgInfo(id);
			list=info.effect;
		}
		else if(type.equals(PackageInfoDef.RootType.LAUNCH)==true)
		{
			PackageInfo.LaunchPackageInfo info=mPkgInfoManager.getLaunchPkgInfo(id);
			list=info.effect;
		}
		else if(type.equals(PackageInfoDef.RootType.PROGRAM)==true)
		{
			PackageInfo.BaseProgramInfo info=mPkgInfoManager.getProgramInfo(id);
			list=info.effect;
		}
		
		return list;
	}
}
