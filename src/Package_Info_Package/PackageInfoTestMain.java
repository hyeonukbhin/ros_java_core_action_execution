package Package_Info_Package;

import java.util.ArrayList;

import Package_Info_Package.PackageInfoDef.RootType;

public class PackageInfoTestMain {
	
	void jsonLoadTest()
	{
		PackageInfoManager manager=PackageInfoManager.getInstance();
		PackageInfo.CommonPackageInfo common=manager.getCommonPkgInfo("ros_robot_expression_skills");
		PackageInfo.LaunchPackageInfo launch=manager.getLaunchPkgInfo("ros_gazebo_gesture");
		PackageInfo.BaseProgramInfo program=manager.getProgramInfo("ros_myBom_face_blue");
		PackageInfo.InterfacePackageInfo interfacePkg=manager.getInterfaceInfo("ros_interface_robot_expression_skills");
	}
	
	void printDef()
	{
	
		System.out.println("PackageInfoDef.RootType.COMMON: "+PackageInfoDef.RootType.COMMON); //->COMMON
		System.out.println("PackageInfoDef.RootType.COMMON.getStr() :"+PackageInfoDef.RootType.COMMON.getStr()); //->Command_Package
		System.out.println("PackageInfoDef.ID: "+PackageInfoDef.ID); //->Id
		
	}

	public static void main(String[] args) {		
		PackageInfoTestMain test=new PackageInfoTestMain();
		test.printDef();
		test.jsonLoadTest();
		

	}

}
