package utils;

import core.TechnologyTree;
import core.Types;
import core.actors.Tribe;
import core.game.GameState;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class TechView extends JComponent {

    private Dimension size;
    private JEditorPane textArea;
    private GameState gs;

    TechView()
    {
        this.size = new Dimension(400, 500);

        textArea = new JEditorPane("text/html", "");
        textArea.setPreferredSize(this.size);
        Font textFont = new Font(textArea.getFont().getName(), Font.PLAIN, 12);
        textArea.setFont(textFont);
        textArea.setEditable(false);
        textArea.setBackground(Color.lightGray);
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        this.setLayout(new FlowLayout());
        this.add(textArea);
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
                String s = "<ul>";
                for (Types.TECHNOLOGY opt : Types.TECHNOLOGY.values()) {
                    boolean researched = tt.isResearched(opt);
                    boolean researchable = tt.isResearchable(opt);
                    s += "<li>";
                    s += (researched?"<b><span color=\"green\">":researchable?"<span color=\"blue\">":"<s>") + opt
                            + (researched?"</span></b>":researchable?"</span>":"</s>")
                            + ": " + (researched?"researched" : researchable?"researchable":"--");
                    s += "</li>";
                }
                s += "</ul>";
                if (!textArea.getText().equals(s)) {
                    textArea.setText(s);
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
