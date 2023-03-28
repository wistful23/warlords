package com.def.warlords.tools;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wistful23
 * @version 1.23
 */
public class MapViewer extends JFrame {

    public static void main(String[] args) throws IOException {
        new MapViewer().start();
    }

    public void start() throws IOException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Map Viewer");
        setSize(800, 600);
        getContentPane().add(new JScrollPane(new MapPanel()));
        setVisible(true);
    }

    private class MapPanel extends JPanel {

        private static final int MAP_WIDTH = 109;
        private static final int MAP_HEIGHT = 156;

        private static final int CELL_WIDTH = 7;
        private static final int CELL_HEIGHT = 7;

        private final byte[] map = new byte[2 * MAP_WIDTH * MAP_HEIGHT];

        private Point p1, p2;
        private int pos, shift;

        MapPanel() throws IOException {
            setPreferredSize(new Dimension(MAP_WIDTH * CELL_WIDTH, MAP_HEIGHT * CELL_HEIGHT));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isShiftDown()) {
                        shift = 1 - shift;
                    } else {
                        final Point p = getPoint(e);
                        if (p == null) {
                            return;
                        }
                        if (p1 == null) {
                            p1 = p;
                        } else if (p2 == null) {
                            p2 = p;
                        } else {
                            p1 = p2 = null;
                        }
                    }
                    updateTitle();
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    final Point p = getPoint(e);
                    if (p != null) {
                        pos = p.y * MAP_WIDTH + p.x;
                        updateTitle();
                    }
                }
            });
            try (final InputStream in = getClass().getResourceAsStream("/illuria.map")) {
                if (in == null) {
                    throw new FileNotFoundException("Map was not found");
                }
                final int num = in.read(map);
                if (num != map.length) {
                    throw new IOException("Incorrect map size: " + num + " != " + map.length);
                }
            }
        }

        private Point getPoint(MouseEvent e) {
            final Point p = new Point(e.getX() / CELL_WIDTH, e.getY() / CELL_HEIGHT);
            return p.x >= 0 && p.y >= 0 && p.x < MAP_WIDTH && p.y < MAP_HEIGHT ? p : null;
        }

        private void updateTitle() {
            String title = "Map Viewer | map[" + pos + "] = " + map[2 * pos + shift];
            if (p1 != null && p2 != null) {
                title += " | dist = " + Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
            }
            MapViewer.this.setTitle(title);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int y = 0; y < MAP_HEIGHT; ++y) {
                for (int x = 0; x < MAP_WIDTH; ++x) {
                    final Point p = new Point(x, y);
                    if (p.equals(p1) || p.equals(p2)) {
                        g.setColor(shift == 0 ? Color.RED : Color.GREEN);
                    } else {
                        g.setColor(new Color(map[2 * (y * MAP_WIDTH + x) + shift] * 1235));
                    }
                    g.fillRect(x * CELL_WIDTH, y * CELL_HEIGHT, CELL_WIDTH - 1, CELL_HEIGHT - 1);
                }
            }
        }
    }
}
