//
//  CallingViewController.swift
//  CallkitSample-Swift
//
//  Created by Thịnh Giò on 2/10/20.
//  Copyright © 2020 ThinhNT. All rights reserved.
//
import UIKit
import MediaPlayer

class CallingViewController: UIViewController {
    private var pipWidth: CGFloat {
        return  170.0 * UIScreen.main.bounds.width / 384.0
    }
    @IBOutlet weak var ivDisplayImage: UIImageView!
    @IBOutlet weak var lbName: UILabel!
    @IBOutlet weak var lbStatus: UILabel!
    @IBOutlet weak var ivQuality: UIImageView!
    @IBOutlet weak var btMute: UIButton!
    @IBOutlet weak var btSpeaker: UIButton!
    @IBOutlet weak var btEnd: UIButton!
    @IBOutlet weak var btReject: UIButton!
    @IBOutlet weak var btAnswer: UIButton!
    @IBOutlet weak var blurView: UIView!
    @IBOutlet weak var remoteView: UIView!
    @IBOutlet weak var localView: UIView!

    @IBOutlet weak var containerView: UIView!
    
    @IBOutlet weak var backButton: UIButton!
    var trayOriginalCenter: CGPoint = .zero

    private lazy var pipContainerView: AudioCallPipView = {
        let view = AudioCallPipView()
        view.backgroundColor = UIColor(named: "primaryColor")
        return view
    }()
    
