/*
 * Copyright 2006-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.osgi.extender.internal.util.concurrent;

import org.springframework.util.Assert;

/**
 * Utility class that executes the given Runnable task on a newly spawn thread.
 * If the thread does not return in the given amount of time, it will be killed
 * by invoking {@link java.lang.Thread#stop}.
 * 
 * <p/> This class is intended for usage inside the framework, mainly by the
 * extender package for controlling runaway threads.
 * 
 * @see Counter
 * @see Thread
 * @author Costin Leau
 * 
 */
public abstract class RunnableTimedExecution {

	private static class MonitoredRunnable implements Runnable {
		private Runnable task;

		private Counter counter;

		public MonitoredRunnable(Runnable task, Counter counter) {
			this.task = task;
			this.counter = counter;
		}

		public void run() {
			try {
				task.run();
			}
			finally {
				counter.decrement();
			}
		}
	}

	public static boolean execute(Runnable task, long waitTime) {
		Assert.notNull(task);

		Counter counter = new Counter("counter for task: " + task);
		Runnable wrapper = new MonitoredRunnable(task, counter);

		counter.increment();

		Thread thread = new Thread(wrapper);

		thread.start();

		if (counter.waitForZero(waitTime)) {
			thread.stop();
			return true;
		}

		return false;
	}
}
