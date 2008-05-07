package net.bioclipse.usermanager;

import net.bioclipse.usermanager.UserContainer;

/**
 * This class does evil stuff with package protected variables for 
 * testing purposes
 * 
 * Motivation: An ugly test i better than no test at all...
 * 
 * @author jonalv
 *
 */
public abstract class UserContainerModifier {

    public static void addAccountType( UserContainer userContainer,
                                       AccountType accountType ) {
        userContainer.availableAccountTypes.add(accountType);
    }
}
