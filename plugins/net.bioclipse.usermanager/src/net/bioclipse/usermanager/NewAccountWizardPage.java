package net.bioclipse.usermanager;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class NewAccountWizardPage extends WizardPage implements Listener{
	// This is the ID from the extension point
	private static final String IACCOUNTS_ID = "accountlogin.accounts";
		
	private ArrayList<Composite> accountComposites = new ArrayList<Composite>();
	private Combo accountTypeCombo;
	private Composite accountSettings;
	private StackLayout accountStack;
	private Object[] plugins;
	
	protected NewAccountWizardPage(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		Composite accountType = new Composite(container, SWT.NONE);
		GridData gd_accountType = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_accountType.widthHint = 430;
		gd_accountType.heightHint = 40;
		accountType.setLayoutData(gd_accountType);
				
		Label accountTypeTxt = new Label(accountType, SWT.NONE);
		accountTypeTxt.setBounds(10, 10, 120, 14);
		accountTypeTxt.setText("Select account type:");
		
		accountTypeCombo = new Combo(accountType, SWT.NONE | SWT.READ_ONLY);
		accountTypeCombo.setBounds(130, 10, 280, 22);
		accountTypeCombo.addListener(SWT.Selection, this);
		
		accountSettings = new Composite(container, SWT.NONE);
		GridData gd_accountSettings = new GridData(GridData.FILL_BOTH);
		gd_accountSettings.grabExcessVerticalSpace = false;
		gd_accountSettings.verticalAlignment = SWT.TOP;
		gd_accountSettings.heightHint = 48;
		accountType.setLayoutData(gd_accountSettings);
		accountStack = new StackLayout();
		accountSettings.setLayout(accountStack);
		
		getAccountDetails(accountSettings);
		accountStack.topControl = accountComposites.get(0);
		accountTypeCombo.select(0);
		setControl(container);
	}
	
	/**
	 * Add the account types (i.e. the IAccounts plug-ins)  name to the 
	 * combo-box and there contains to the array-list that handles them (i.e.
	 *  acconutComposites).
	 *  
	 * @param container The container to put the account types items in.
	 */
	private void getAccountDetails(Composite container) {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(IACCOUNTS_ID);
		if (config.length > 0) {
			final Composite cont = container;	
			plugins = new Object[config.length];
			try {
				for (int i=0;i<config.length;i++) {
					plugins[i] = config[i].createExecutableExtension("class");

					final Object o = plugins[i];
					if (o instanceof IAccounts) {
						ISafeRunnable runnable = new ISafeRunnable() {
							@Override
							public void handleException(Throwable exception) {
								System.out.println("Exception in client");
							}

							@Override
							public void run() throws Exception {
								accountComposites.add(
										((IAccounts) o).createComposite(cont));
								accountTypeCombo.add(((IAccounts) o).getName());

							}
						};
						SafeRunner.run(runnable);
					}
				}

			} catch (CoreException ex) {
				System.out.println(ex.getMessage());
			}
		} else {
			accountComposites.add(empty(container));
		}
	}
	
	/**
	 * This method handles the combobox, i.e. it shows the selected account's
	 * composite below the combobox.
	 */
	@Override
	public void handleEvent(Event event) {
		if (event.widget == accountTypeCombo) {
			if (accountTypeCombo.getSelectionIndex() == -1){
				System.out.println("Please select an account-type");
			} else if (accountTypeCombo.getSelectionIndex() < 
					accountComposites.size()) { 
				accountStack.topControl = accountComposites.get(
						accountTypeCombo.getSelectionIndex());
				accountSettings.layout();
				giveFocus();
			}
		}	
	}
	
	/**
	 * This method tell the selected plug-in to create an account.
	 */
	private void giveFocus() {
		try {
			final Object o = plugins[accountTypeCombo.getSelectionIndex()];
			if (o instanceof IAccounts) {
				ISafeRunnable runnable = new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
						System.out.println("Exception in client");
					}

					@Override
					public void run() throws Exception {
						((IAccounts) o).setFocus();				
					}
				};
				SafeRunner.run(runnable);			
			}
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	/**
	 * A method that creates a composite to be used when there isn't any 
	 * plug-ins found.
	 * 
	 * @param text A message to be showed in the composite 
	 * @return A composite with a text in it.
	 */
	private Composite empty(Composite parent) {
		Composite emptyComposite = new Composite(parent, SWT.NONE); 
		GridData gd_as = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_as.widthHint = 430;
		gd_as.heightHint = 161;
		emptyComposite.setLayoutData(gd_as);
		GridLayout gl = new GridLayout(2,false);
		emptyComposite.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		new Label(emptyComposite, SWT.CENTER).setText("Cant find any accounts");
				
		return emptyComposite;
	}
}
