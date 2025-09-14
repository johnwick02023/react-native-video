package com.brentvatne.exoplayer;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.TransferListener;

import java.io.IOException;

public class CountingDataSource implements DataSource {

    public interface Listener {
        void onBytesRead(long bytesRead);
    }

    private final DataSource baseDataSource;
    private long totalBytes = 0;
    private Listener listener;

    public CountingDataSource(@NonNull DataSource baseDataSource) {
        this.baseDataSource = baseDataSource;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        return baseDataSource.open(dataSpec);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        int bytesRead = baseDataSource.read(buffer, offset, readLength);
        if (bytesRead > 0) {
            totalBytes += bytesRead;
            if (listener != null) listener.onBytesRead(totalBytes);
        }
        return bytesRead;
    }

    @Override
    public Uri getUri() {
        return baseDataSource.getUri();
    }

    @Override
    public void close() throws IOException {
        baseDataSource.close();
    }

    @Override
    public void addTransferListener(@NonNull TransferListener transferListener) {
        baseDataSource.addTransferListener(transferListener);
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public static class Factory implements DataSource.Factory {
        private final DataSource.Factory baseFactory;
        private Listener listener;

        public Factory(DataSource.Factory baseFactory) {
            this.baseFactory = baseFactory;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        public DataSource createDataSource() {
            CountingDataSource source = new CountingDataSource(baseFactory.createDataSource());
            source.setListener(listener);
            return source;
        }
    }
}
