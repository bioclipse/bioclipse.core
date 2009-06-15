package net.bioclipse.ds.signsig.bursi;

import net.bioclipse.ds.business.BaseDSPropertyCalculator;

/**
 * 
 * @author ola
 *
 */
public class SignSigPropertyCalculator extends BaseDSPropertyCalculator{

    @Override
    public String getPropertyName() {
        return "Signature Significance";
    }

    @Override
    public String getTestID() {
        return "signsig.bursi";
    }
    
}