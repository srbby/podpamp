package com.serb.podpamp.model.managers.domain;

public class FeedItem {
	private long id;
	private String filePath;
	private int elapsed;

	public FeedItem(long id, String filePath, int elapsed) {
		setId(id);
		setFilePath(filePath);
		setElapsed(elapsed);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getElapsed() {
		return elapsed;
	}

	public void setElapsed(int elapsed) {
		this.elapsed = elapsed;
	}
}
