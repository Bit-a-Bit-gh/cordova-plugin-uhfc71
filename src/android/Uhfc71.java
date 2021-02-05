package it.dynamicid;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Uhfc71 extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if ("rfidScan".equals(action)) {
			rfidScan(args.getString(0), args.getLong(1), args.getLong(2), callbackContext);
			return true;
		} else if ("barcodeScan".equals(action)) {
			barcodeScan(args.getLong(0), args.getLong(1), callbackContext);
			return true;
		}

		return false;
	}

	private void rfidScan(String epc, long waittime, long txpower, CallbackContext callbackContext) {
		try {
			 Context context = this.cordova.getActivity().getApplicationContext();
			
			//Toast.makeText(webView.getContext(), "Costruttore", Toast.LENGTH_LONG).show();			
			InventoryUhfc71 iu = new InventoryUhfc71(context, txpower);			
			//Toast.makeText(webView.getContext(), "Start" + iu, Toast.LENGTH_LONG).show();
			iu.StartInventoryStream();
			try {
				Thread.sleep(waittime);
			} catch (Exception e) {

			}
			//Toast.makeText(webView.getContext(), "Stop", Toast.LENGTH_LONG).show();
			iu.StopInventoryStream();
			
			
			String result = "";

			if(epc.isEmpty()) {
				result = iu.GetTags();
				
				if(result.isEmpty()){
					result = "NO-TAGS";
				}
				
				
								
			} else {
				//result = iu.GetTags();
				String tags[] = result.split(",");
				List<String> lista = new ArrayList<String>();

				for(int i=0; i<tags.length; i++) {
					lista.add(tags[i]);
				}

				if(lista.contains(epc)) {
					result="OK";
				}else {
					result="KO";
				}

			}


			//Toast.makeText(webView.getContext(), iu.mUiResultMsg, Toast.LENGTH_LONG).show();
			callbackContext.success(result);

		} catch (Exception e) {
			callbackContext.error(e.toString());
		}


	}

	private void barcodeScan(long waittime, long txpower, CallbackContext callbackContext) {
		try {
			 Context context = this.cordova.getActivity().getApplicationContext();
			
			//Toast.makeText(webView.getContext(), "Costruttore", Toast.LENGTH_LONG).show();			
			InventoryUhfc71 iu = new InventoryUhfc71(context, txpower);			
			//Toast.makeText(webView.getContext(), "Start" + iu, Toast.LENGTH_LONG).show();
			iu.ScanBarcode();
			try {
				Thread.sleep(waittime);
			} catch (Exception e) {

			}
			iu.StopBarcode();
			//Toast.makeText(webView.getContext(), "Stop", Toast.LENGTH_LONG).show();
			
			
			String result = iu.GetBarcode();

			if(result.isEmpty()) {
				
				result = "NO-BARCODES";
								
			}
			//Toast.makeText(webView.getContext(), iu.mUiResultMsg, Toast.LENGTH_LONG).show();
			callbackContext.success(result);

		} catch (Exception e) {
			callbackContext.error(e.toString());
		}


	}
}
