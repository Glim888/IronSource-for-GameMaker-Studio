/*
	Here you can see the possible callbacks
*/

if (ds_exists(async_load, ds_type_map)) {

	// get Callbacks
	var _interstitialCallback = ds_map_find_value(async_load, "interstitial");
	var _rewardedCallback = ds_map_find_value(async_load, "rewarded");
	
	if (!is_undefined(_interstitialCallback)) {
		
		// interstitial shown -> load new_ one!
		if (_interstitialCallback == "onInterstitialAdShowSucceeded" ||
			_interstitialCallback == "onInterstitialAdOpened") {
				alarm[0] = room_speed;
				
			}
		if (_interstitialCallback == "onInterstitialAdOpened") {	
		}
			
		// possible callbacks
		
		// onInterstitialAdClicked
		// onInterstitialAdReady
		// onInterstitialAdLoadFailed
		// onInterstitialAdOpened
		// onInterstitialAdClosed
		// onInterstitialAdShowSucceeded
		// onInterstitialAdShowFailed
	}
	
	if (!is_undefined(_rewardedCallback)) {
		
		// rewarded video successful
		if (_rewardedCallback == "onRewardedVideoAdRewarded") {
		}
		
		// onRewardedVideoAdOpened
		// onRewardedVideoAdClosed
		// onRewardedVideoAvailabilityChanged
		// onRewardedVideoAdStarted
		// onRewardedVideoAdEnded
		// onRewardedVideoAdRewarded
		// onRewardedVideoAdShowFailed
		// onRewardedVideoAdClicked
	}
		
}