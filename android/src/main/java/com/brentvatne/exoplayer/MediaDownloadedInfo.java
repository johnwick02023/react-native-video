package com.brentvatne.exoplayer;

public class MediaDownloadedInfo implements Cloneable{
    public int quality;
    public int bitrate;
    public double duration;
    public long bytes;
    public int count;
    public MediaDownloadedInfo(int quality, int bitrate, double duration, long bytes, int count){
        this.quality = quality;
        this.bitrate = bitrate;
        this.duration = duration;
        this.bytes = bytes;
        this.count = count;
    }
    public String toString(){
        return "Duration: " + duration+ "s, Size: " + bytes+", Quality: " + quality + ", Count: " + count + ", Bitrate: " + bitrate;
    }
    @Override
    public MediaDownloadedInfo clone() {
        try {
            return (MediaDownloadedInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
