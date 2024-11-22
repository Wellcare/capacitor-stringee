#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorStringeePlugin, "CapacitorStringee",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(StringeeConnect, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(StringeeCall, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(StringeeHangup, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(removeAllListeners, CAPPluginReturnNone);
)
