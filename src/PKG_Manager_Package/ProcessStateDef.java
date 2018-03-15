package PKG_Manager_Package;


public class ProcessStateDef {
	
	
	
	public static enum State{
		PROCESS_ON(0), PROCESS_OFF(1), 
		TOPIC_ERROR(2), PROCESS_ERROR(3), NONE_ERROR(4),
		NONE_TAG(5);
		
		private int mState;
		
		State(int state) {
			this.mState=state;
		}
		
		public int getInt()
		{
			return mState;
		}
		
		
	}
	
	public static ProcessStateDef.State fromInt(int error)
	{
		State stateList[]=ProcessStateDef.State.values();
		
		return stateList[error];
	}

}
