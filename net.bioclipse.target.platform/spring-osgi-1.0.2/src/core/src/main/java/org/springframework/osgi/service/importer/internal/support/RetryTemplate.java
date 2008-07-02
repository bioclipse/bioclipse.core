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
package org.springframework.osgi.service.importer.internal.support;

import org.springframework.util.Assert;

/**
 * Wrapper retry template.
 * 
 * @author Costin Leau
 */
public class RetryTemplate {

	private static final int hashCode = RetryTemplate.class.hashCode() * 13;

	public static final long DEFAULT_WAIT_TIME = 1000;

	public static final int DEFAULT_RETRY_NUMBER = 3;

	private long waitTime = DEFAULT_WAIT_TIME;

	private int retryNumbers = DEFAULT_RETRY_NUMBER;

	public RetryTemplate() {
	}

	public RetryTemplate(int retryNumbers, long waitTime) {
		Assert.isTrue(retryNumbers >= 0, "retryNumbers must be positive");
		Assert.isTrue(waitTime >= 0, "waitTime must be positive");

		this.retryNumbers = retryNumbers;
		this.waitTime = waitTime;
	}

	public RetryTemplate(RetryTemplate template) {
		this(template.getRetryNumbers(), template.getWaitTime());
	}

	public Object execute(RetryCallback callback, Object notificationLock) {
		Assert.notNull(callback, "callback is required");
		Assert.notNull(notificationLock, "notificationLock is required");

		int count = 0;
		synchronized (notificationLock) {
			do {
				Object result = callback.doWithRetry();
				if (callback.isComplete(result))
					return result;

				// task is not complete - retry
				count++;
				if (waitTime != 0) {
					// Do NOT use Thread.sleep() here - it does not release
					// locks.
					try {
						notificationLock.wait(waitTime);
					}
					catch (InterruptedException ex) {
						throw new RuntimeException("retry failed; interrupted while sleeping", ex);
					}
				}
			} while (count < retryNumbers);
		}
		return null;
	}

	public Object execute(RetryCallback callback) {
		return execute(callback, this);
	}

	public int getRetryNumbers() {
		return retryNumbers;
	}

	public void setRetryNumbers(int retryNumbers) {
		this.retryNumbers = retryNumbers;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof RetryTemplate) {
			RetryTemplate oth = (RetryTemplate) other;

			return (waitTime == oth.waitTime && retryNumbers == oth.retryNumbers);
		}
		return false;
	}

	public int hashCode() {
		return hashCode;
	}

}
