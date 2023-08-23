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
        label.textColor = .white
        label.textAlignment = .center
        return label
    }()
    
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.text = "Nguyen Van A"
        label.textColor = .white
        label.textAlignment = .center
        return label
    }()
    
    init() {
        super.init(frame: .zero)
        setupUIs()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupUIs() {
        [avatarImageView, durationLabel, nameLabel].forEach({addSubview($0)})
        [avatarImageView, durationLabel, nameLabel].forEach({$0.translatesAutoresizingMaskIntoConstraints = false})
        
        let constraints: [NSLayoutConstraint] = [
            avatarImageView.widthAnchor.constraint(equalToConstant: 40),
            avatarImageView.heightAnchor.constraint(equalToConstant: 40),
            avatarImageView.leftAnchor.constraint(equalTo: leftAnchor, constant: 6),
            avatarImageView.centerYAnchor.constraint(equalTo: centerYAnchor),
            nameLabel.topAnchor.constraint(equalTo: avatarImageView.topAnchor),
            nameLabel.leftAnchor.constraint(equalTo: avatarImageView.rightAnchor, constant: 10),

            durationLabel.bottomAnchor.constraint(equalTo: avatarImageView.bottomAnchor),
            durationLabel.leftAnchor.constraint(equalTo: avatarImageView.rightAnchor, constant: 10),
            
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
        nameLabel.text = text
    }
}
