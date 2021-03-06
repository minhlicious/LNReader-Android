package com.erakk.lnreader.task;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.erakk.lnreader.callback.CallbackEventData;
import com.erakk.lnreader.callback.ICallbackEventData;
import com.erakk.lnreader.callback.ICallbackNotifier;
import com.erakk.lnreader.dao.NovelsDao;
import com.erakk.lnreader.helper.AsyncTaskResult;
import com.erakk.lnreader.model.NovelContentModel;
import com.erakk.lnreader.model.PageModel;

public class DownloadNovelContentTask extends AsyncTask<Void, ICallbackEventData, AsyncTaskResult<NovelContentModel[]>> implements ICallbackNotifier {
	private static final String TAG = DownloadNovelContentTask.class.toString();
	private final PageModel[] chapters;
	public volatile IAsyncTaskOwner owner;
	private int currentChapter = 0;
	private final String taskId;

	public DownloadNovelContentTask(PageModel[] chapters, IAsyncTaskOwner owner) {
		super();
		this.chapters = chapters;
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
	protected AsyncTaskResult<NovelContentModel[]> doInBackground(Void... params) {
		ArrayList<Exception> exceptionList = new ArrayList<Exception>();
		try {
			NovelContentModel[] contents = new NovelContentModel[chapters.length];
			for (int i = 0; i < chapters.length; ++i) {
				currentChapter++;
				NovelContentModel oldContent = NovelsDao.getInstance().getNovelContent(chapters[i], false, null);
				String message = String.format("Downloading: %s", chapters[i].getTitle());
				if (oldContent != null) {
					message = String.format("Updating: %s", chapters[i].getTitle());
				}
				Log.i(TAG, message);
				publishProgress(new CallbackEventData(message));

				try {
					NovelContentModel temp = NovelsDao.getInstance().getNovelContentFromInternet(chapters[i], this);
					contents[i] = temp;
				} catch (Exception e) {
					Log.e(TAG, String.format("Error when downloading: %s", chapters[i].getTitle()), e);
					exceptionList.add(e);
				}

			}
			if (exceptionList.size() > 0) {
				return new AsyncTaskResult<NovelContentModel[]>(exceptionList.get(exceptionList.size() - 1));
			}
			return new AsyncTaskResult<NovelContentModel[]>(contents);
		} catch (Exception e) {
			return new AsyncTaskResult<NovelContentModel[]>(e);
		}
	}

	@Override
	protected void onProgressUpdate(ICallbackEventData... values) {
		// executed on UI thread.
		owner.setMessageDialog(values[0]);
		owner.updateProgress(this.taskId, currentChapter, chapters.length, values[0].getMessage());
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<NovelContentModel[]> result) {
		owner.getResult(result);
		owner.downloadListSetup(this.taskId, null, 2, result.getError() != null ? true : false);
	}
}