package net.bioclipse.ds.signsig.bursi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.ds.model.ITestResult;

/**
 * 
 * @author ola
 *
 */
public class DSTestsProperty {

    Map<String, List<ITestResult>> result;

    
    public DSTestsProperty() {
        result=new HashMap<String, List<ITestResult>>();
    }


    public Map<String, List<ITestResult>> getResult() {
    
        return result;
    }

    
    public void setResult( Map<String, List<ITestResult>> result ) {
    
        this.result = result;
    }

    
}
