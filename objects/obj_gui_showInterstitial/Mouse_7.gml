/// @description Insert description here
// You can write your code in this editor

// if its capped, then dont show interstital -> you can setup the pacing/capping at the IS dashboard
if (IronSource_IsInterstitialCapped("DefaultInterstitial")) return;

if (IronSource_InterstitialIsReady()) IronSource_ShowInterstitial("DefaultInterstitial");
