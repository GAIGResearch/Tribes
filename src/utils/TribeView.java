package utils;

import core.actors.Tribe;
import core.game.GameState;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

import static core.Constants.GUI_SIDE_PANEL_WIDTH;
import static core.Constants.GUI_TECH_PANEL_HEIGHT;


public class TribeView extends JComponent {

    private Dimension size;
    private JEditorPane textArea;
    private GameState gs;

    TribeView()
    {
        this.size = new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_TECH_PANEL_HEIGHT);

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
        if (gs != null) {
            //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Tribe[] tribes = gs.getTribes().clone();

            Tribe t_this = tribes[gs.getActiveTribeID()];
            String s = "<p><b>" + t_this.getName() + "</b>  ...........  " + t_this.getScore() + " points. Stars: "
                    + t_this.getStars() + " (+" + t_this.getMaxProduction(gs) + ")</p><br/><hr><h2>Rankings</h2>";

            // TODO: make sure this doesn't mess up assumptions of tribe order, it shouldn't since array cloned
            Arrays.sort(tribes, Comparator.comparing(Tribe::getReverseScore));
            for (Tribe t : tribes) {
                s += "<p><b>" + t.getName() + "</b>  ...........  " + t.getScore() + " points</p>";
            }

            if (!textArea.getText().equals(s)) {
                textArea.setText(s);
            }
        }
    }


    /**

     */
    void paint(GameState gameState)
    {
        this.gs = gameState;
    }


    /**
     * Gets the dimensions of the window.
     * @return the dimensions of the window.
     */
    public Dimension getPreferredSize() {
        return size;
    }

}
