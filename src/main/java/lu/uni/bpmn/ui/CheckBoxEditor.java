package lu.uni.bpmn.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import lu.uni.bpmn.DataProtectionBPMNPlugin;
import lu.uni.bpmn.model.Item;
import lu.uni.bpmn.model.ModelFactory;
import lu.uni.bpmn.model.Value;

public class CheckBoxEditor extends ObjectEditor {
	protected Composite editorComposite;
	protected Map<String, String> optionMap;
	Item item = null;

	/**
	 * Initializes a new ChackBox Editor with a given Option List containing
	 * Keys and Lable Strings
	 * 
	 * @param Item
	 * @param OptionMap
	 */
	public CheckBoxEditor(AbstractDetailComposite parent, Item item,
			Map<String, String> optionMap) {
		super(parent, item, DataProtectionBPMNPlugin.DATAPROTECTION_ITEMLIST_FEATURE);
		this.item = item;
		this.optionMap = optionMap;
	}

	/**
	 * This method creates a composite with a separate CheckBox for each Element
	 * form the OpitonList. The selection is managed by the valueListAdapter
	 * class.
	 */
	protected Control createControl(Composite composite, String label, int style) {

		// create a separate label to the LEFT of the checkbox set
		if (label != null) {
			Label labelWidget = getToolkit().createLabel(composite, label);
			labelWidget.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
					false, 1, 1));
			updateLabelDecorator();
		}
		editorComposite = new Composite(composite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		editorComposite.setLayoutData(data);
		editorComposite.setLayout(new FillLayout(SWT.VERTICAL));

		// create a checkbox for each entry from the OptionList
		Iterator<Entry<String, String>> iter=optionMap.entrySet().iterator();
		//for (Map.Entry<String, String> entry : optionMap.entrySet()) {
		while (iter.hasNext()) {
			Map.Entry<String, String>  entry = iter.next();
			final String aKey = entry.getKey();
			final String aLabel = entry.getValue();

			Button button = getToolkit().createButton(editorComposite, aLabel,
					SWT.CHECK);
			button.setSelection(isSelected(aKey));
			button.setData(aKey);
			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button bb = (Button) e.getSource();
					if (!isWidgetUpdating) {
						boolean checked = bb.getSelection();
						if (checked)
							addKey(aKey);
						else
							removeKey(aKey);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}
		return editorComposite;
	}

	/**
	 * Tests if the item EObject contains a value with the given key
	 * 
	 * @param key
	 */
	public boolean isSelected(final String key) {

		if (key == null)
			return false;

		for (Value aValue : item.getValuelist()) {
			if (key.equals(aValue.getValue())) {
				return true;
			}
		}

		// not found
		return false;
	}

	/**
	 * This method iterates over all existing values in the Item EObject and to
	 * test if the given key is already containing. If not the method adds a
	 * new Value EObject with the given key
	 * 
	 * @param key
	 */
	public void addKey(final String key) {
		
		// test if key still exists....

		if (key == null)
			return ;

		for (Value aValue : item.getValuelist()) {
			if (key.equals(aValue.getValue())) {
				return ;
			}
		}
		
		
		// no key found - so we now add a new Value EObject with that key
		TransactionalEditingDomain domain = getDiagramEditor()
				.getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {

				// insert a new value element
				Value value = ModelFactory.eINSTANCE.createValue();
				value.setValue(key);
				item.getValuelist().add(value);

			}
		});
		
		
		
	}
	
	
	
	
	/**
	 * This method iterates over all existing values in the Item EObject  to
	 * test if the given key is already containing. If  the method removes the 
	 * Value EObject with the given key
	 * 
	 * @param key
	 */
	public void removeKey(final String key) {
		
		//  key found - so we now remove the Value EObject 
		TransactionalEditingDomain domain = getDiagramEditor()
				.getEditingDomain();
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			protected void doExecute() {
				// iterate over all item values
				Iterator<Value> iter = item.getValuelist().iterator();
				while (iter.hasNext()) {

					Value val = iter.next();
					if (key.equals(val.getValue())) {
						item.getValuelist().remove(val);
						break;
					}
				}
			}
		});
	}
	
	

	@Override
	public Item getValue() {
		return item;
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		editorComposite.setVisible(visible);
		GridData data = (GridData) editorComposite.getLayoutData();
		data.exclude = !visible;
	}

	public void dispose() {
		super.dispose();
		if (editorComposite != null && !editorComposite.isDisposed()) {
			editorComposite.dispose();
			editorComposite = null;
		}
	}

	public Control getControl() {
		return editorComposite;
	}
}
