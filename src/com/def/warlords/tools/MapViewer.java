package com.def.warlords.tools;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

        private int shift;

        MapPanel() throws IOException {
            setPreferredSize(new Dimension(MAP_WIDTH * CELL_WIDTH, MAP_HEIGHT * CELL_HEIGHT));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    shift = 1 - shift;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    final int i = e.getY() / CELL_HEIGHT;
                    final int j = e.getX() / CELL_WIDTH;
                    if (i >= 0 && j >= 0 && i < MAP_HEIGHT && j < MAP_WIDTH) {
                        final int pos = i * MAP_WIDTH + j;
                        final int value = map[2 * pos + shift];
                        MapViewer.this.setTitle("Map Viewer | map[" + pos + "] = " + value);
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

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int i = 0; i < MAP_HEIGHT; ++i) {
                for (int j = 0; j < MAP_WIDTH; ++j) {
                    g.setColor(new Color(map[2 * (i * MAP_WIDTH + j) + shift] * 1235));
                    g.fillRect(j * CELL_WIDTH, i * CELL_HEIGHT, CELL_WIDTH - 1, CELL_HEIGHT - 1);
                }
            }
        }
    }
}
