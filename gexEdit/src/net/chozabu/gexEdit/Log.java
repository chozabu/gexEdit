package net.chozabu.gexEdit;

public class Log {
	/** Get the current line number.
	 * @return int - Current line number.
	 */
	
	
	public static void o(String in){
		System.out.println(in);//+" "+getLineNumber());
	}

	public static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}
}
