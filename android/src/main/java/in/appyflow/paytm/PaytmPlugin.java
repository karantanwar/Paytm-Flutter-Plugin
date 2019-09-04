package in.appyflow.paytm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

/**
 * PaytmPlugin
 * by Mr Dishant Mahajan
 */
public class PaytmPlugin implements MethodCallHandler {
    /**
     * Plugin registration.
     */
    private final Activity activity;

    String TAG = getClass().getName();


    Result result;

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "paytm");
        channel.setMethodCallHandler(new PaytmPlugin(registrar.activity()));
    }

    private PaytmPlugin(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void onMethodCall(MethodCall call, Result result) {

        this.result = result;

        if (call.method.equals("startPaytmPayment")) {

            boolean testing = call.argument("testing");
            String mId = call.argument("mId").toString();
            String orderId = call.argument("orderId").toString();
            String custId = call.argument("custId").toString();
            String channelId = call.argument("channelId").toString();
            String txnAmount = call.argument("txnAmount").toString();
            String website = call.argument("website").toString();
            String callBackUrl = call.argument("callBackUrl").toString();
            String industryTypeId = call.argument("industryTypeId").toString();
            String checkSumHash = call.argument("checkSumHash").toString();
            String ssoToken = call.argument("ssoToken").toString();
            String requestType = call.argument("requestType").toString();
            initializePaytmPayment(testing, mId, orderId, custId, channelId, txnAmount, website, callBackUrl, industryTypeId, checkSumHash, ssoToken, requestType);

        } else {
            result.notImplemented();
        }
    }

    private void sendResponse(Map<String, Object> paramMap) {

        result.success(paramMap);

    }


    private void initializePaytmPayment(boolean testing, String mId,
                                        String orderId,
                                        String custId,
                                        String channelId,
                                        String txnAmount,
                                        String website,
                                        String callBackUrl,
                                        String industryTypeId,
                                        String checkSumHash,
                                        String ssoToken,
                                        String type) {

        //getting paytm service
//        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
//        PaytmPGService Service = PaytmPGService.getProductionService();

        PaytmPGService Service;

        if (testing) {
            Service = PaytmPGService.getStagingService();
        } else {
            Service = PaytmPGService.getProductionService();
        }

        //creating a hashmap and adding all the values required

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", mId);
        paramMap.put("REQUEST_TYPE", "ADD_MONEY");
        paramMap.put("SSO_TOKEN", ssoToken);
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CHANNEL_ID", channelId);
        paramMap.put("CUST_ID", custId);
        paramMap.put("TXN_AMOUNT", txnAmount);
        paramMap.put("WEBSITE", website);
        paramMap.put("INDUSTRY_TYPE_ID", industryTypeId);
        paramMap.put("CALLBACK_URL", callBackUrl);
        paramMap.put("CHECKSUMHASH", checkSumHash);

       // paytmParams.put("MID",merchantMid);
       // paytmParams.put("REQUEST_TYPE", "ADD_MONEY");
        //paytmParams.put("SSO_TOKEN", sso_token);
        //paytmParams.put("ORDER_ID",orderId);
        //paytmParams.put("CHANNEL_ID",channelId);
        //paytmParams.put("CUST_ID",custId);
        //paytmParams.put("MOBILE_NO",mobileNo);
       // paytmParams.put("EMAIL",email);
        //paytmParams.put("TXN_AMOUNT",txnAmount);
        //paytmParams.put("WEBSITE",website);
        //paytmParams.put("INDUSTRY_TYPE_ID",industryTypeId);
        //paytmParams.put("CALLBACK_URL", callbackUrl);

//        Log.i(TAG, paramMap.toString());


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(activity, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle bundle) {

                Log.i(TAG, bundle.toString());

                Map<String, Object> paramMap = new HashMap<>();


                for (String key : bundle.keySet()) {
                    paramMap.put(key, bundle.getString(key));
                }

                Log.i(TAG, paramMap.toString());

                sendResponse(paramMap);

            }

            @Override
            public void networkNotAvailable() {
                Map<String, Object> paramMap = new HashMap<>();

                paramMap.put("error", true);
                paramMap.put("errorMessage", "Network Not Available");

                sendResponse(paramMap);
            }

            @Override
            public void clientAuthenticationFailed(String s) {

                Map<String, Object> paramMap = new HashMap<>();

                paramMap.put("error", true);
                paramMap.put("errorMessage", s);

                sendResponse(paramMap);
            }

            @Override
            public void someUIErrorOccurred(String s) {

                Map<String, Object> paramMap = new HashMap<>();

                paramMap.put("error", true);
                paramMap.put("errorMessage", s);

                sendResponse(paramMap);

            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {

                Map<String, Object> paramMap = new HashMap<>();

                paramMap.put("error", true);
                paramMap.put("errorMessage", s + " , " + s1.toString());

                sendResponse(paramMap);
            }

            @Override
            public void onBackPressedCancelTransaction() {

                Map<String, Object> paramMap = new HashMap<>();

                paramMap.put("error", true);
                paramMap.put("errorMessage", "Back Pressed Transaction Cancelled");

                sendResponse(paramMap);

            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.i(TAG, s + bundle.toString());

                Map<String, Object> paramMap = new HashMap<>();


                for (String key : bundle.keySet()) {
                    paramMap.put(key, bundle.getString(key));
                }

                Log.i(TAG, paramMap.toString());

                paramMap.put("error", true);
                paramMap.put("errorMessage", s);


                sendResponse(paramMap);


            }

        });


    }


}
