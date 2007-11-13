package eu.planets_project.ifr.core.wdt.impl.gui;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.planets_project.ifr.core.wdt.impl.util.Util;

public class PLANETSWizard {
	
	
	/**
	 * Construct the Wizard
	 */
	public PLANETSWizard(Shell shell) {

	
		//Create a window
		Display display = new Display();
		//Shell shell = new Shell(display);
		
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
