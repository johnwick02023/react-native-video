import Foundation
import AVFoundation
import UIKit

class WatchTrackingService {
    private var timer: Timer?
    private weak var player: AVPlayer?
    private var playerItem: AVPlayerItem?
    private var lastReportTime: Date?
    private var bitrate: Int32 = -1
    private var resolution: Int32 = 0
    private var onWatchTracking: RCTDirectEventBlock?
    private var reportedPause: Bool = false

    init(player: AVPlayer, playerItem: AVPlayerItem?, onWatchTracking: RCTDirectEventBlock?) {
        self.player = player
        self.playerItem = playerItem
        self.onWatchTracking = onWatchTracking;
    }

    func startReporting() {
        print("Start Reporting System")
        lastReportTime = Date() 

        timer = Timer.scheduledTimer(withTimeInterval: 3.0, repeats: true) { [weak self] _ in
            self?.report()
        }
        RunLoop.main.add(timer!, forMode: .common)
    }

    func stopReporting() {
        print("End Reporting System")
        timer?.invalidate()
        timer = nil
        if let last = lastReportTime {
            let elapsed = Date().timeIntervalSince(last)
            report(seconds: Int32(elapsed))
        }
    }

    private func report(seconds: Int32 = 3) {
        lastReportTime = Date()
        if player?.timeControlStatus != .waitingToPlayAtSpecifiedRate {
            handler(seconds: seconds)
        }
        
    }
    private func handler(seconds: Int32) {
        Task {
            let tracks = await RCTVideoAssetsUtils.getTracks(asset:  playerItem!.asset, withMediaType: .video)
            let presentationSize =  playerItem?.presentationSize
            if presentationSize?.height != 0.0 {
                resolution = Int32(presentationSize?.height ?? 0)
            } else if let videoTrack = tracks?.first {
                let naturalSize = videoTrack.naturalSize
                resolution = Int32(naturalSize.height)
            }
            if let lastEvent = player?.currentItem?.accessLog()?.events.last {
                bitrate = Int32(lastEvent.indicatedBitrate)
            }
            if bitrate > 0 {
                let approxBytes = Int32(bitrate) * seconds / 8

                print("📡 Report → time: \(seconds)s, res: \(resolution), bitrate: \(bitrate), bytes≈ \(approxBytes)")
                if let onWatchTracking = onWatchTracking {
                    onWatchTracking([
                        "duration": seconds,
                        "resolution": resolution,
                        "bitrate": bitrate,
                        "bytes": approxBytes
                    ])
                }
            }else{
                print("There is no any bitrate in this video")
            }
        }
        
    }
}
