package com.example.journallingapp;

import android.os.Handler;
import android.os.Looper;

public class BackgroundThread extends Thread {

    /* This code is taken from lab 7 - progress bar. */

    private Handler handler;

    @Override
    public void run() {
        super.run();
        Looper.prepare(); // Associating thread
        handler = new Handler();
        Looper.loop();
    }

    /**
     * This method is used to add different tasks to the message queue
     *
     * @param task The task being added to the handler message queue.
     */
    public void addTaskToMessageQueue(Runnable task) {
        handler.post(task); // First task1 is added to the queue then task2.
    }

    public Handler getHandler() {
        return handler;
    }
}
