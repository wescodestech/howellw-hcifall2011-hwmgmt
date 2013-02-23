

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class PopupMenuInfo implements ActionListener, MouseListener {  
    private JPopupMenu infoMenu = new JPopupMenu();
    private JMenuItem editAssignment = new JMenuItem("Edit Assignment");
    private JMenuItem deleteAssignment = new JMenuItem("Delete Assignment");
    
    public PopupMenuInfo(){
        infoMenu.add(editAssignment);
        infoMenu.add(deleteAssignment);
    }
    public JPopupMenu getMenu(){
        return infoMenu;
    }
    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()){
            infoMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()){
            infoMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.isPopupTrigger()){
            infoMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {}
}
