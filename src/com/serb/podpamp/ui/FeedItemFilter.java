package com.serb.podpamp.ui;

import android.text.TextUtils;
import com.serb.podpamp.model.provider.Contract;

import java.io.Serializable;

public class FeedItemFilter implements Serializable {
	//region Private Members.

	private String selection;

	private long feedId = -1;

	private boolean showStarred;
	private boolean showUnreadOnly;
	private boolean showDownloadedOnly;

	//endregion

	//region Getters/Setters.

	public long getFeedId() {
		return feedId;
	}

	public void setFeedId(long feedId) {
		this.feedId = feedId;
	}

	public String getSelection()
	{
		return selection;
	}

	public boolean isShowStarred() {
		return showStarred;
	}

	public void setShowStarred(boolean showStarred) {
		this.showStarred = showStarred;
	}

	public boolean isShowUnreadOnly() {
		return showUnreadOnly;
	}

	public void setShowUnreadOnly(boolean showUnreadOnly) {
		this.showUnreadOnly = showUnreadOnly;
	}

	public boolean isShowDownloadedOnly() {
		return showDownloadedOnly;
	}

	public void setShowDownloadedOnly(boolean showDownloadedOnly) {
		this.showDownloadedOnly = showDownloadedOnly;
	}

	//endregion

	//region Public Methods.

	public String setupSelection(String initialSelection) {
		selection = initialSelection;
		if (getFeedId() > -1)
			selection = updateSelection(getSelection(), Contract.FeedItems.FEED_ID + " = " + String.valueOf(getFeedId()));
		if (isShowStarred())
		{
			selection = updateSelection(getSelection(), Contract.FeedItems.IS_STARRED + " = 1 ");
		}
		if (isShowDownloadedOnly())
		{
			selection = updateSelection(getSelection(), Contract.FeedItems.DOWNLOADED + " >= " + Contract.FeedItems.SIZE);
		}
		if (isShowUnreadOnly())
		{
			selection = updateSelection(getSelection(), Contract.FeedItems.IS_READ + " = 0 ");
		}
		return selection;
	}

	//endregion

	//region Private Methods.

	private String updateSelection(String selection, String update)
	{
		if (!TextUtils.isEmpty(selection))
			selection += " and " + update;
		else
			selection = update;
		return selection;
	}

	//endregion
}
