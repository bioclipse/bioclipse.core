package net.bioclipse.ui.install.handler;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.epp.mpc.ui.MarketplaceClient;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.ui.discovery.repository.RepositoryDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenDiscoveryDialog extends AbstractHandler{
	private static Logger logger = LoggerFactory.getLogger(OpenDiscoveryDialog.class);
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    MarketplaceClient.openMarketplaceWizard(null);
		return null;
	}

}
