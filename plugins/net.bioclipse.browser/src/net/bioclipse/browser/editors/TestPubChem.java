package net.bioclipse.browser.editors;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestPubChem {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		
		final Browser browser = new Browser(shell, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl("http://www.ncbi.nlm.nih.gov/sites/entrez?db=pccompound&term=omeprazole");
		
		
		final Text text = new Text(shell, SWT.SINGLE | SWT.LEAD | SWT.READ_ONLY);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setText("test");

		browser.addProgressListener(new ProgressListener() {
			
			public void completed(ProgressEvent event) {
				// just before body.onLoad
				// execute JS code here
				
				//Should fire a BG process
				handleContent(browser.getUrl(),browser.getText());
			}
			public void changed(ProgressEvent event) {
			}
		});
		
		
		shell.setSize(800,600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	protected static void handleContent(String url, String text) {
		
		//Run regexp and extract cool things
		if (url.startsWith("http://www.ncbi.nlm.nih.gov/sites/entrez?db=pccompound")){
			extractPubChem(url,text);
		}
		
	}

	private static void extractPubChem(String url, String text) {
		
		//<a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=4594&amp;loc=ec_rcs">CID: 4594

		int ix = text.indexOf("cid=",0);
		while (ix>0){
			System.out.println(text.substring(ix,ix+10));
			ix = text.indexOf("cid=",ix+1);
		}
//		String save2Dstr="summary.cgi?cid=" + CID + "&disopt=SaveSDF";
		
	}
}
