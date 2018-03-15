package Package_Info_Package;

import java.util.ArrayList;

public class PackageInfoDef {
	
	public final static String ID="Id";

	public static enum RootType{
		COMMON("Common_Package"),LAUNCH("Launch_Package"), 
		PROGRAM("Base_Program"), INTERFACE("Interface_Package");		
		
		private String value;
		
		RootType(String value){
			this.value = value;
		}
		
		public String getStr(){
			return value;	
		}
		
	}
	
	public static ArrayList<RootType> getList()
	{
		ArrayList<RootType> list=new ArrayList<RootType>();
		for(RootType type:RootType.values())
			list.add(type);
		
		return list;
	}


	public static enum KillCommandType{
		NODE("NODE_KILL"),PROCESS("PROCESS_KILL");		
		
		private String value;
		
		KillCommandType(String value){
			this.value = value;
		}
		
		public String getStr(){
			return value;	
		}
	
    }
	

}
