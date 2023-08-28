//
//  AudioCallPipView.swift
//  WellcareCapacitorStringee
//
//  Created by vulcanlabs-hai on 21/08/2023.
//

import Foundation
import UIKit

class AudioCallPipView: UIView {
    private lazy var avatarImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.backgroundColor = UIColor(named: "colorD9D9D9")
        return imageView
    }()
    
    private lazy var durationLabel: UILabel = {
        let label = UILabel()
        label.text = "00:00"
        label.textColor = .lightText
        label.textAlignment = .left
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.text = "Nguyen Van A"
        label.textColor = .white
        label.textAlignment = .left
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    
    init() {
        super.init(frame: .zero)
        setupUIs()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private var shadowLayer: CAShapeLayer!
    var isLayoutSubView: Bool = false
       override func layoutSubviews() {
           super.layoutSubviews()
           guard !isLayoutSubView else { return }
           isLayoutSubView = true
           layer.masksToBounds = false
           let path = UIBezierPath(roundedRect:CGRect(origin: .zero, size: CGSize(width: self.frame.width, height: 52)),
                                   byRoundingCorners:[.topLeft, .bottomLeft],
                                   cornerRadii: CGSize(width: 26, height:  26))

           layer.shadowColor = UIColor.black.cgColor
           layer.shadowPath = path.cgPath
           layer.shadowOffset = CGSize(width: 5.0, height: 3.0)
           layer.shadowOpacity = 0.5
           layer.shadowRadius = 4.0
           
           layer.cornerRadius = 26
           layer.maskedCorners = [.layerMinXMinYCorner, .layerMinXMaxYCorner]

       }
    
    func setupUIs() {
        
        if shadowLayer == nil {
            shadowLayer = CAShapeLayer()
            shadowLayer.path = UIBezierPath(roundedRect: bounds, cornerRadius: 12).cgPath
            shadowLayer.fillColor = UIColor.white.cgColor

            shadowLayer.shadowColor = UIColor.darkGray.cgColor
            shadowLayer.shadowPath = shadowLayer.path
            shadowLayer.shadowOffset = CGSize(width: 2.0, height: 2.0)
            shadowLayer.shadowOpacity = 0.8
            shadowLayer.shadowRadius = 2

            layer.insertSublayer(shadowLayer, at: 0)
            //layer.insertSublayer(shadowLayer, below: nil) // also works
        }

        
        [avatarImageView, durationLabel, nameLabel].forEach({addSubview($0)})
        [avatarImageView, durationLabel, nameLabel].forEach({$0.translatesAutoresizingMaskIntoConstraints = false})
        
        let constraints: [NSLayoutConstraint] = [
            avatarImageView.widthAnchor.constraint(equalToConstant: 40),
            avatarImageView.heightAnchor.constraint(equalToConstant: 40),
            avatarImageView.leftAnchor.constraint(equalTo: leftAnchor, constant: 6),
            avatarImageView.centerYAnchor.constraint(equalTo: centerYAnchor),
            nameLabel.topAnchor.constraint(equalTo: avatarImageView.topAnchor),
            nameLabel.leftAnchor.constraint(equalTo: avatarImageView.rightAnchor, constant: 10),
            nameLabel.rightAnchor.constraint(lessThanOrEqualTo: rightAnchor, constant: -4),

            durationLabel.bottomAnchor.constraint(equalTo: avatarImageView.bottomAnchor),
            durationLabel.leftAnchor.constraint(equalTo: avatarImageView.rightAnchor, constant: 10),
            durationLabel.rightAnchor.constraint(lessThanOrEqualTo: rightAnchor, constant: -4)

            
        ]
        
        NSLayoutConstraint.activate(constraints)

        avatarImageView.layer.cornerRadius = 20
        avatarImageView.layer.borderWidth = 2
        avatarImageView.layer.borderColor = UIColor.white.cgColor
        avatarImageView.clipsToBounds = true
    }
    
    func updateDuration(with text: String) {
        durationLabel.text = text
    }
    
    func updateAvatar(with image: UIImage) {
        avatarImageView.image = image
    }
    
    func updateName(with text: String) {
        nameLabel.text = text + "abc lakjfl aldkfjla dfjlakdf lkadjf vcvc dfadf dfdf "
    }
}
