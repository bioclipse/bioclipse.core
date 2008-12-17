package net.bioclipse.webservices.wizards;
/**
 * 
 * The Wizard to handle job results.
 * 
 * @author edrin
 *
 */
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.IWizardPage;
import net.bioclipse.webservices.wizards.WebServiceWizardData;
import net.bioclipse.webservices.wizards.wizardpages.IDoPerformFinish;
public class JobFinishedWizard extends Wizard {
        private WebServiceWizardData data;
        private IWizardPage page;
        public JobFinishedWizard(WebServiceWizardData data, IWizardPage page, String title) {
                super();
                this.data = data;
                this.page = page;
                setWindowTitle(title);
                setNeedsProgressMonitor(true);				
        }
        public void addPages() {		
                addPage(page);
        }
        public boolean performFinish() {
                if(page instanceof IDoPerformFinish)
                {
                        ((IDoPerformFinish)page).DoPerformFinish();
                }
                return true;
        }
        public boolean canFinish() {
                return data.canFinish();
        }
}