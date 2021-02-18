package ${YYAndroidPackageName};

//Game Maker Studio 2 Packages
import ${YYAndroidPackageName}.R;
import com.yoyogames.runner.RunnerJNILib;
import ${YYAndroidPackageName}.RunnerActivity;

//Some Android Packages
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.view.View;
import android.view.MotionEvent;
import java.util.ArrayList;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;

//Starts Here
public class Ironsource implements IExtensionBase , RewardedVideoListener, InterstitialListener {

    IronSourceBannerLayout banner;

    final String TAG = "yoyo";

    public void IronSource_Init(String appKey, double test) {
       
        // test integration
        if (test > 0.0) {
            IronSource.setAdaptersDebug(true);
            IntegrationHelper.validateIntegration(RunnerActivity.CurrentActivity);
        }

        IronSource.setRewardedVideoListener(this);
        IronSource.setInterstitialListener(this);
        IronSource.init(RunnerActivity.CurrentActivity, appKey, IronSource.AD_UNIT.OFFERWALL, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO, IronSource.AD_UNIT.BANNER);   

        Log.d(TAG, "INIT IronSource - TEST:" + test);
    }
    
    public void IronSource_DestroyBanner() {
        if (banner != null) {
            IronSource.destroyBanner(banner);
            banner = null;
            Log.d(TAG, "IronSource - Banner destroyed!");   
        }else{
            Log.d(TAG, "IronSource - There is no Banner to destroy!");   
        }
    }

    public void IronSource_BannerVisibility(final double visiblility) {
        RunnerActivity.CurrentActivity.runOnUiThread(new Runnable() {
            public void run() {
            final FrameLayout bannerContainer = RunnerActivity.CurrentActivity.findViewById(R.id.bannerContainer);  
            bannerContainer.setVisibility((int) visiblility);  
            }
        });
            
    }

    public void IronSource_CreateBanner(double bannerType, final double gravity) {

        if (banner != null) {
            Log.d(TAG, "IronSource - There is still a banner, delete this banner before!");    
            return;
        }

        final ISBannerSize bs;
        // https://developers.ironsrc.com/ironsource-mobile/android/banner-integration-android/#step-1
        switch ((int)bannerType) {

            case 0: bs = ISBannerSize.BANNER; Log.d(TAG, "IronSource - Banner = small"); break;
            case 1: bs = ISBannerSize.LARGE; Log.d(TAG, "IronSource - Banner = big"); break;
            case 2: bs = ISBannerSize.RECTANGLE; Log.d(TAG, "IronSource - Banner = rect"); break;
            case 3: bs = ISBannerSize.SMART; Log.d(TAG, "IronSource - Banner = smart"); break;
            case 4: bs = new ISBannerSize(320, 50); Log.d(TAG, "IronSource - Banner = custom"); break;
            default: Log.d(TAG, "IronSource - BANNER WRONG TYPE!:"); return;
        }

        RunnerActivity.CurrentActivity.runOnUiThread(new Runnable() {
            public void run() {

            // find bannerContainer (C:\ProgramData\GameMakerStudio2\Cache\runtimes\runtime-2.2.5.378\android\runner\ProjectFiles\src\main\res\layout.maxin.xml)
            // i created this FrameLayout
            /*
            <FrameLayout
                android:id="@+id/bannerContainer"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="bottom"
                android:visibility="visible" />
            */
            final FrameLayout bannerContainer = RunnerActivity.CurrentActivity.findViewById(R.id.bannerContainer);       

            // set gravity
            FrameLayout.LayoutParams  lp = (FrameLayout.LayoutParams) bannerContainer.getLayoutParams();
            
            if (gravity == 0) {
                lp.gravity = Gravity.TOP;
            }else if (gravity == 1) {
                lp.gravity = Gravity.BOTTOM;
            }else{
                Log.d(TAG, "IronSource - BANNER GRAVITY WRONG!:");
                lp.gravity = Gravity.TOP;
            }
            
            bannerContainer.setLayoutParams(lp); 

            // create banner
            banner = IronSource.createBanner(RunnerActivity.CurrentActivity, bs);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            bannerContainer.addView(banner, 0, layoutParams);
            Log.d(TAG, "IronSource - BANNER created");

            if (banner != null) {
                // set the banner listener
                banner.setBannerListener(new BannerListener() {
                    @Override
                    public void onBannerAdLoaded() {
                        Log.d(TAG, "IronSource - onBannerAdLoaded");
                        // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                        bannerContainer.setVisibility(View.VISIBLE);
                    }
    
                    @Override
                    public void onBannerAdLoadFailed(IronSourceError error) {
                        Log.d(TAG, "IronSource - onBannerAdLoadFailed" + " " + error);
                        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                        RunnerJNILib.DsMapAddString(dsMapIndex, "banner", "onBannerAdLoadFailed");
                        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
                    }
    
                    @Override
                    public void onBannerAdClicked() {
                        Log.d(TAG, "IronSource - onBannerAdClicked");
                        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                        RunnerJNILib.DsMapAddString(dsMapIndex, "banner", "onBannerAdClicked");
                        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
                    }
    
                    @Override
                    public void onBannerAdScreenPresented() {
                        Log.d(TAG, "IronSource - onBannerAdScreenPresented");
                        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                        RunnerJNILib.DsMapAddString(dsMapIndex, "banner", "onBannerAdScreenPresented");
                        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
                    }
    
                    @Override
                    public void onBannerAdScreenDismissed() {
                        Log.d(TAG, "IronSource - onBannerAdScreenDismissed");
                        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                        RunnerJNILib.DsMapAddString(dsMapIndex, "banner", "onBannerAdScreenDismissed");
                        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
                    }
    
                    @Override
                    public void onBannerAdLeftApplication() {
                        Log.d(TAG, "IronSource - onBannerAdLeftApplication");
                        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
                        RunnerJNILib.DsMapAddString(dsMapIndex, "banner", "onBannerAdLeftApplication");
                        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
                    }
                });
    
                // load ad into the created banner
                IronSource.loadBanner(banner);
            } else {
                Log.d(TAG, "IronSource - BANNER creation failed");
            }
        }
        });

    }
    
