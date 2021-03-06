package com.erakk.lnreader.task;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.erakk.lnreader.callback.CallbackEventData;
import com.erakk.lnreader.callback.ICallbackEventData;
import com.erakk.lnreader.callback.ICallbackNotifier;
import com.erakk.lnreader.dao.NovelsDao;
import com.erakk.lnreader.helper.AsyncTaskResult;
import com.erakk.lnreader.model.NovelCollectionModel;
import com.erakk.lnreader.model.PageModel;

public class DownloadNovelDetailsTask extends AsyncTask<PageModel, ICallbackEventData, AsyncTaskResult<ArrayList<NovelCollectionModel>>> implements ICallbackNotifier {
	public volatile IAsyncTaskOwner owner;
	private int currentPart = 0;
	private int totalParts = 0;
	private final String taskId;

	public DownloadNovelDetailsTask(IAsyncTaskOwner owner) {
		this.owner = owner;
		this.taskId = this.toString();
	}

	@Override
	protected void onPreExecute() {
		// executed on UI thread.
		// owner.toggleProgressBar(true);
		boolean exists = false;
		exists = owner.downloadListSetup(this.taskId, null, 0, false);
		if (exists)
			this.cancel(true);
	}

	@Override
	public void onCallback(ICallbackEventData message) {
		publishProgress(message);
	}

	@Override
	protected AsyncTaskResult<ArrayList<NovelCollectionModel>> doInBackground(PageModel... params) {
		ArrayList<NovelCollectionModel> result = new ArrayList<NovelCollectionModel>();
		totalParts = params.length;
		for (PageModel pageModel : params) {
			currentPart++;
			try {
				publishProgress(new CallbackEventData("Downloading chapter list for: " + pageModel.getTitle()));
				NovelCollectionModel novelCol = NovelsDao.getInstance().getNovelDetailsFromInternet(pageModel, this);
				Log.d("DownloadNovelDetailsTask", "Downloaded: " + novelCol.getPage());
				result.add(novelCol);
			} catch (Exception e) {
				Log.e("DownloadNovelDetailsTask", "Failed to download novel details for " + pageModel.getPage() + ": " + e.getMessage(), e);
				return new AsyncTaskResult<ArrayList<NovelCollectionModel>>(e);
			}
		}
		return new AsyncTaskResult<ArrayList<NovelCollectionModel>>(result);
	}

	@Override
	protected void onProgressUpdate(ICallbackEventData... values) {
		// executed on UI thread.
		owner.setMessageDialog(values[0]);
		owner.updateProgress(this.taskId, currentPart, totalParts, values[0].getMessage());
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<ArrayList<NovelCollectionModel>> result) {
		owner.getResult(result);
		owner.downloadListSetup(this.taskId, null, 2, result.getError() != null ? true : false);
	}
}
