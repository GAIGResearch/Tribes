package utils;

import core.TechnologyTree;
import core.Types;
import core.actors.Tribe;
import core.game.GameState;
import players.ActionController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static core.Constants.*;

public class TechView extends JComponent {

    private Dimension size;
    JButton[] techs;
    String[] techEffects;
    private GameState gs;
    ActionController ac;
    TechnologyNode[] technologies;
    ArrayList<TechnologyNode> roots, leaves;
    InfoView infoView;

    TechView(ActionController ac, InfoView infoView)
    {
        this.setLayout(new FlowLayout());
        this.infoView = infoView;

        this.ac = ac;
        this.size = new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_TECH_PANEL_HEIGHT);
        JPanel buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(GUI_SIDE_PANEL_WIDTH, GUI_TECH_PANEL_FULL_SIZE));
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;
        buttons.setLayout(gbl);

        // Get tech tree structure
        int nTechs = Types.TECHNOLOGY.values().length;
        roots = new ArrayList<>();
        leaves = new ArrayList<>();
        technologies = new TechnologyNode[nTechs];
        for (int i = 0; i < nTechs; i++) {
            technologies[i] = new TechnologyNode(Types.TECHNOLOGY.values()[i]);
            if (technologies[i].isRoot()) {
                roots.add(technologies[i]);
            }
        }
        for (int i = 0; i < nTechs; i++) {
            technologies[i].setChildren();
            technologies[i].getRootIdx(roots);
            if (technologies[i].isLeaf()) {
                leaves.add(technologies[i]);
            }
        }

        techs = new JButton[nTechs];
        for (int i = 0; i < nTechs; i++) {
            TechnologyNode node = technologies[i];
            gbc.gridy = node.getRootIdx(roots) + node.getOffsetIdx(leaves) + node.childIdx + node.getSumParentIdx();
            gbc.gridx = node.depth;

            Types.TECHNOLOGY t = node.tech;

            // Info of each research
            String effect = "<h1>" + t.toString() + "</h1>";
            for (Types.BUILDING b: Types.BUILDING.values()) {
                if (b.getTechnologyRequirement() == t) {
                    effect += "<br/>Enables building " + b.toString() + ".";
                }
            }
            for (Types.UNIT u: Types.UNIT.values()) {
                if (u.getTechnologyRequirement() == t && u.spawnable()) {
                    effect += "<br/>Enables spawning " + u.toString() + ".";
                }
            }
            for (Types.RESOURCE r : Types.RESOURCE.values()) {
                if (r.getTechnologyRequirement() == t) {
                    effect += "<br/>Enables gathering " + r.toString() + ".";
                }

            }
            for (Types.ACTION a : Types.ACTION.values()) {
                if (a.getTechnologyRequirement() == t) {
                    effect += "<br/>Enables action " + a.toString() + ".";
                }
            }
//            techEffects[i] = effect;
            String ef = effect;

            JButton button = new JButton(t.name());
            button.setBackground(Color.DARK_GRAY);
            button.addActionListener(e -> {
                infoView.setTechHighlightText(ef);
                infoView.setTechHighlight(t);
            });
            button.setOpaque(true);
            techs[i] = button;
            buttons.add(techs[i], gbc);
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
                    boolean techRequirement = tt.isResearchable(opt);
                    int starCost = opt.getCost(t.getCitiesID().size());
                    boolean starRequirement = t.getStars() >= starCost;
                    boolean researchable = techRequirement && starRequirement;
                    if (!(researchable || researched)) {
//                        techs[i].setEnabled(false);
                        techs[i].setBackground(Color.DARK_GRAY);
                        techs[i].setForeground(new Color(176, 183, 181));
                    } else {
                        if (researched) {
                            techs[i].setBackground(new Color(4, 77, 118));
                            techs[i].setForeground(new Color(128, 255, 255));
                            techs[i].setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                        } else {
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

    static class TechnologyNode {
        static HashSet<TechnologyNode> nodes;
        static int identifier = 0;

        Types.TECHNOLOGY tech;
        TechnologyNode parent;
        TechnologyNode[] children;
        int depth = -1;
        int rootIdx = -1;
        int offsetIdx = -1;
        int childIdx = 0;  // idx of this node in the children array of the parent
        int id;

        TechnologyNode(Types.TECHNOLOGY t) {
            if (nodes == null) {
                nodes = new HashSet<>();
            }

            if (t != null) {
                this.id = identifier++;
                this.tech = t;
                this.parent = findParent(t.getParentTech());
                if (this.parent != null) {
                    nodes.add(this.parent);
                }

                if (isRoot()) {
                    depth = 0;
                } else {
                    depth = parent.depth + 1;
                }

                nodes.add(this);
            }
        }

        void setChildren() {
            ArrayList<Types.TECHNOLOGY> child = tech.getChildTech();
            int nChildren = child.size();
            this.children = new TechnologyNode[nChildren];
            int idx = 0;
            for (TechnologyNode n: nodes) {
                if (child.contains(n.tech)) {
                    n.childIdx = idx;
                    children[idx++] = n;
                }
            }
        }

        TechnologyNode findParent(Types.TECHNOLOGY t) {
            if (t == null) return null;
            for (TechnologyNode n: nodes) {
                if (n.tech == t) {
                    return n;
                }
            }
            return new TechnologyNode(t);
        }

        boolean isLeaf() {
            return children.length == 0;
        }

        boolean isRoot() {
            return parent == null;
        }

        int getRootIdx(ArrayList<TechnologyNode> roots) {
            if (rootIdx == -1) {
                TechnologyNode n = this;
                while (n != null) {
                    if (n.isRoot()) {
                        rootIdx = roots.indexOf(n);
                        break;
                    } else {
                        n = n.parent;
                    }
                }
            }
            return rootIdx;
        }

        int getOffsetIdx(ArrayList<TechnologyNode> leaves) {
            if (offsetIdx == -1) {
                offsetIdx = 0;
                for (TechnologyNode l : leaves) {
                    if (l.rootIdx <= rootIdx) {
                        offsetIdx++;
                    }
                }
            }
            return offsetIdx;
        }

        int getSumParentIdx() {
            int s = 0;
            TechnologyNode node = this;
            while (node.parent != null) {
                s += node.parent.childIdx;
                node = node.parent;
            }
            return s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TechnologyNode that = (TechnologyNode) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return tech.toString();
        }
    }
}
