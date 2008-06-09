package net.bioclipse.cdk10.rdfeditor.editor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.bioclipse.cdk10.business.CDK10Reaction;

import org.openscience.cdk.interfaces.IReaction;


public class ReactionSaveHelper {

    public static List<CDK10Reaction> extractCDK10Reactions(
                                                        ReactionTableEntry[] entries, ArrayList<String> propHeaders ) {

        List<CDK10Reaction> reactions = new ArrayList<CDK10Reaction>();
        int cnt=0;
        for (ReactionTableEntry entry : entries){
            IReaction reaction = (IReaction)entry.getReactionImpl();

//            System.out.println("Reaction: " + entry.getIndex());
            
            //Add the properties with key->value to the AC
            Hashtable<String, String> props=new Hashtable<String, String>();
            int pcnt=0;
            for (String pkey : propHeaders){
                String val = String.valueOf( entry.columns[pcnt] );
//                System.out.println("    Added property: " + pkey + " -> " + val);
                props.put( pkey, val );
                pcnt++;
            }
            
            reaction.setProperties( props );

            CDK10Reaction react = new CDK10Reaction(reaction);
            reactions.add( react );
            
            cnt++;
        }
        
        return reactions;
    }

    
    
}
