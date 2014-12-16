package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.util.Log;
import android.widget.ImageView;

import java.util.LinkedList;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;

/**
 * Created by francisco on 11-12-2014.
 */
public class SynchronizedDownloadList {

    private static final String TAG = "DOWNLOAD_LIST";
    private static final int MAX_LENGHT = 1;

    private LinkedList<DownloadTask> taskList;
    private int totalTasks;

    public SynchronizedDownloadList() {
        this.taskList = new LinkedList<DownloadTask>();
        this.totalTasks = 0;
    }

    public boolean contains(String url) {
        boolean exist = false;

        for (DownloadTask task : this.taskList) {
            if (url == task.getUrl()) {
                exist = true;
                break;
            }
        }

        return exist;
    }

    public void clearList() {
        taskList.clear();
        totalTasks = 0;
    }

    public synchronized void addDownloadTask(DownloadTask task)
            throws InterruptedException {

        while (totalTasks == MAX_LENGHT)
            wait();

        taskList.add(task);
        Log.i(TAG, "Add Image URL - " + task.getUrl());
        ++totalTasks;

        notify();
    }

    public synchronized DownloadTask getDownloadTask() throws InterruptedException {

        while (totalTasks == 0)
            wait();

        DownloadTask task = taskList.remove();
        Log.i(TAG, "Get Image URL - " + task.getUrl());
        --totalTasks;

        notify();

        return task;

    }

    public synchronized boolean isEmpty() {
        return taskList.isEmpty();
    }

    public synchronized void removeDownloadTaskByImageView(ImageView image) {
        int index = 0;

        for (index = 0; index < taskList.size(); index++)
            if (taskList.get(index).getImageView() == image)
                break;

        if (index < taskList.size()) { // encontrei
            taskList.remove(index);
        }
    }

}
