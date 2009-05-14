package net.bioclipse.jobs;

import net.bioclipse.core.domain.IBioObject;

/**
 * @author jonalv
 */
public class BioclipseJobUpdateHook {

    private String jobName;
    
    public BioclipseJobUpdateHook(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
    
    public void processResult(IBioObject chunk) {
    }
    
    public void done() {
    }
}