    private lazy var pandGesture: UIPanGestureRecognizer = {
        let panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))

        return panGesture
    }()
    
    private lazy var tapGesture: UITapGestureRecognizer = {
        let panGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))

        return panGesture
    }()
    
    
    var callControl: CallControl!
    var call: StringeeCall!
    var stringeeClient: StringeeClient?
    var callTimer: Timer?
    lazy var timeCounter = TimeCounter()

    var timeoutTimer: Timer?
    var callInterval: Int = 0
    
    // Handle Audio Ouput
    var mpVolumeView: MPVolumeView!
    var airplayRouteButton: UIButton?
    var mediaFirstTimeConnected = false

    // MARK: - Init
    init(control: CallControl, call: StringeeCall?, stringeeClient: StringeeClient?) {
        super.init(nibName: "CallingViewController", bundle: nil)
        self.callControl = control
        self.call = call
        self.stringeeClient = stringeeClient
        call?.delegate = self
        InstanceManager.shared.callingVC = self

        // Lưu thông tin vào call control
        if let call = call {
            self.callControl.isIncoming = call.isIncomingCall
            self.callControl.isVideo = call.isVideoCall
            self.callControl.from = call.from
            self.callControl.to = call.to
            self.callControl.isAppToPhone = call.callType == .callIn || call.callType == .callOut
        }

        // if call's type is video then route audio to speaker
//        StringeeAudioManager.instance()?.setLoudspeaker(self.callControl.isVideo)
//        self.callControl.isSpeaker = self.callControl.isVideo
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // Notifications
        NotificationCenter.default.addObserver(self, selector: #selector(CallingViewController.handleSessionRouteChange), name: AVAudioSession.routeChangeNotification, object: nil)

        // UI
        setupUI()

        // Check timeout for call
        timeoutTimer = Timer.scheduledTimer(timeInterval: 10, target: self, selector: #selector(CallingViewController.checkCallTimeout), userInfo: nil, repeats: true)
        RunLoop.current.add(timeoutTimer!, forMode: .default)

        if call == nil {
            call = StringeeCall(stringeeClient: self.stringeeClient, from: callControl.from, to: callControl.to)
            call.delegate = self
            call.isVideoCall = callControl.isVideo

            call.make { [weak self] (status, code, message, data) in
                guard let self = self else { return }
                if (!status) {
                    self.endCallAndDismis()
                }
            }
        } else {
            call.initAnswer()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if (airplayRouteButton == nil) {
            mpVolumeView = MPVolumeView(frame: self.view.bounds)
            mpVolumeView.showsRouteButton = false
            mpVolumeView.showsVolumeSlider = false
            mpVolumeView.isUserInteractionEnabled = false
            self.view.addSubview(mpVolumeView)
            airplayRouteButton = mpVolumeView.subviews.filter { $0 is UIButton }.first as? UIButton
        }
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
       
    }

    // MARK: - Outlet Actions

    @IBAction func endTapped(_ sender: Any) {
        call.hangup { (status, code, message) in
            if (!status) {
                self.endCallAndDismis()
            }
        }
    }

    @IBAction func rejectTapped(_ sender: Any) {
        call.reject { (status, code, message) in
            if (!status) {
                self.endCallAndDismis()
            }
        }
    }

    @IBAction func answerTapped(_ sender: Any) {
        call.answer { (status, code, message) in
            if (!status) {
                self.endCallAndDismis()
            }
        }
        callControl.signalingState = .answered
        updateScreen()
    }

    @IBAction func muteTapped(_ sender: Any) {
        if call == nil { return }

        callControl.isMute = !callControl.isMute
        call.mute(callControl.isMute)
        let imageName = callControl.isMute ? "icon_mute_selected" : "icon_mute"
        btMute.setBackgroundImage(UIImage(named: imageName), for: .normal)
    }

    @IBAction func speakerTapped(_ sender: Any) {
        let isBluetoothConnected = isBluetoothConnected()
        if (isBluetoothConnected) {
            airplayRouteButton?.sendActions(for: .touchUpInside)
        } else {
            var imageName = ""
            if (callControl.audioOutputMode == .iphone) {
                callControl.audioOutputMode = .speaker
                StringeeAudioManager.instance()?.setLoudspeaker(true)
                imageName = "icon_speaker_selected"
            } else {
                callControl.audioOutputMode = .iphone
                StringeeAudioManager.instance()?.setLoudspeaker(false)
                imageName = "icon_speaker"
            }
            btSpeaker.setBackgroundImage(UIImage(named: imageName), for: .normal)
        }
    }

    @IBAction func tappedBackButton(_ sender: Any) {
        enterPIP()
    }

    func enterPIP() {
        if !Thread.isMainThread {
            DispatchQueue.main.async {
                self.enterPIP()
            }
            return
        }
        
        containerView.isHidden = true
        backButton.isHidden = true
        localView.isHidden = true
        remoteView.isHidden = true
        self.view.backgroundColor = .clear
        self.view.frame = CGRect(origin: CGPoint(x: UIScreen.main.bounds.width - pipWidth - 10, y: 72), size: CGSize(width: pipWidth, height: 72))
        self.view.layer.cornerRadius = 0
        self.view.clipsToBounds = false
        self.view.addSubview(pipContainerView)
        pipContainerView.frame = CGRect(origin: CGPoint(x: 10, y: 10), size: CGSize(width: pipWidth, height: 52))
        pipContainerView.addGestureRecognizer(pandGesture)
        pipContainerView.addGestureRecognizer(tapGesture)

    }
    
    func exitPIP() {
        if !Thread.isMainThread {
            DispatchQueue.main.async {
                self.exitPIP()
            }
            return
        }
        backButton.isHidden = false
        containerView.isHidden = false
        localView.isHidden = false
        remoteView.isHidden = false
        
        self.view.backgroundColor = .black
        self.view.frame = UIScreen.main.bounds
        self.view.layer.cornerRadius = 0
        pipContainerView.removeFromSuperview()
        view.removeGestureRecognizer(pandGesture)
        view.removeGestureRecognizer(tapGesture)

    }
    
    @objc func handleTap(_ sender: UITapGestureRecognizer) {
        exitPIP()
    }
    
    @objc func handlePan(_ sender: UIPanGestureRecognizer) {
        var translation = sender.translation(in: self.view)
        var velocity = sender.velocity(in: self.view)
        let minimizedWidth = 190.0 * UIScreen.main.bounds.width / 384.0

        if sender.state == .began {
            trayOriginalCenter = self.view.center
            
        } else if sender.state == .changed {
            
            var newOffsetY = trayOriginalCenter.y + translation.y
            var newOffsetX = trayOriginalCenter.x + translation.x

            newOffsetX = max(pipWidth/2, newOffsetX)
            newOffsetX = min(UIScreen.main.bounds.width -  pipWidth/2, newOffsetX)

            
            newOffsetY = max(pipWidth/2, newOffsetY)
            newOffsetY = min(UIScreen.main.bounds.height - pipWidth/2, newOffsetY)

            self.view.center = CGPoint(x: self.view.center.x, y: newOffsetY)
            
        } else if sender.state == .ended {
            debugPrint("[capacitor-agora] hai handlePan velocity \(velocity.y)")
            let speed: CGFloat = velocity.y / 500
            if abs(speed) > 1 {
                animatePipView(speed: speed)
            }
        }
        
        func animatePipView(speed: CGFloat) {
            let deltaY: CGFloat = (UIScreen.main.bounds.height / 6) * speed

            var newOffsetY: CGFloat = self.view.center.y
            newOffsetY = newOffsetY + deltaY
            newOffsetY = max(pipWidth/2, newOffsetY)
            newOffsetY = min(UIScreen.main.bounds.height - pipWidth/2, newOffsetY)

            let distance = abs(newOffsetY) - self.view.center.y
            
            let duration = (distance / UIScreen.main.bounds.height) * 1.5
            UIView.animate(withDuration: duration, delay: 0) {[weak self] in
                guard let self = self else { return }
                self.view.center = CGPoint(x: self.view.center.x, y: newOffsetY)
            }
        }
    }
    
    // MARK: - Public Actions

    func endCallAndDismis(description: String = "Call ended") {
        DispatchQueue.main.async {
            UIDevice.current.isProximityMonitoringEnabled = false
            UIApplication.shared.isIdleTimerDisabled = false
            self.view.isUserInteractionEnabled = false
            self.lbStatus.text = description
            self.pipContainerView.updateDuration(with: description)
            // Ngừng timer
            self.stopCallTimer()
            self.stopTimeoutTimer()

            DispatchQueue.main.asyncAfter(deadline: .now() + 0.7, execute: {
//                self.dismiss(animated: true, completion: nil)
                self.view.removeFromSuperview()
                InstanceManager.shared.callingVC = nil
                self.stringeeClient?.disconnect()
            })
        }
    }

    func updateScreen() {
        DispatchQueue.main.async {
            let screenType = self.screenType()
            switch screenType {
            case .incoming:
                self.btReject.isHidden = false
                self.btAnswer.isHidden = false
                self.btEnd.isHidden = true
                break
            case .outgoing:
                self.btReject.isHidden = true
                self.btAnswer.isHidden = true
                self.btEnd.isHidden = false
                break
            case .calling:
                self.btReject.isHidden = true
                self.btAnswer.isHidden = true
                self.btEnd.isHidden = false
                break
            }
        }
    }
    // MARK: - Private Actions

    private func screenType() -> CallScreenType {
        var screenType: CallScreenType!
        if (callControl.signalingState == .answered) {
            screenType = .calling
        } else {
            screenType = callControl.isIncoming ? .incoming : .outgoing
        }

        return screenType
    }

    private func setupUI() {
        UIDevice.current.isProximityMonitoringEnabled = true
        UIApplication.shared.isIdleTimerDisabled = callControl.isVideo
        if (callControl.displayImage != "") {
            let imageUrl = URL(string: callControl.displayImage)!
            let task = URLSession.shared.dataTask(with: imageUrl) { (data, response, error) in
                if let error = error {
                    print("Error downloading image: \(error)")
                    return
                }
                
                guard let data = data, let image = UIImage(data: data) else {
                    print("Invalid image data")
                    return
                }
                
                DispatchQueue.main.async {
                    // Set the downloaded image to the UIImageView instance
                    self.ivDisplayImage.image = image
                    self.pipContainerView.updateAvatar(with: image)
                }
            }
            task.resume()
        }
        // Fill data
        self.lbStatus.text = callControl.isIncoming ? "Incoming Call" : "Outgoing Call"
        self.pipContainerView.updateDuration(with: callControl.isIncoming ? "Incoming Call" : "Outgoing Call")
        self.lbName.text = callControl.displayName
        self.pipContainerView.updateName(with: callControl.displayName)
        //self.ivDisplayImage.image = UIImage(data: data!)
        updateScreen()
    }

    // MARK: - Timer

    private func startCallTimer() {
        if callControl.signalingState != .answered || callControl.mediaState != .connected {
            return
        }

        if callTimer == nil {
            // Bắt đầu đếm giây
            callTimer = Timer(timeInterval: 1, target: self, selector: #selector(CallingViewController.timeTick(timer:)), userInfo: nil, repeats: true)
            RunLoop.current.add(callTimer!, forMode: .default)
            callTimer?.fire()

            // => Ko check timeout nữa
            self.stopTimeoutTimer()
        }
    }

    @objc private func timeTick(timer: Timer) {
        let timeNow = timeCounter.timeNow()
        self.lbStatus.text = timeNow
        pipContainerView.updateDuration(with: timeNow)
    }

    private func stopCallTimer() {
        CFRunLoopStop(CFRunLoopGetCurrent())
        callTimer?.invalidate()
        callTimer = nil
    }

    private func stopTimeoutTimer() {
        CFRunLoopStop(CFRunLoopGetCurrent())
        timeoutTimer?.invalidate()
        timeoutTimer = nil
    }

    @objc private func checkCallTimeout() {
        print("checkCallTimeout")
        callInterval += 10
        if callInterval > 60 && callTimer == nil {
            if callControl.isIncoming {
                call.reject { (status, code, message) in
                    if (!status) {
                        self.endCallAndDismis()
                    }
                }
            } else {
                call.hangup { (status, code, message) in
                    if (!status) {
                        self.endCallAndDismis()
                    }
                }
            }
        }
    }
}

extension CallingViewController: StringeeCallDelegate {
    func didChangeSignalingState(_ stringeeCall: StringeeCall!, signalingState: SignalingState, reason: String!, sipCode: Int32, sipReason: String!) {
        print("didChangeSignalingState \(signalingState.rawValue)")
        callControl.signalingState = signalingState
        DispatchQueue.main.async {
            switch signalingState {
            case .calling:
                self.lbStatus.text = "Calling..."
                self.pipContainerView.updateDuration(with: "Calling...")
            case .ringing:
                self.lbStatus.text = "Ringing..."
                self.pipContainerView.updateDuration(with: "Ringing...")

            case .answered:
                self.callControl.signalingState = .answered
                self.updateScreen()
                self.startCallTimer()
            case .ended:
                self.endCallAndDismis()
            case .busy:
                self.endCallAndDismis(description: "Busy")
            @unknown default:
                break
            }
        }
    }

    func didChangeMediaState(_ stringeeCall: StringeeCall!, mediaState: MediaState) {
        print("didChangeMediaState \(mediaState.rawValue)")
        DispatchQueue.main.async {
            switch mediaState {
            case .connected:
                self.callControl.mediaState = mediaState
                self.lbStatus.text = self.callControl.isIncoming ? "Incoming Call" : "Outgoing Call"
                self.pipContainerView.updateDuration(with: self.callControl.isIncoming ? "Incoming Call" : "Outgoing Call")
                self.startCallTimer()
                
                // if call's type is video then route audio to speaker
                if self.callControl.isVideo && !self.mediaFirstTimeConnected {
                    self.mediaFirstTimeConnected = !self.mediaFirstTimeConnected
                    self.routeToSpeakerIfNeeded()
                }
            case .disconnected:
                break
            @unknown default:
                break
            }
        }
    }

    func didHandle(onAnotherDevice stringeeCall: StringeeCall!, signalingState: SignalingState, reason: String!, sipCode: Int32, sipReason: String!) {
        if signalingState == .answered || signalingState == .busy || signalingState == .ended {
            endCallAndDismis(description: "The call is handled on another device")
        }
    }

    func didReceiveLocalStream(_ stringeeCall: StringeeCall!) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            stringeeCall.localVideoView.frame = CGRect(origin: .zero, size: self.localView.frame.size)
            self.localView.addSubview(stringeeCall.localVideoView)
        }
    }

    func didReceiveRemoteStream(_ stringeeCall: StringeeCall!) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            stringeeCall.remoteVideoView.frame = CGRect(origin: .zero, size: self.remoteView.frame.size)
            self.remoteView.addSubview(stringeeCall.remoteVideoView)
        }
    }
}

