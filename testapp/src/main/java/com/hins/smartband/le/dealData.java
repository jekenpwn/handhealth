package com.hins.smartband.le;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.hins.smartband.database.BindDeviceDAO;
import com.hins.smartband.database.SpaceDAO;
import com.hins.smartband.model.Tb_bind_device_m;
import com.hins.smartband.model.Tb_space_m;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class dealData {
	private Map<String,String> map=new HashMap<String,String>();
	private String[] str={"temperature","heart","blood_oxygen","body_state","running","sleeptime","makeup","Danger"};//8个
	private StringBuffer sb;
	private Context mcontext;
	int t=0;
	public dealData(String nameone,String addressone,String addresstwo,Context context){
		super();
		this.mcontext=context;
		sb=new StringBuffer();
		sb.append(addresstwo);
		dealTwoDevAdd(nameone,addressone);
	}
	public dealData(String string,Context context){
		super();
		this.mcontext=context;
		sb=new StringBuffer();
		sb.append(string);

	}
	public void deal(){
		while(sb.length()!=0){
			String st=sb.substring(0, 1);//第一个字母的是什么
			int m=sb.indexOf(st)+1;//第一个字母的是什么
			int i=sb.indexOf(st,m);//跟第一个字母相同的字母的位置
			String st2=sb.substring(m, i);//得到36.40
			//===================增强版2.2=============
			if(t==5||t==6){
				StringBuilder sb2=new StringBuilder(st2);
				sb2.insert(2, ":");
				map.put(str[t], sb2.toString());
			}else{
				map.put(str[t], st2);
			}
			//===================增强版2.2=============
			sb.delete(sb.indexOf(st),i+1);
			t++;
		}
		if(map.size()==8) {
			//当数据异常时不保存
			String time = new NowTime().nowTime();
			SpaceDAO spaceDao = new SpaceDAO(mcontext);
			spaceDao.add(new Tb_space_m(map.get(str[0]), map.get(str[1]), map.get(str[2]), map.get(str[3]),
					map.get(str[4]), map.get(str[5]), map.get(str[6])
					, map.get(str[7]), time));
			map.clear();
			t = 0;
			Intent intent = new Intent();
			intent.setAction("Hins");
			mcontext.sendBroadcast(intent);
		}

	}
	//00:15:83:00:88:A4
	private void dealTwoDevAdd(String nameone,String addressone){
		String st=sb.substring(0, 1);//第一个字母的是什么
		int m=sb.indexOf(st)+1;//第一个字母的是什么
		int i=sb.indexOf(st,m);//跟第一个字母相同的字母的位置
		String st2=sb.substring(m, i);//得到36.40
		BindDeviceDAO bindDeviceDao=new BindDeviceDAO(mcontext);
		bindDeviceDao.add(new Tb_bind_device_m(nameone,addressone,st2));
	}
}


