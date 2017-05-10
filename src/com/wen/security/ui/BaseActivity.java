package com.wen.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.R;
import com.wen.security.beans.MenberInfo;
import com.wen.security.serial.TaskMG;
import com.wen.security.serial.TaskMG.IStoreObjectStatus;
import com.wen.security.serial.TaskMG.ITemper;
import com.wen.security.ui.view.LoginDialog;
import com.wen.security.utils.FileCache;
import com.wen.security.utils.RTools;

public class BaseActivity extends Activity implements OnClickListener {
	private final String TAG = java.lang.System.currentTimeMillis()+"";
	// protected SecurityApplication sApp;
	protected FileCache fileCache;

	protected ImageView af_user_icon_img;
	protected TextView af_user_name_txt;
	protected TextView af_user_from_txt;
	protected ImageButton af_change_btn;
	// protected ImageButton af_lock_btn;
	protected ImageButton af_setting_btn;
	protected ImageButton af_help_btn;
	public ImageButton ac_top_back;

	protected void initRes() {
		fileCache = new FileCache(this);
		af_user_icon_img = (ImageView) findViewById(R.id.af_user_icon_img);
		af_user_name_txt = (TextView) findViewById(R.id.af_user_name_txt);
		af_user_from_txt = (TextView) findViewById(R.id.af_user_from_txt);
		af_change_btn = (ImageButton) findViewById(R.id.af_change_btn);
		// af_lock_btn = (ImageButton) findViewById(R.id.af_lock_btn);
		af_setting_btn = (ImageButton) findViewById(R.id.af_setting_btn);
		af_help_btn = (ImageButton) findViewById(R.id.af_help_btn);

		ac_top_temper_txt = (TextView) findViewById(R.id.ac_top_temper_txt);
		ac_top_power_txt = (TextView) findViewById(R.id.ac_top_power_txt);
		ac_top_net_txt = (TextView) findViewById(R.id.ac_top_net_txt);
		ac_top_date_txt = (TextView) findViewById(R.id.ac_top_date_txt);
		ac_top_back = (ImageButton) findViewById(R.id.ac_top_back);
		ac_top_date_txt.setText(RTools.getTimeToD());
		ac_top_net_txt.setText(RTools.isNet(getApplicationContext()));

		if (DataCache.getOne().sMenberInfo != null) {
			//当前登录用户信息
			initView(DataCache.getOne().sMenberInfo);
		}

	}

	/**
	 * 注册警报
	 */
	protected void registerAlarm() {

		TaskMG.getOne().registerTemper(TAG, new ITemper() {

			@Override
			public void onTemper(String id, String temper) {
				// TODO Auto-generated method stub
				int ldoor = Integer.parseInt(temper.substring(0, 1));
				int lpower = Integer.parseInt(temper.substring(0, 2)) % 10;
				int temp = Integer.parseInt(temper.substring(2));
				if (temp > 60) {
					DataFunc.addAlarmLog(BaseActivity.this,"温度异常！报警温度：" + temper);
				}
				ac_top_temper_txt.setText("温度：" + temper.substring(2) + "℃");

				if (lpower == 1) {
					ac_top_power_txt.setText("电池供电");
					DataFunc.addAlarmLog(BaseActivity.this,"供电异常！");
				} else {
					ac_top_power_txt.setText("市电正常");
				}

				if (ldoor == 1) {
					//异常开门
					DataFunc.addAlarmLog(BaseActivity.this,"非法开门！门序列号为：0");
				} else if (ldoor == 2) {
					DataFunc.addAlarmLog(BaseActivity.this,"门超时末关！");
				}
			}
		});
/*		TaskMG.getOne().registerBadOpen("BaseActivity",
				new IStoreObjectStatus() {

					@Override
					public void onStatus(String id, String addr) {
						// TODO Auto-generated method stub
						DataFunc.addAlarmLog("非法开门！门序列号为：" + addr);
					}
				});
				*/
		TaskMG.getOne().registerBadGetGun(TAG,
				new IStoreObjectStatus() {

					@Override
					public void onStatus(String id, String addr) {
						// TODO Auto-generated method stub
						DataFunc.addAlarmLog(BaseActivity.this,"非法领枪!枪地址是：" + addr);
					}
				});
	/*	TaskMG.getOne().registerBadGetAmmo("ActivityGo",
				new IStoreObjectStatus() {

					@Override
					public void onStatus(String id, String addr) {
						// TODO Auto-generated method stub
						DataFunc.addAlarmLog("非法领弹!弹地址是：" + addr);
					}
				});
		TaskMG.getOne().registerBadGetAmmoSets("ActivityGo",
				new IStoreObjectStatus() {

					@Override
					public void onStatus(String id, String addr) {
						// TODO Auto-generated method stub
						DataFunc.addAlarmLog("非法领弹夹!弹夹地址是：" + addr);
					}
				});

		TaskMG.getOne().registerZHUDian(TAG, new IStoreObjectStatus() {

			@Override
			public void onStatus(String id, String addr) {
				// TODO Auto-generated method stub
				DataFunc.addAlarmLog("变化触点地址是：" + addr);
			}
		});*/

	}

