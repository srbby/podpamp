package com.serb.podpamp.model.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FeedUrlsContainer implements Parcelable {
	private List<String> urls;

	public FeedUrlsContainer(List<String> urls) {
		this.urls = urls;
	}

	public FeedUrlsContainer(Parcel in) {
		urls = new ArrayList<String>();
		in.readStringList(urls);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeStringList(urls);
	}

	public static final Parcelable.Creator<FeedUrlsContainer> CREATOR
			= new Parcelable.Creator<FeedUrlsContainer>() {
		public FeedUrlsContainer createFromParcel(Parcel in) {
			return new FeedUrlsContainer(in);
		}

		public FeedUrlsContainer[] newArray(int size) {
			return new FeedUrlsContainer[size];
		}
	};

	public List<String> getUrls() {
		return urls;
	}
}