    // public methods for GMS2
    public void IronSource_LoadInterstitial(){
        IronSource.loadInterstitial();
    }

    public double IronSource_isInterstitialCapped(String placementname) {
        return IronSource.isInterstitialPlacementCapped(placementname) == true ? 1.0 : 0.0;
    }

    public void IronSource_ShowInterstitial(String placementname) {
        Log.d(TAG, "IS CAPPED - :" + IronSource.isInterstitialPlacementCapped(placementname));     
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial(placementname);
        }
    }


    public double IronSource_InterstitialIsReady() {
        return IronSource.isInterstitialReady() ? 1.0 : 0.0;
    }


    public void IronSource_ShowRewardedVideo() {
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo();
        }
    }

    public double IronSource_RewardedIsReady() {
        return IronSource.isRewardedVideoAvailable() ? 1.0 : 0.0;
    }

    //#region IExtensionBase
    /** Called when the activity restarted */

    public void onRestart() {
     
    }

    /** Called when the activity is about to become visible. */

    public void onStart() {       
    }

    /** Called when the activity has become visible. */

    public void onResume() {
        IronSource.onResume(RunnerActivity.CurrentActivity);     
    }

    /** Called when another activity is taking focus. */

    public void onPause() {
        IronSource.onPause(RunnerActivity.CurrentActivity);
        
    }

    /** Called when the activity is no longer visible. */

    public void onStop() {
       
    }

    /** Called just before the activity is destroyed. */

    public void onDestroy() {
       // useless to create ds map, because it will never reach the social event!
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public Dialog onCreateDialog(int id) {
        return null;
    }

    public boolean onTouchEvent(final MotionEvent event) {
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    public boolean performClick(){
        return false;
    }
    //#endregion

    
     // --------- IronSource Rewarded Video Listener ---------

    @Override
    public void onRewardedVideoAdOpened() {
        // called when the video is opened
        Log.d(TAG, "onRewardedVideoAdOpened");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdOpened");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // called when the video is closed
        Log.d(TAG, "onRewardedVideoAdClosed");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdClosed");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
        
    }

    @Override
    public void onRewardedVideoAvailabilityChanged(boolean b) {
        // called when the video availbility has changed
        Log.d(TAG, "onRewardedVideoAvailabilityChanged" + " " + b);
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAvailabilityChanged");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onRewardedVideoAdStarted() {
        // called when the video has started
        Log.d(TAG, "onRewardedVideoAdStarted");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdStarted");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onRewardedVideoAdEnded() {
        // called when the video has ended
        Log.d(TAG, "onRewardedVideoAdEnded");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdEnded");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
        // called when the video has been rewarded and a reward can be given to the user
        Log.d(TAG, "onRewardedVideoAdRewarded" + " " + placement);
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdRewarded");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);

    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
        // called when the video has failed to show
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onRewardedVideoAdShowFailed" + " " + ironSourceError);
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdShowFailed");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onRewardedVideoAdClicked(Placement placement) {
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "rewarded", "onRewardedVideoAdClicked");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    // --------- IronSource Interstitial Listener ---------

    @Override
    public void onInterstitialAdClicked() {
        // called when the interstitial has been clicked
        Log.d(TAG, "onInterstitialAdClicked");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdClicked");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onInterstitialAdReady() {
        // called when the interstitial is ready
        Log.d(TAG, "onInterstitialAdReady");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdReady");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
        // called when the interstitial has failed to load
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onInterstitialAdLoadFailed" + " " + ironSourceError);
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdLoadFailed");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onInterstitialAdOpened() {
        // called when the interstitial is shown
        Log.d(TAG, "onInterstitialAdOpened");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdOpened");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onInterstitialAdClosed() {
        // called when the interstitial has been closed
        Log.d(TAG, "onInterstitialAdClosed");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdClosed");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onInterstitialAdShowSucceeded() {
        // called when the interstitial has been successfully shown
        Log.d(TAG, "onInterstitialAdShowSucceeded");
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdShowSucceeded");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
        // called when the interstitial has failed to show
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onInterstitialAdShowFailed" + " " + ironSourceError);
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString(dsMapIndex, "interstitial", "onInterstitialAdShowFailed");
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, 70);
    }
    

}