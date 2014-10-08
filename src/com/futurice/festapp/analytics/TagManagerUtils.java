package com.futurice.festapp.analytics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.tagmanager.Container;
import com.google.tagmanager.ContainerOpener;
import com.google.tagmanager.DataLayer;
import com.google.tagmanager.TagManager;
import com.google.tagmanager.ContainerOpener.OpenType;
import com.google.tagmanager.Logger.LogLevel;

public class TagManagerUtils {
	private static final long TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS = 2000;
	private static final String CONTAINER_ID = "GTM-5XFNM5";

	private static Container container;

	public static Container getContainer() {
		return container;
	}

	public static void setContainer(Container c) {
		container = c;
	}

	public static void initTagManager(Context context,
			final Runnable onContainerOpened) {
		TagManager tagManager = TagManager.getInstance(context);
		tagManager.getLogger().setLogLevel(LogLevel.VERBOSE);

		ContainerOpener.openContainer(tagManager, CONTAINER_ID,
				OpenType.PREFER_NON_DEFAULT,
				TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS,
				new ContainerOpener.Notifier() {

					@Override
					public void containerAvailable(Container container) {
						setContainer(container);
						onContainerOpened.run();
					}
				});
	}

	public static void pushOpenScreenEvent(Context context, String screenName) {
		Log.d("TagManagerUtils", "openScreen: " + screenName);
		DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
		dataLayer.push(DataLayer.mapOf("screenName", screenName, "event",
				"openScreen"));
	}

	public static void pushCloseScreenEvent(Context context, String screenName) {
		Log.d("TagManagerUtils", "closeScreen: " + screenName);
		DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
		dataLayer.push(DataLayer.mapOf("screenName", screenName, "event",
				"closeScreen"));
	}
}
