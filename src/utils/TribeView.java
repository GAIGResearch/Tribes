package utils;

import core.actors.Tribe;
import core.game.GameState;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;


public class TribeView extends JComponent {

    private Dimension size;
    private JEditorPane textArea;
    private GameState gs;

    TribeView()
    {
        this.size = new Dimension(400, 300);

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
        if(gs != null)
        {
            //For a better graphics, enable this: (be aware this could bring performance issues depending on your HW & OS).
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Tribe[] tribes = gs.getTribes();

            String s = "";

            for (Tribe t: tribes) {
                s += "<p><b>" + t.getName() + "</b>  ...........  " + t.getScore() + " points (stars: " + t.getStars() + ")</p>";
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
