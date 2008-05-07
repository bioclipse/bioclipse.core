package net.bioclipse.recording;

public class BioObjectRecord extends MethodRecord {

    protected String bioObjectId;
    
    public BioObjectRecord( String methodName,
                            String bioObjectId,
                            Object[] parameters,
                            Object returnValue  ) {
        
        super(methodName, parameters, returnValue);
        this.bioObjectId = bioObjectId;
    }

    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < paramaters.size(); i++) {
            Parameter p = paramaters.get(i);
            
            if( p instanceof NonBioObjectParameter ) {
                sb.append( ( (NonBioObjectParameter)p ).stringRepresentation );
            }
            else if( p instanceof BioObjectParameter) {
                sb.append(p.type);
            }
            else {
                throw new IllegalStateException( "Unrecognized " +
                                                 "paramater type: " + p ); 
            }
            
            if(i != paramaters.size() - 1) {
                sb.append(", ");
            }
            else {
                sb.append(' ');
            }
        }
        
        //TODO: connect the right variable here
        return "bioObject" + "." + methodName + "( "
            + sb.toString() + ")";
    }
    
}
