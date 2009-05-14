package net.bioclipse.jobs;

import net.bioclipse.core.domain.BioObject;

/**
 * @author jonalv
 */
public class BioclipseJobUpdateHook implements IPartialReturner {

    private String jobName;
    
    public BioclipseJobUpdateHook(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
    
    public void done() {
    }

    public void partialReturn( BioObject chunk ) {

    }
}