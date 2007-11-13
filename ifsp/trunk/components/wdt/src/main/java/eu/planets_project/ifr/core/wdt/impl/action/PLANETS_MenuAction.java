package eu.planets_project.ifr.core.wdt.impl.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import eu.planets_project.ifr.core.wdt.impl.gui.PLANETSWizard;

public class PLANETS_MenuAction implements IObjectActionDelegate {

	
	/**
	 * Constructor for Action1.
	 */
	public PLANETS_MenuAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
			
		//Shell shell = new Shell();
		//MessageDialog.openInformation(
		//	shell,
		//	"Test Plug-in",
		//	"New Action was executed.");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
