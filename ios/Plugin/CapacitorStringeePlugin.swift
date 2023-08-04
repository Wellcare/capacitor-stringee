import Foundation
import Capacitor
import UIKit
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorStringeePlugin)
public class CapacitorStringeePlugin: CAPPlugin {
    private let implementation = CapacitorStringee()
    public let stringeeClient = StringeeClient()
    var callingVC: CallingViewController?
    var token: String?

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
    
    @objc func StringeeConnect(_ call: CAPPluginCall) {
        self.token = call.getString("token") ?? ""
        if self.token == "" {
            call.reject("No token provided")
        }
        stringeeClient.connectionDelegate = self;
        stringeeClient.incomingCallDelegate = self
        stringeeClient.connect(withAccessToken: self.token)
        call.resolve(["token": self.token as Any])
    }
    
    @objc func StringeeCall(_ call: CAPPluginCall) {
        let to = call.getString("to") ?? ""
        let displayName = call.getString("displayName") ?? ""
        let displayImage = call.getString("displayImage") ?? ""
        if to == "" {
            call.reject("No call target provided")
        }
        makeCall(to, displayName, displayImage)
        call.resolve()
    }
    
    @objc func StringeeReject(_ call: CAPPluginCall) {
        call.resolve()
    }
    
    func makeCall(_ to: String, _ displayName: String, _ displayImage: String) {
        if (!stringeeClient.hasConnected || to == "") {
            
            return
        }
        
        var callControl = CallControl()
        callControl.from = stringeeClient.userId
        callControl.to = to
        callControl.username = displayName
        callControl.displayImage = displayImage
        callControl.isVideo = false
        
        DispatchQueue.main.async {
            let topMost = UIApplication.getTopViewController()
            let callingVC = CallingViewController.init(control: callControl, call: nil, stringeeClient: self.stringeeClient)
            callingVC.modalPresentationStyle = .fullScreen
            self.callingVC = callingVC
            topMost?.present(callingVC, animated: true)
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {[weak self] in
                // Do stuff
            }
        }
    }
}

extension CapacitorStringeePlugin: StringeeConnectionDelegate {

    public func didConnect(_ stringeeClient: StringeeClient!, isReconnecting: Bool) {
        let jsObject: [String: Any] = [
            "uid": stringeeClient.userId!,
            "isReconnecting": isReconnecting
        ]
        notifyListeners("onConnectionConnected", data: jsObject)
    }

    public func didDisConnect(_ stringeeClient: StringeeClient!, isReconnecting: Bool) {
        let jsObject: [String: Any] = [
            "uid": stringeeClient.userId!,
            "isReconnecting": isReconnecting
        ]
        notifyListeners("onConnectionDisconnected", data: jsObject)
    }

    public func didFailWithError(_ stringeeClient: StringeeClient!, code: Int32, message: String!) {
        let jsObject: [String: Any] = [
            "code": code,
            "error": code,
            "message": message as Any
        ]
        notifyListeners("onConnectionError", data: jsObject)
    }

    public func requestAccessToken(_ stringeeClient: StringeeClient!) {
        notifyListeners("onRequestNewToken", data: nil)
    }

    public func didReceiveCustomMessage(_ stringeeClient: StringeeClient!, message: [AnyHashable : Any]!, fromUserId userId: String!) {
        let jsObject: [String: Any] = [
            "message": message as Any,
            "from": userId as Any,
            "msg": message as Any
        ]
        notifyListeners("onCustomMessage", data: jsObject);
    }
}

extension CapacitorStringeePlugin: StringeeIncomingCallDelegate {
    public func incomingCall(with stringeeClient: StringeeClient!, stringeeCall: StringeeCall!) {
        // unimplement yet cause currently we just using app to phone feature
        DispatchQueue.main.async {
            if (self.callingVC != nil) {
                stringeeCall.reject { (status, code, message) in
                    print(message ?? "")
                }
                return
            }
            var callControl = CallControl()
            callControl.from = stringeeClient.userId
            //callControl.to = to
            callControl.isVideo = false
            let topMost = UIApplication.getTopViewController()
            let callingVC = CallingViewController.init(control: callControl, call: nil, stringeeClient: self.stringeeClient)
            callingVC.modalPresentationStyle = .fullScreen
            self.callingVC = callingVC
            topMost?.present(callingVC, animated: true)
        }
    }
}


extension UIApplication {
    
    class func getTopViewController(base: UIViewController? = UIApplication.shared.keyWindow?.rootViewController) -> UIViewController? {
        
        if let nav = base as? UINavigationController {
            return getTopViewController(base: nav.visibleViewController)
            
        } else if let tab = base as? UITabBarController, let selected = tab.selectedViewController {
            return getTopViewController(base: selected)
            
        } else if let presented = base?.presentedViewController {
            return getTopViewController(base: presented)
        }
        return base
    }
}
