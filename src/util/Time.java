package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;


public class Time{
	public static final String TIME_SERVER = "time-a.nist.gov";

	public String getTime(){
		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress;
		TimeInfo timeInfo=null;
		
		try {
			inetAddress = InetAddress.getByName(TIME_SERVER);
			timeInfo = timeClient.getTime(inetAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long returnTime = timeInfo.getReturnTime();
		Date time = new Date(returnTime);
		return ""+time;
	
	}
		
}
