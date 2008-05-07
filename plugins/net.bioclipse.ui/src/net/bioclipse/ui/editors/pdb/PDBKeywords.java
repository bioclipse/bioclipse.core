package net.bioclipse.ui.editors.pdb;

import net.bioclipse.ui.editors.keyword.Keywords;

public class PDBKeywords extends Keywords{
    
    public PDBKeywords() {
        super();
        
        String[][] PDBStringArrays = new String[7][];
        PDBStringArrays[0]=KeywordsHeader;
        PDBStringArrays[1]=keywordsComments;
        PDBStringArrays[2]=keywordsData;
        PDBStringArrays[3]=keywordsDataHet;
        PDBStringArrays[4]=keywordsDataSecondary;
        PDBStringArrays[5]=keywordsDataAtom;
        PDBStringArrays[6]=keywordsDataBond;
        
        setStringlist(PDBStringArrays);
    }
    
    
    
    public final String[] KeywordsHeader = { 
            "HEADER",
            "TITLE",
            "COMPND",
            "SOURCE",
            "AUTHOR",
            "EXPDATA",
            "REVDAT",
            "FTNOTE",
            "FORMUL",
            "MODEL",
            "TER",
            "ENDMDL",
            "MASTER",
            "EXPDTA",
            "END"
    };
    
    public final String[] keywordsComments = { 

            "JRNL",
            "REMARK",
            "DBREF",
            "KEYWDS"
    };
    
    public final String[] keywordsData = { 
            "ORIGX1",
            "ORIGX2",
            "ORIGX3",
            "SCALE",
            "MTRIX",
            "CRYST"
    };

    public final String[] keywordsDataHet = { 
            "HETATM",
            "HET"
    };

    public final String[] keywordsDataAtom = { 
            "ATOM"
    };
    
    public final String[] keywordsDataBond = { 
            "CONECT"
    };
    
    public final String[] keywordsDataSecondary = { 
            "SEQRES",
            "SEQADV",
            "SITE",
            "SHEET",
            "HELIX",
            "SSBOND"
    };
    
    
    //GETTERS
    //=======
    
    public String[] getKeywordsComments() {
        return keywordsComments;
    }

    public String[] getKeywordsData() {
        return keywordsData;
    }

    public String[] getKeywordsDataHet() {
        return keywordsDataHet;
    }

    public String[] getKeywordsDataAtom() {
        return keywordsDataAtom;
    }

    public String[] getKeywordsDataBond() {
        return keywordsDataBond;
    }

    public String[] getKeywordsDataSecondary() {
        return keywordsDataSecondary;
    }

    
    public String[] getKeywordsHeader() {
        return KeywordsHeader;
    }
    
    
    
}
