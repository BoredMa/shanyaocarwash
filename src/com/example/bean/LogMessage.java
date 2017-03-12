package com.example.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMessage {

	public static Logs packageMessage(String message,String money,String phone,String plateNum) {
		Logs logs = new Logs();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss"); 
		String time = format.format(new Date());
		logs.setDate(time);
		switch(message){
		case "rechargeCard":									
			logs.setDetail("充卡成功! \n\n\n您成功冲卡20次!");
			break;
		case "recharge":
			logs.setDetail("充值成功! \n\n\n您成功为账号为："+phone+" 的用户充值,充值金额："+money+"元!");
			break;
		case "helpfr":
			logs.setDetail("买单成功! \n\n\n您成功为车牌号码为："+plateNum+" 的车主买单一次,情谊地久天长！");
			break;	
		case "washmycar":
			logs.setDetail("付款成功! \n\n\n您成功为车牌号码为："+plateNum+" 的爱车付款一次，欢迎下次光临！");
			break;	
		}
		
		return logs;
	}
}