// MARK: - Handle Audio Output

extension CallingViewController {
    @objc private func handleSessionRouteChange() {
        DispatchQueue.main.async {
            let route = AVAudioSession.sharedInstance().currentRoute
            if let portDes = route.outputs.first {
                var imageName = ""
                if (portDes.portType == .builtInSpeaker) {
                    self.callControl.audioOutputMode = .speaker
                    imageName = "icon_speaker_selected"
                } else if (portDes.portType == .headphones || portDes.portType == .builtInReceiver) {
                    self.callControl.audioOutputMode = .iphone
                    imageName = "icon_speaker"
                } else {
                    self.callControl.audioOutputMode = .bluetooth
                    imageName = "ic-bluetooth"
                }
                
                self.btSpeaker.setBackgroundImage(UIImage(named: imageName), for: .normal)
            }
        }
    }
    
    private func routeToSpeakerIfNeeded() {
        DispatchQueue.main.async {
            let route = AVAudioSession.sharedInstance().currentRoute
            if let portDes = route.outputs.first {
                // if headphone is not plugged in and bluetooth is not connected then route audio to speaker in case call's type is video
                if portDes.portType != .headphones && !self.isBluetoothConnected() {
                    StringeeAudioManager.instance()?.setLoudspeaker(true)
                    self.callControl.audioOutputMode = .speaker
                }
            }
        }
    }
    
    // Check if device is connected to any bluetooth device or not
    func isBluetoothConnected() -> Bool {
        guard let availableInputs = AVAudioSession.sharedInstance().availableInputs else {
            return false
        }
        
        for availableInput in availableInputs {
            if availableInput.portType == .bluetoothHFP || availableInput.portType == .bluetoothLE || availableInput.portType == .bluetoothA2DP {
                return true
            }
        }
        
        return false
    }
}


