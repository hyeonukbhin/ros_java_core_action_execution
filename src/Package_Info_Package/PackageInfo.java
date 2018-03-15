package Package_Info_Package;

import java.util.ArrayList;

import Package_Info_Package.PackageInfo.InterfacePackageInfo;

public class PackageInfo {
	
	public static class CommonPackageInfo{
		public String id;
		public Boolean boot;
		public String interfaceId;
		public int loadTimeOut;
		public int executionTimeOut;
		public ArrayList<String> precondition=new ArrayList<String>();
		public ArrayList<String> effect=new ArrayList<String>();
		public ArrayList<SubNodeInfo> subNodeInfoList=new ArrayList<SubNodeInfo>();
		
	}
	
	public static class LaunchPackageInfo{
		public String id;
		public Boolean boot;
		public String runCommand;
		public String interfaceId;
		public int loadTimeOut;
		public int executionTimeOut;
		public ArrayList<String> precondition=new ArrayList<String>();
		public ArrayList<String> effect=new ArrayList<String>();
		public ArrayList<String> subNodeGraphNameList=new ArrayList<String>();
		
	}
	
	public static class BaseProgramInfo{
		public String id;
		public String processName;
		public Boolean boot;
		public int loadTimeOut;
		public String runCommand;
		public ArrayList<SubProcess> subProcessList=new ArrayList<SubProcess>();
		public ArrayList<String> precondition=new ArrayList<String>();
		public ArrayList<String> effect=new ArrayList<String>();
	}
	
	public static class InterfacePackageInfo{
		public String id;
		public String nodeName;
		public String topicName;
		public int loadTimeOut;
		public String runCommand;
		public String killCommandType;
		
	}
	
	public static class SubNodeInfo{
		public String id;
		public String nodeName;
		public String runCommand;
		public String killCommandType;
	}
	

	
	public static class SubProcess{
		public String processName;
		public int numOfprocess;
	}


}
