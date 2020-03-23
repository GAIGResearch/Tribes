package utils;

import core.TechnologyTree;
import core.Types;
import core.actions.tribeactions.ResearchTech;
import core.actors.Tribe;
import core.game.GameState;
import players.ActionController;

import javax.swing.*;
import java.awt.*;

import static core.Constants.*;

public class TechView extends JComponent {

    private Dimension size;
    JButton[] techs;
    private GameState gs;
    ActionController ac;

    TechView(ActionController ac)
    {
        this.setLayout(new FlowLayout());  // TODO: organize these based on tech tree structure (place underneath parents)

        this.ac = ac;
        this.size = new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_TECH_PANEL_HEIGHT);
        JPanel buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_TECH_PANEL_FULL_SIZE));
        techs = new JButton[Types.TECHNOLOGY.values().length];
        for (int i = 0; i < Types.TECHNOLOGY.values().length; i++) {
            Types.TECHNOLOGY t = Types.TECHNOLOGY.values()[i];
            JButton button = new JButton(t.name());
            button.setBackground(Color.DARK_GRAY);
            button.addActionListener(e -> {
                ResearchTech a = new ResearchTech(gs.getActiveTribeID());
                a.setTech(t);  // TODO: confirmation
                ac.addAction(a, gs);
            });
            techs[i] = button;
            buttons.add(techs[i]);
        }
        JScrollPane scrollPane = new JScrollPane(buttons);
        scrollPane.setPreferredSize(size);
        this.add(scrollPane);
    }


    public void paintComponent(Graphics gx)
    {
        Graphics2D g = (Graphics2D) gx;
        paintWithGraphics(g);
    }

    private void paintWithGraphics(Graphics2D g)
    {
        //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Tribe t = gs.getActiveTribe();
        if (t != null) {
            TechnologyTree tt = t.getTechTree();
            if (tt != null) {
                for (int i = 0; i < Types.TECHNOLOGY.values().length; i++) {
                    Types.TECHNOLOGY opt = Types.TECHNOLOGY.values()[i];
                    boolean researched = tt.isResearched(opt);
                    boolean researchable = tt.isResearchable(opt) && t.getStars() >= opt.getCost(t.getCitiesID().size());
                    if (!(researchable || researched)) {
                        techs[i].setEnabled(false);
                        techs[i].setBackground(Color.DARK_GRAY);
                        techs[i].setToolTipText("Not Available");
                    } else {
                        if (researched) {
                            techs[i].setEnabled(false);
                            techs[i].setToolTipText("Researched");
                            techs[i].setBackground(new Color(4, 77, 118));
                            techs[i].setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                        } else {
                            techs[i].setEnabled(true);
                            techs[i].setToolTipText("Researchable");
                            techs[i].setBackground(Color.DARK_GRAY);
                            techs[i].setForeground(new Color(78, 255, 113));
                            techs[i].setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
                        }
                    }
                }
            }
        }
    }


    void paint(GameState gs)
    {
        this.gs = gs;
        this.repaint();
    }

    /**
     * Gets the dimensions of the window.
     * @return the dimensions of the window.
     */
    public Dimension getPreferredSize() {
        return size;
    }
}
