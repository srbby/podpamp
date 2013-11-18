package com.serb.podpamp.model.managers;

import com.serb.podpamp.utils.Utils;

import java.io.File;

public class EpisodeMetadata {
	private long size;
	private String sizeLabel;
	private long downloaded;

	public long feedItemId;
	public String url;
	public String fileName;
	public File file;
	public String title;

	public void setSize(long size) {
		this.size = size;
		sizeLabel = Utils.getFileSizeText(size);
	}

	public long getSize() {
		return size;
	}

	public String getSizeLabel() {
		return sizeLabel;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded, long sizeFallbackValue) {
		this.downloaded = downloaded;
		if (downloaded > size) {
			setSize(sizeFallbackValue);
		}
	}
}
