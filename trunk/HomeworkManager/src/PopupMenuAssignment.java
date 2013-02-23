
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class PopupMenuAssignment implements ActionListener, MouseListener {
    private JPopupMenu assignmentMenu = new JPopupMenu();
    private JMenuItem addAssignment = new JMenuItem("Add Assignment");
    private JMenuItem editAssignment = new JMenuItem("Edit Assignment");
    private JMenuItem deleteAssignment = new JMenuItem("Delete Assignment");
    private JMenu view = new JMenu("View");
    private JMenuItem nextDay = new JMenuItem("Next Day");
    private JMenuItem prevDay = new JMenuItem("Previous Day");
    private JMenuItem today = new JMenuItem("Today");
    private JMenuItem threeDay= new JMenuItem("3 Days");
    private JMenuItem fiveDay = new JMenuItem("5 Days");
    private JMenuItem sevenDay = new JMenuItem("7 Days");
    private JMenuItem completed = new JMenuItem("Show Completed Tasks");
    private JMenu sort = new JMenu("Sort by");
    private JMenuItem due = new JMenuItem("Due Date");
    private JMenuItem start = new JMenuItem("Start Date");
    private JMenuItem nextWork = new JMenuItem("Next Work Time");
    private JMenuItem priority = new JMenuItem("Priority");
    private JMenu subjects = new JMenu("Subjects");
    private JMenuItem calc = new JMenuItem("Calculus");
    private JMenuItem hist = new JMenuItem("History");
    private JMenuItem hci = new JMenuItem("Human-Computer Interaction");
    private JMenuItem ps = new JMenuItem("Public Speaking");
    
    public PopupMenuAssignment(){     
        assignmentMenu.add(addAssignment);
        assignmentMenu.add(editAssignment);
        assignmentMenu.add(deleteAssignment);
        assignmentMenu.addSeparator();
        view.add(completed);
        view.addSeparator();
        view.add(nextDay);
        view.add(prevDay);
        view.addSeparator();
        view.add(threeDay);
        view.add(fiveDay);
        view.add(sevenDay);
        assignmentMenu.add(view);
        assignmentMenu.addSeparator();
        sort.add(due);
        sort.add(start);
        sort.add(nextWork);
        sort.add(priority);
        assignmentMenu.add(sort);
        assignmentMenu.addSeparator();
        subjects.add(calc);
        subjects.add(hist);
        subjects.add(hci);
        subjects.add(ps);
        assignmentMenu.add(subjects);
    }
    public JPopupMenu getMenu(){
        return assignmentMenu;
    }
    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()){
            assignmentMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()){
            assignmentMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.isPopupTrigger()){
            assignmentMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {}
}
