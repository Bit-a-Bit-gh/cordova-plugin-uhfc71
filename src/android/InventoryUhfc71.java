package it.dynamicid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

import com.rscja.deviceapi.RFIDWithUHF;
import com.zebra.adc.decoder.Barcode2DWithSoft;

public class InventoryUhfc71 {

	public Context  mContext;
	public RFIDWithUHF mReader; 
	public boolean loopFlag;
	public String strBarcode = new String();
	Barcode2DWithSoft barcode2DWithSoft;
	List<String> listaTags;
	private HashMap<String, String> map;
	private ArrayList<HashMap<String, String>> tagList;


	public InventoryUhfc71(Context context, long txpower) {
		super();
		mContext = context;
		loopFlag = false;
		listaTags = new ArrayList<String>();
		String stato = "0";
		strBarcode = "";
		boolean sav = false;
		barcode2DWithSoft = Barcode2DWithSoft.getInstance();
		try {
			mReader = RFIDWithUHF.getInstance();
			sav = mReader.init();
		} catch (Exception ex) {

		}

		try {
			mReader.setPower((int) txpower);			
		} catch (Exception ex) {
			stato += ex.toString();
		}
		
		
		//Toast.makeText(context, sav+"", Toast.LENGTH_LONG).show();		

		tagList = new ArrayList<HashMap<String, String>>();
		
		/*
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				String result = msg.obj + "";
				String[] strs = result.split("@");
				addEPCToList(strs[0],strs[1]);
				//mContext.playSound(1);
			}
		};
		*/
	}

	private void init() {
		boolean es = false;
		try {
			es = this.mReader.init();
		} catch (Exception e) {
			es = false;
		}
	}

	public Barcode2DWithSoft.ScanCallback  ScanBack= new Barcode2DWithSoft.ScanCallback(){
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            if (length < 1) {
                if (length == -1) {
                    strBarcode += "Scan cancel";
                } else if (length == 0) {
					strBarcode += "Scan TimeOut";
                } else {
                    //Log.i(TAG,"Scan fail");
                }
            }else{
                String barCode = "LISTO";


              //  String res = new String(dd,"gb2312");
                try {
                    //Log.i("Ascii",seldata);
                    barCode = new String(bytes, 0, length, "ASCII");
                }
                catch (UnsupportedEncodingException ex)   {}
                strBarcode = barCode;
            }

        }
    };

	public void ScanBarcode() {
		//if(barcode2DWithSoft!=null) {
            //Log.i(TAG,"ScanBarcode");

			barcode2DWithSoft.scan();
            barcode2DWithSoft.setScanCallback(ScanBack);
        //}
	}

	public void StopBarcode() {
		//if(barcode2DWithSoft!=null) {
            //Log.i(TAG,"ScanBarcode");

			barcode2DWithSoft.stopScan();
        //}
	}

	public String GetBarcode() {
		return this.strBarcode;
	}

	public void StartInventoryStream() {
		
		if (mReader.startInventoryTag((byte)0, (byte)0)) {	
			loopFlag = true;
			new TagThread(10).start();
		} else {
		}
	}


	/**
	 * Stop streaming.
	 */
	public void StopInventoryStream()
	{

		if (loopFlag) {

			loopFlag = false;

			if (mReader.stopInventory()) {
				//Stop OK
				mReader.free();
			} else {
				//Stop KO

			}

		}

	}

	public String GetTags() {
		String retval = "";    	

		for(int i=0; i<listaTags.size(); i++) {
			String epcString;
			epcString = listaTags.get(i);
			retval = retval + epcString + ",";           
		}   	

		return retval;
	}

	private void addEPCToList(String epc,String rssi) {
		if (!TextUtils.isEmpty(epc)) {
			int index = checkIsExist(epc);

			map = new HashMap<String, String>();

			map.put("tagUii", epc);
			map.put("tagCount", String.valueOf(1));
			map.put("tagRssi", rssi);

			if (index == -1) {
				tagList.add(map);

			} else {
				int tagcount = Integer.parseInt(tagList.get(index).get("tagCount"), 10) + 1;
				map.put("tagCount", String.valueOf(tagcount));
				tagList.set(index, map);
			}



		}
	}

	public int checkIsExist(String strEPC) {
		int existFlag = -1;
		if (isEmpty(strEPC)) {
			return existFlag;
		}

		String tempStr = "";
		for (int i = 0; i < tagList.size(); i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp = tagList.get(i);

			tempStr = temp.get("tagUii");

			if (strEPC.equals(tempStr)) {
				existFlag = i;
				break;
			}
		}

		return existFlag;
	}

	public static boolean isEmpty(CharSequence cs) {

		return cs == null || cs.length() == 0;

	}

	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}


	class TagThread extends Thread {

		private int mBetween = 80;
		HashMap<String, String> map;

		public TagThread(int iBetween) {
			mBetween = iBetween;
		}

		public void run() {
			String strTid;
			String strResult;

			String[] res = null;

			while (loopFlag) {

				res = mReader.readTagFromBuffer();//.readTagFormBuffer();

				if (res != null) {

					strTid = res[0];
					if (!strTid.equals("0000000000000000")&&!strTid.equals("000000000000000000000000")) {
						strResult = "TID:" + strTid + "\n";
					} else {
						strResult = "";
					}
					//Message msg = handler.obtainMessage();
					
					String epc = mReader.convertUiiToEPC(res[1]);
					if(!listaTags.contains(epc)) {                	
						listaTags.add(epc);                            	
					}						
					//msg.obj = strResult + "EPC:" + mReader.convertUiiToEPC(res[1]) + "@" + res[2];
					//handler.sendMessage(msg);					
				}
				try {
					sleep(mBetween);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}
	
		

}
