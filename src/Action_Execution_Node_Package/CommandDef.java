package Action_Execution_Node_Package;




public class CommandDef {
	
	public static int ERROR_COMMAND_ID=-1;
	
	public static enum Type{
		
		PACKAGE_ON("on"),PACKAGE_OFF("off"), PACKAGE_EXECUTION("execution"), 
		PACKAGE_RECOVERY("recovery"), PACKAGE_STOP("stop");		

		private String value;
		
		Type(String value)
		{
			this.value = value;
		}
		
		public String getStr(){
			return value;
		}
		
		
	}
	
	public static Type getCommandType(String command)
	{
		for(Type type:Type.values())
		{
			if(command.equals(type.getStr()))
				return type;
		}
		
		return null;
		
	}

	

}
