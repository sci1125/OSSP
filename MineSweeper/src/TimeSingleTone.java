public class TimeSingleTone {
	private static TimeSingleTone timeSingleTone = null;	
	private GameTimeListener mListener;
	private TimeSingleTone() {		}
	private Thread playTimeThread; // 시간을 재는 타임 스레드
    private int playTimeSecond = 0; // 시간 초       

	public static TimeSingleTone getInstance() {
		if(timeSingleTone == null)
			timeSingleTone = new TimeSingleTone();
		return timeSingleTone;
	}	
	
	public void setListener(GameTimeListener _listener) {
		mListener = _listener;
	}
	
	public void setControlThread(boolean optionThread) {    	
		if (optionThread) {
		// 타임 스레드 생성
			if (playTimeThread == null) {
				playTimeThread = new Thread(new Runnable() {
				@Override
				public void run() { 		
					while (!Thread.currentThread().isInterrupted()) {
						try {
							// 1초 딜레이
							Thread.sleep(1000);
							playTimeSecond++; 					 				
							mListener.onTimeTick(playTimeSecond);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}								
					}					
				}
			}); 
				playTimeThread.start();    		
			}   	    	        	
		}    			
		else {
			if (!playTimeThread.isInterrupted()) {
				playTimeThread.interrupt();
				playTimeThread = null;
			}        		
		}
	}

	public void setUpdateTime(int playTimeSecond) {
		this.playTimeSecond = playTimeSecond;
	}
	
	public int getUpdateTime() {
		return this.playTimeSecond;
	}
}
