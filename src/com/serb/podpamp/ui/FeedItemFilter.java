package com.serb.podpamp.ui;

import android.text.TextUtils;
import com.serb.podpamp.model.provider.Contract;

public class FeedItemFilter {
	//region Private Members.

	private String selection;

	private boolean showStarred;
	private boolean showRead;
	private boolean showUnread;
	private boolean showDownloaded;
	private boolean showNotDownloaded;

	//endregion

	//region Getters/Setters.

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

	public boolean isShowRead() {
		return showRead;
	}

	public void setShowRead(boolean showRead) {
		this.showRead = showRead;
	}

	public boolean isShowUnread() {
		return showUnread;
	}

	public void setShowUnread(boolean showUnread) {
		this.showUnread = showUnread;
	}

	public boolean isShowDownloaded() {
		return showDownloaded;
	}

	public void setShowDownloaded(boolean showDownloaded) {
		this.showDownloaded = showDownloaded;
	}

	public boolean isShowNotDownloaded() {
		return showNotDownloaded;
	}

	public void setShowNotDownloaded(boolean showNotDownloaded) {
		this.showNotDownloaded = showNotDownloaded;
	}

	//endregion

	//region Public Methods.

	public String setupSelection(String initialSelection) {
		selection = initialSelection;
		if (isShowStarred())
		{
			selection = updateSelection(selection, Contract.FeedItems.IS_STARRED + " = 1 ");
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
