import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HomeworkThingListModel extends javax.swing.DefaultComboBoxModel {
	private static final long serialVersionUID = 1L;
	
	private ChangeListener listener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			itemChanged(e.getSource());
		}
	};
	
	public HomeworkThingListModel() {
		super();
	}
	
	public HomeworkThingListModel(Task[] items) {
		super(items);

		if (items != null) {
			for (int i = 0; i < items.length; ++i)
				addListenerTo(items[i]);
		}
	}
	
	public HomeworkThingListModel(Subject[] items) {
		super(items);
		
		if (items != null) {
			for (int i = 0; i < items.length; ++i)
				addListenerTo(items[i]);
		}
	}
	
	public int getSelectedIndex() {
		if (getSelectedItem() == null) return -1;
		return super.getIndexOf(getSelectedItem());
	}
	
	public void setSelectedIndex(int index) {
		if (index >= getSize()) return;
		if (index < 0) setSelectedItem(null);
		else setSelectedItem(getElementAt(index));
	}
	
	@Override
	public void addElement(Object anObject) {
		if (anObject == null) return;
//		if (anObject instanceof Subject) {
//			for (int i = 0; i < getSize(); ++i) {
//				if (getElementAt(i) instanceof Subject) {
//					if (((Subject)getElementAt(i)).getName().equals(((Subject)anObject).getName()))
//							return;
//				}
//			}
//		}
		if (anObject instanceof Task || anObject instanceof Subject) {
			super.addElement(anObject);
			addListenerTo(anObject);
		}
	}
	
	@Override
	public void insertElementAt(Object anObject, int index) {
		if (index < 0 || index >= getSize() || anObject == null)
			return;
		
		if (anObject instanceof Task || anObject instanceof Subject) {
			super.insertElementAt(anObject, index);
			addListenerTo(anObject);
		}
	}
	
	@Override
	public void removeAllElements() {
		for (int i = 0; i < getSize(); ++i)
			removeListenerFrom(getElementAt(i));
		super.removeAllElements();
	}
	
	@Override
	public void removeElement(Object anObject) {
		removeListenerFrom(anObject);
		super.removeElement(anObject);
	}
	
	@Override
	public void removeElementAt(int index) {
		if (index >= getSize() || index < 0)
			return;
		removeListenerFrom(getElementAt(index));
		super.removeElementAt(index);
	}
	
	private void addListenerTo(Object anObject) {
		if (anObject == null) return;
		if (super.getIndexOf(anObject) != -1) {
			if (anObject instanceof Task)
				((Task)anObject).addChangeListener(listener);
			else if (anObject instanceof Subject)
				((Subject)anObject).addChangeListener(listener);
		}
	}
	
	private void removeListenerFrom(Object anObject) {
		if (anObject == null) return;
		if (anObject instanceof Task)
			((Task)anObject).removeChangeListener(listener);
		else if (anObject instanceof Subject)
			((Subject)anObject).removeChangeListener(listener);
	}
	
	private void itemChanged(Object o) {
		fireContentsChanged(o, getIndexOf(o), getIndexOf(o));
	}
}
