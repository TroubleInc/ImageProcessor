public class Runner{
	public static void main(String [] args){
		if(args.length < 1){
			new ImageProcess.Processor((String)null);
		} else {
			new ImageProcess.Processor(args[0]);
		}
	}
}
