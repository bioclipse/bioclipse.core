/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core.jobs;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
public abstract class AbstractJob implements IActionJob {
    protected boolean fail = false;
    protected ISchedulingRule rule = null;
    protected int delay = 0;
    public void setFail(boolean fail) {
        this.fail = fail;
    }
    public void setRule(ISchedulingRule rule) {
        this.rule = rule;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
    public int getDelay() {
        return this.delay;
    }
    public ISchedulingRule getRule() {
        return this.rule;
    }
}
