

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class PopupMenuSubject implements ActionListener, MouseListener {  
    private JPopupMenu classMenu = new JPopupMenu();
    private JMenuItem addClass = new JMenuItem("Add");
    private JMenuItem editClass = new JMenuItem("Edit");
    private JMenuItem deleteClass = new JMenuItem("Delete");
    
    public PopupMenuSubject(){
        classMenu.add(addClass);
        classMenu.add(editClass);
        classMenu.add(deleteClass);
    }
    
    public JPopupMenu getMenu(){
        return classMenu;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()){
            classMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()){
            classMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.isPopupTrigger()){
            classMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {}   
}
