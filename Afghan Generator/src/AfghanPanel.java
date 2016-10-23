/**
 * Panel to represent afghan with proportionally resizing squares.
 * Adapted from MadProgrammer's ChessBoardPane posted as an answer here: 
 * http://stackoverflow.com/questions/14635952/java-layout-proportions-creating-a-scalable-square-panel
 * 
 * @author MadProgrammer, adapted by Emily Hastings 
 * @version 10/23/16 
 *
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class AfghanPanel extends JPanel {
    public AfghanPanel(int length, int width, Color[][] squares) {
        setLayout(new AfghanLayoutManager());

        for (int row = 0; row < length; row++) {
            for (int col = 0; col < width; col++) {
                Color color = squares[col][row];
                add(new AfghanPanel.Cell(color), new Point(col, row));            
            }
        }
    }


    public class Cell extends JLabel {

        public Cell(Color background) {

            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            setBackground(background);
            setOpaque(true);
            setText("\t");

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(25, 25);
        }
    }

    public class AfghanLayoutManager implements LayoutManager2 {

        private Map<Point, Component> mapComps;

        public AfghanLayoutManager() {
            mapComps = new HashMap<>(25);
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
            if (constraints instanceof Point) {

                mapComps.put((Point) constraints, comp);

            } else {

                throw new IllegalArgumentException("Afghan constraints must be a Point");

            }
        }

        @Override
        public Dimension maximumLayoutSize(Container target) {
            return preferredLayoutSize(target);
        }

        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0.5f;
        }

        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        @Override
        public void invalidateLayout(Container target) {
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            Point[] keys = mapComps.keySet().toArray(new Point[mapComps.size()]);
            for (Point p : keys) {
                if (mapComps.get(p).equals(comp)) {
                    mapComps.remove(p);
                    break;
                }
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new CellGrid(mapComps).getPreferredSize();
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {
            int width = parent.getWidth();
            int height = parent.getHeight();

            int gridSize = Math.min(width, height);

            CellGrid grid = new CellGrid(mapComps);
            int rowCount = grid.getRowCount();
            int columnCount = grid.getColumnCount();

            int cellSize = gridSize / Math.max(rowCount, columnCount);

            int xOffset = (width - (cellSize * columnCount)) / 2;
            int yOffset = (height - (cellSize * rowCount)) / 2;

            Map<Integer, List<CellGrid.Cell>> cellRows = grid.getCellRows();
            for (Integer row : cellRows.keySet()) {
                List<CellGrid.Cell> rows = cellRows.get(row);
                for (CellGrid.Cell cell : rows) {
                    Point p = cell.getPoint();
                    Component comp = cell.getComponent();

                    int x = xOffset + (p.x * cellSize);
                    int y = yOffset + (p.y * cellSize);

                    comp.setLocation(x, y);
                    comp.setSize(cellSize, cellSize);

                }
            }

        }

        public class CellGrid {

            private Dimension prefSize;
            private int cellWidth;
            private int cellHeight;

            private Map<Integer, List<Cell>> mapRows;
            private Map<Integer, List<Cell>> mapCols;

            public CellGrid(Map<Point, Component> mapComps) {
                mapRows = new HashMap<>(25);
                mapCols = new HashMap<>(25);
                for (Point p : mapComps.keySet()) {
                    int row = p.y;
                    int col = p.x;
                    List<Cell> rows = mapRows.get(row);
                    List<Cell> cols = mapCols.get(col);
                    if (rows == null) {
                        rows = new ArrayList<>(25);
                        mapRows.put(row, rows);
                    }
                    if (cols == null) {
                        cols = new ArrayList<>(25);
                        mapCols.put(col, cols);
                    }
                    Cell cell = new Cell(p, mapComps.get(p));
                    rows.add(cell);
                    cols.add(cell);
                }

                int rowCount = mapRows.size();
                int colCount = mapCols.size();

                cellWidth = 0;
                cellHeight = 0;

                for (List<Cell> comps : mapRows.values()) {
                    for (Cell cell : comps) {
                        Component comp = cell.getComponent();
                        cellWidth = Math.max(cellWidth, comp.getPreferredSize().width);
                        cellHeight = Math.max(cellHeight, comp.getPreferredSize().height);
                    }
                }

                int cellSize = Math.max(cellHeight, cellWidth);

                prefSize = new Dimension(cellSize * colCount, cellSize * rowCount);
            }

            public int getRowCount() {
                return getCellRows().size();
            }

            public int getColumnCount() {
                return getCellColumns().size();
            }

            public Map<Integer, List<Cell>> getCellColumns() {
                return mapCols;
            }

            public Map<Integer, List<Cell>> getCellRows() {
                return mapRows;
            }

            public Dimension getPreferredSize() {
                return prefSize;
            }

            public int getCellHeight() {
                return cellHeight;
            }

            public int getCellWidth() {
                return cellWidth;
            }

            public class Cell {

                private Point point;
                private Component component;

                public Cell(Point p, Component comp) {
                    this.point = p;
                    this.component = comp;
                }

                public Point getPoint() {
                    return point;
                }

                public Component getComponent() {
                    return component;
                }
            }
        }
    }
}