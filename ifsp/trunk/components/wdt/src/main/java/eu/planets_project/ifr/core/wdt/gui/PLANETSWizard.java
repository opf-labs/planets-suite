package eu.planets_project.ifr.core.wdt.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;




public class PLANETSWizard {
	
	
	/**
	 * Construct the Wizard
	 */
	public PLANETSWizard() {

		//Create a window
		//Display display = new Display();
		//Shell shell = new Shell(display);
		
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		
		
        shell.setText("PLANETS Wizard");
		shell.setBounds(100,100,175,125);
		shell.setLayout(new FillLayout());
		
		//Create a Tabfolder - for additional functionality of the PLANETS
		//Wizard in later stages
		final TabFolder tabfolder = new TabFolder(shell, SWT.BORDER);
		
		//Add a single tab
		
		TabItem tabItem = new TabItem(tabfolder, SWT.NULL);
		tabItem.setText("BPEL Cleaner");
		Composite composite = new Composite(tabfolder, SWT.NULL);
		tabItem.setControl(composite);
		
		//Add a Button to start the BPEL Cleaner
		Button button = new Button(composite, SWT.PUSH);
		button.setBounds(25,25,100,25);
		button.setText("Start BPEL Cleaner");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("BPEL Cleaner started");
			} 
		});
		

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}			
		}
		display.dispose();
						
	}
	
	
	
	
	
	
	
	
	

}