	protected void initView(MenberInfo menberInfo) {
		if (menberInfo != null) {
			// sApp.sLoader.DisplayImage(menberInfo.User_Photo,
			af_user_icon_img.setImageBitmap(RTools.getBitmap(menberInfo.User_Photo));
			af_user_name_txt.setText("管理员：" + menberInfo.User_Name);
			af_user_from_txt.setText(menberInfo.Department);
		}
	}

	@Override
	protected void onDestroy() {
		TaskMG.getOne().removeAlwaysTask(TAG);
		//DataFunc.saveAndSendLog();
		super.onDestroy();
	}

	protected TextView ac_top_temper_txt;
	protected TextView ac_top_power_txt;
	protected TextView ac_top_net_txt;
	protected TextView ac_top_date_txt;

	protected void initEvent() {

		af_user_icon_img.setOnClickListener(this);
		af_user_name_txt.setOnClickListener(this);
		af_user_from_txt.setOnClickListener(this);
		af_change_btn.setOnClickListener(this);
		// af_lock_btn.setOnClickListener(this);
		af_setting_btn.setOnClickListener(this);
		af_help_btn.setOnClickListener(this);

		ac_top_temper_txt.setOnClickListener(this);
		ac_top_power_txt.setOnClickListener(this);
		ac_top_net_txt.setOnClickListener(this);
		ac_top_date_txt.setOnClickListener(this);
		ac_top_back.setOnClickListener(this);
	}

	public void back() {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_top_back:
				back();
				break;
			case R.id.af_user_icon_img:

				break;
			case R.id.af_user_name_txt:

				break;
			case R.id.af_user_from_txt:

				break;
			case R.id.af_change_btn: //管理员交接班
				LoginDialog dialog = new LoginDialog(this);
				dialog.show();
				break;

			case R.id.af_setting_btn:
				//设置
				startActivity(new Intent(this,SettingsActivity.class));
				break;
			case R.id.af_help_btn: //帮助
				// TaskMG.getOne().removeAlwaysTask("BaseActivity");
				//重启程序
//				String packageName = getBaseContext().getPackageName();
//				Log.i(TAG,"packageName:"+packageName);
//				Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(packageName);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
				//重启系统
//				Intent intent =new Intent(Intent.ACTION_REBOOT);
//				intent.putExtra("nowait",1);
//				intent.putExtra("interval",1);
//				intent.putExtra("window",0);
//				sendBroadcast(intent);

//				PowerManager pm = (PowerManager) getSystemService(Service.POWER_SERVICE);
//				pm.reboot("recovery");
				//关机
//				String ACTION_REQUEST_SHUTDOWN ="android.intent.action.ACTION_REQUEST_SHUTDOWN";
//				String EXTRA_KEY_CONFIRM ="android.intent.extra.KEY_CONFIRM";
//				Intent i =new Intent(ACTION_REQUEST_SHUTDOWN);
//				i.putExtra(EXTRA_KEY_CONFIRM,false);
//				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(i);

				break;
			case R.id.ac_top_temper_txt:

				break;
			case R.id.ac_top_power_txt:

				break;
			case R.id.ac_top_net_txt:

				break;
			case R.id.ac_top_date_txt:

				break;

			default:
				break;
		}
	}

}
