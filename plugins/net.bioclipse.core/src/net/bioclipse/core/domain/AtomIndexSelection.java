package net.bioclipse.core.domain;


public class AtomIndexSelection implements IAtomSelection{

    int[] selection;
    
    public int[] getSelection() {

        return selection;
    }

    
    public void setSelection( int[] selection ) {
    
        this.selection = selection;
    }


    public AtomIndexSelection(int[] selection) {

        super();
        this.selection = selection;
    }

}
